/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;
import java.util.List;

/**
 * @author hcadavid
 */
public class BlackListSearchThread extends Thread {

  private int startIndex;
  private int endIndex;
  private String ipAddress;
  private int occurrencesFound;
  private List<Integer> blackListOccurrences;
  private HostBlacklistsDataSourceFacade dataSource;

  public BlackListSearchThread(int startIndex, int endIndex, String ipAddress) {
    this.startIndex = startIndex;
    this.endIndex = endIndex;
    this.ipAddress = ipAddress;
    this.occurrencesFound = 0;
    this.blackListOccurrences = new LinkedList<>();
    this.dataSource = HostBlacklistsDataSourceFacade.getInstance();
  }

  @Override
  public void run() {
    System.out.println("Hilo " + Thread.currentThread().getName() +
        " iniciado - Revisando servidores [" + startIndex + " - " + endIndex + "]");

    for (int i = startIndex; i <= endIndex; i++) {

      if (dataSource.isInBlackListServer(i, ipAddress)) {
        blackListOccurrences.add(i);
        occurrencesFound++;

        System.out.println("Hilo " + Thread.currentThread().getName() +
            " encontró IP en blacklist #" + i);
      }
    }

    System.out.println("Hilo " + Thread.currentThread().getName() +
        " terminado - Encontradas " + occurrencesFound + " ocurrencias");
  }

  public int getOccurrencesFound() {
    return occurrencesFound;
  }

  public List<Integer> getBlackListOccurrences() {
    return blackListOccurrences;
  }

  public String getSegmentInfo() {
    return "[" + startIndex + " - " + endIndex + "]";
  }
}
