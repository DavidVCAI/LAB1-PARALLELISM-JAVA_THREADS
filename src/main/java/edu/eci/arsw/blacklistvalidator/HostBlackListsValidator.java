/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT = 5;

    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * 
     * @param ipaddress suspicious host's IP address.
     * @return Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress) {

        LinkedList<Integer> blackListOcurrences = new LinkedList<>();

        int ocurrencesCount = 0;

        HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();

        int checkedListsCount = 0;

        for (int i = 0; i < skds.getRegisteredServersCount() && ocurrencesCount < BLACK_LIST_ALARM_COUNT; i++) {
            checkedListsCount++;

            if (skds.isInBlackListServer(i, ipaddress)) {

                blackListOcurrences.add(i);

                ocurrencesCount++;
            }
        }

        if (ocurrencesCount >= BLACK_LIST_ALARM_COUNT) {
            skds.reportAsNotTrustworthy(ipaddress);
        } else {
            skds.reportAsTrustworthy(ipaddress);
        }

        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}",
                new Object[] { checkedListsCount, skds.getRegisteredServersCount() });

        return blackListOcurrences;
    }

    /**
     * Check the given host's IP address in all the available black lists using N
     * threads,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * 
     * @param ipaddress suspicious host's IP address.
     * @param N         number of threads to use for parallel search
     * @return Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress, int N) {

        System.out.println("=== BÚSQUEDA PARALELA DE BLACKLIST ===");
        System.out.println("IP a buscar: " + ipaddress);
        System.out.println("Número de hilos: " + N);
        System.out.println("Umbral de alarma: " + BLACK_LIST_ALARM_COUNT + " ocurrencias\n");

        LinkedList<Integer> blackListOccurrences = new LinkedList<>();
        HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();

        int totalServers = skds.getRegisteredServersCount();
        System.out.println("Total de servidores a revisar: " + totalServers + "\n");

        int segmentSize = totalServers / N;
        int remainder = totalServers % N;

        System.out.println("Tamaño base del segmento: " + segmentSize);
        System.out.println("Servidores restantes: " + remainder + "\n");

        BlackListSearchThread[] threads = new BlackListSearchThread[N];

        int currentIndex = 0;
        for (int i = 0; i < N; i++) {
            int startIndex = currentIndex;
            int endIndex = currentIndex + segmentSize - 1;

            if (i < remainder) {
                endIndex++;
            }

            if (endIndex >= totalServers) {
                endIndex = totalServers - 1;
            }

            threads[i] = new BlackListSearchThread(startIndex, endIndex, ipaddress);
            threads[i].setName("SearchThread-" + (i + 1));

            System.out.println("Hilo " + threads[i].getName() + " asignado segmento: [" +
                    startIndex + " - " + endIndex + "] (" + (endIndex - startIndex + 1) + " servidores)");

            currentIndex = endIndex + 1;
        }

        System.out.println("\n--- INICIANDO BÚSQUEDA PARALELA ---");
        long startTime = System.currentTimeMillis();

        // Iniciar todos los hilos
        for (BlackListSearchThread thread : threads) {
            thread.start();
        }

        try {
            for (BlackListSearchThread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("\n--- BÚSQUEDA PARALELA COMPLETADA ---");
        System.out.println("Tiempo total: " + (endTime - startTime) + " ms\n");

        int totalOccurrences = 0;
        int totalCheckedLists = 0;

        System.out.println("=== RESULTADOS POR HILO ===");
        for (BlackListSearchThread thread : threads) {
            int occurrences = thread.getOccurrencesFound();
            List<Integer> threadOccurrences = thread.getBlackListOccurrences();

            System.out.println(thread.getName() + " " + thread.getSegmentInfo() +
                    ": " + occurrences + " ocurrencias encontradas " + threadOccurrences);

            blackListOccurrences.addAll(threadOccurrences);
            totalOccurrences += occurrences;

            String segmentInfo = thread.getSegmentInfo();
            String[] parts = segmentInfo.replace("[", "").replace("]", "").split(" - ");
            int start = Integer.parseInt(parts[0]);
            int end = Integer.parseInt(parts[1]);
            totalCheckedLists += (end - start + 1);
        }

        System.out.println("\n=== RESUMEN FINAL ===");
        System.out.println("Total de ocurrencias encontradas: " + totalOccurrences);
        System.out.println("Blacklists donde se encontró: " + blackListOccurrences);
        System.out.println("Total de listas revisadas: " + totalCheckedLists);

        if (totalOccurrences >= BLACK_LIST_ALARM_COUNT) {
            skds.reportAsNotTrustworthy(ipaddress);
            System.out.println(
                    "RESULTADO: HOST NO CONFIABLE (" + totalOccurrences + " >= " + BLACK_LIST_ALARM_COUNT + ")");
        } else {
            skds.reportAsTrustworthy(ipaddress);
            System.out.println("RESULTADO: HOST CONFIABLE (" + totalOccurrences + " < " + BLACK_LIST_ALARM_COUNT + ")");
        }

        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}",
                new Object[] { totalCheckedLists, skds.getRegisteredServersCount() });

        return blackListOccurrences;
    }

    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());

}
