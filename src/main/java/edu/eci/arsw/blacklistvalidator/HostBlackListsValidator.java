package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Validator class for checking IP addresses against multiple blacklist servers.
 *
 * Provides both sequential and parallel implementations for blacklist validation.
 * Determines IP trustworthiness based on occurrence count across registered servers
 * using a configurable threshold approach.

 * @author hcadavid, David Velásquez, Jesús Pinzón
 * @version 2.1
 * @since 2025-08-24
 */
public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT = 5;
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());

    /**
     * Validates an IP address against all available blacklist servers sequentially.
     *
     * @param ipAddress the suspicious host's IP address to validate
     * @return list of blacklist server indices where the IP address was found
     * @throws IllegalArgumentException if ipAddress is null or empty
     */
    public List<Integer> checkHost(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("IP address cannot be null or empty");
        }

        LinkedList<Integer> blacklistOccurrences = new LinkedList<>();
        int occurrencesCount = 0;

        HostBlacklistsDataSourceFacade dataSource = HostBlacklistsDataSourceFacade.getInstance();
        int checkedListsCount = 0;

        LOG.log(Level.INFO, "Starting sequential validation for IP: {0}", ipAddress);

        // Sequential search with early termination
        for (int i = 0; i < dataSource.getRegisteredServersCount() && occurrencesCount < BLACK_LIST_ALARM_COUNT; i++) {
            checkedListsCount++;

            if (dataSource.isInBlackListServer(i, ipAddress)) {
                blacklistOccurrences.add(i);
                occurrencesCount++;
                LOG.log(Level.FINE, "IP {0} found in blacklist server {1}",
                    new Object[]{ipAddress, i});
            }
        }

        // Report final classification
        if (occurrencesCount >= BLACK_LIST_ALARM_COUNT) {
            dataSource.reportAsNotTrustworthy(ipAddress);
            LOG.log(Level.WARNING, "IP {0} classified as NOT TRUSTWORTHY ({1} occurrences)",
                new Object[]{ipAddress, occurrencesCount});
        } else {
            dataSource.reportAsTrustworthy(ipAddress);
            LOG.log(Level.INFO, "IP {0} classified as TRUSTWORTHY ({1} occurrences)",
                new Object[]{ipAddress, occurrencesCount});
        }

        LOG.log(Level.INFO, "Sequential validation completed. Checked {0} of {1} blacklist servers",
                new Object[]{checkedListsCount, dataSource.getRegisteredServersCount()});

        return blacklistOccurrences;
    }

    /**
     * Validates an IP address against all available blacklist servers using parallel processing.
     *
     * @param ipAddress the suspicious host's IP address to validate
     * @param threadCount the number of worker threads to use for parallel processing
     * @return list of blacklist server indices where the IP address was found
     * @throws IllegalArgumentException if ipAddress is null/empty or threadCount is invalid
     * @throws RuntimeException if thread execution is interrupted
     */
    public List<Integer> checkHost(String ipAddress, int threadCount) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("IP address cannot be null or empty");
        }
        if (threadCount <= 0) {
            throw new IllegalArgumentException("Thread count must be positive");
        }

        LOG.log(Level.INFO, "Starting parallel validation for IP: {0} using {1} threads",
            new Object[]{ipAddress, threadCount});

        System.out.println("=== PARALLEL BLACKLIST SEARCH ===");
        System.out.println("Target IP address: " + ipAddress);
        System.out.println("Number of threads: " + threadCount);
        System.out.println("Alarm threshold: " + BLACK_LIST_ALARM_COUNT + " occurrences\n");

        LinkedList<Integer> blacklistOccurrences = new LinkedList<>();
        HostBlacklistsDataSourceFacade dataSource = HostBlacklistsDataSourceFacade.getInstance();

        int totalServers = dataSource.getRegisteredServersCount();
        System.out.println("Total servers to check: " + totalServers + "\n");

        // Calculate workload distribution
        int segmentSize = totalServers / threadCount;
        int remainderServers = totalServers % threadCount;

        System.out.println("Base segment size: " + segmentSize);
        System.out.println("Remainder servers: " + remainderServers + "\n");

        BlackListSearchThread[] workerThreads = new BlackListSearchThread[threadCount];

        // Create and configure worker threads
        int currentIndex = 0;
        for (int i = 0; i < threadCount; i++) {
            int startIndex = currentIndex;
            int endIndex = currentIndex + segmentSize - 1;

            // Distribute remainder servers among first threads
            if (i < remainderServers) {
                endIndex++;
            }

            // Ensure we don't exceed total server count
            if (endIndex >= totalServers) {
                endIndex = totalServers - 1;
            }

            workerThreads[i] = new BlackListSearchThread(startIndex, endIndex, ipAddress);
            workerThreads[i].setName("BlacklistWorker-" + (i + 1));

            int serverCount = endIndex - startIndex + 1;
            System.out.println("Thread " + workerThreads[i].getName() +
                " assigned segment: [" + startIndex + " - " + endIndex + "] (" + serverCount + " servers)");

            currentIndex = endIndex + 1;
        }

        System.out.println("\n--- STARTING PARALLEL SEARCH ---");
        long startTime = System.currentTimeMillis();

        // Start all worker threads
        for (BlackListSearchThread thread : workerThreads) {
            thread.start();
        }

        // Wait for all threads to complete
        try {
            for (BlackListSearchThread thread : workerThreads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            LOG.log(Level.SEVERE, "Parallel validation interrupted for IP: " + ipAddress, e);
            Thread.currentThread().interrupt(); // Restore interrupted status
            throw new RuntimeException("Parallel validation was interrupted", e);
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        System.out.println("\n--- PARALLEL SEARCH COMPLETED ---");
        System.out.println("Total execution time: " + executionTime + " ms\n");

        // Aggregate results from all worker threads
        int totalOccurrences = 0;
        int totalCheckedLists = 0;

        System.out.println("=== RESULTS BY THREAD ===");
        for (BlackListSearchThread thread : workerThreads) {
            int threadOccurrences = thread.getOccurrencesFound();
            List<Integer> threadBlacklists = thread.getBlackListOccurrences();

            System.out.println(thread.getName() + " " + thread.getSegmentInfo() +
                    ": " + threadOccurrences + " occurrences found " + threadBlacklists);

            blacklistOccurrences.addAll(threadBlacklists);
            totalOccurrences += threadOccurrences;

            // Calculate servers processed by this thread
            String segmentInfo = thread.getSegmentInfo();
            String[] parts = segmentInfo.replace("[", "").replace("]", "").split(" - ");
            int segmentStart = Integer.parseInt(parts[0]);
            int segmentEnd = Integer.parseInt(parts[1]);
            totalCheckedLists += (segmentEnd - segmentStart + 1);
        }

        // Display final results and classification
        System.out.println("\n=== FINAL SUMMARY ===");
        System.out.println("Total occurrences found: " + totalOccurrences);
        System.out.println("Blacklists containing IP: " + blacklistOccurrences);
        System.out.println("Total lists checked: " + totalCheckedLists);

        if (totalOccurrences >= BLACK_LIST_ALARM_COUNT) {
            dataSource.reportAsNotTrustworthy(ipAddress);
            System.out.println("RESULT: HOST NOT TRUSTWORTHY (" + totalOccurrences + " >= " + BLACK_LIST_ALARM_COUNT + ")");
            LOG.log(Level.WARNING, "IP {0} classified as NOT TRUSTWORTHY ({1} occurrences) via parallel validation",
                new Object[]{ipAddress, totalOccurrences});
        } else {
            dataSource.reportAsTrustworthy(ipAddress);
            System.out.println("RESULT: HOST TRUSTWORTHY (" + totalOccurrences + " < " + BLACK_LIST_ALARM_COUNT + ")");
            LOG.log(Level.INFO, "IP {0} classified as TRUSTWORTHY ({1} occurrences) via parallel validation",
                new Object[]{ipAddress, totalOccurrences});
        }

        LOG.log(Level.INFO, "Parallel validation completed in {0}ms. Checked {1} of {2} blacklist servers",
                new Object[]{executionTime, totalCheckedLists, dataSource.getRegisteredServersCount()});

        return blacklistOccurrences;
    }
}
