package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;
import java.util.List;

/**
 * Worker thread for searching IP addresses within a specific segment of blacklist servers.
 +
 * @author David Velásquez, Jesús Pinzón
 * @version 1.0
 * @since 2025-08-18
 */
public class BlackListSearchThread extends Thread {

    private int startIndex;
    private int endIndex;
    private String ipAddress;
    private int occurrencesFound;
    private List<Integer> blackListOccurrences;
    private HostBlacklistsDataSourceFacade dataSource;

    /**
    * Constructs a search thread for a specific server segment.
    *
    * @param startIndex the starting server index (inclusive)
    * @param endIndex the ending server index (inclusive)
    * @param ipAddress the IP address to search for
    */
    public BlackListSearchThread(int startIndex, int endIndex, String ipAddress) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.ipAddress = ipAddress;
        this.occurrencesFound = 0;
        this.blackListOccurrences = new LinkedList<>();
        this.dataSource = HostBlacklistsDataSourceFacade.getInstance();
    }

    /**
     * Executes the blacklist search within the assigned server segment.
     *
     * Iterates through servers from startIndex to endIndex, querying each for the target IP.
     * Updates occurrencesFound counter and blackListOccurrences list when matches are found.
     * Provides console output for search progress tracking.
     */
    @Override
    public void run() {
        System.out.println("Thread " + Thread.currentThread().getName() +
        "Started - Checking servers [" + startIndex + " - " + endIndex + "]");

        for (int i = startIndex; i <= endIndex; i++) {

            if (dataSource.isInBlackListServer(i, ipAddress)) {
                blackListOccurrences.add(i);
                occurrencesFound++;

                System.out.println("Thread " + Thread.currentThread().getName() +
                "Found IP on blacklist #" + i);
            }
        }

        System.out.println("Hilo " + Thread.currentThread().getName() +
            "Finished - Found " + occurrencesFound + " occurrences");
    }

    /**
     * Returns the number of blacklist occurrences found by this thread.
     * @return count of occurrences found in the assigned segment
     */
    public int getOccurrencesFound() {
        return occurrencesFound;
    }

    /**
     * Returns the list of blacklist indices where the IP was found.
     * @return list of server indices containing the target IP
     */
    public List<Integer> getBlackListOccurrences() {
        return blackListOccurrences;
    }

    /**
     * Returns a string representation of the assigned segment range.
     * @return formatted string showing the segment boundaries
     */
    public String getSegmentInfo() {
        return "[" + startIndex + " - " + endIndex + "]";
    }
}
