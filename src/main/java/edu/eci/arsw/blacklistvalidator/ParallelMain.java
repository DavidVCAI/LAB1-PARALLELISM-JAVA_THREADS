/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import java.util.List;

/**
 * @author hcadavid
 */
public class ParallelMain {

  public static void main(String[] args) {

    HostBlackListsValidator hblv = new HostBlackListsValidator();

    System.out.println("=== TEST 1: IP no tan dispersa 200.24.34.55 ===");
    long startTime = System.currentTimeMillis();
    List<Integer> result1 = hblv.checkHost("200.24.34.55", 4);
    long endTime = System.currentTimeMillis();
    System.out.println("Tiempo total TEST 1: " + (endTime - startTime) + " ms");
    System.out.println("Resultado: " + result1 + "\n");

    System.out.println("==========================================\n");

    System.out.println("=== TEST 2: IP mas dispersa (202.24.34.55) ===");
    startTime = System.currentTimeMillis();
    List<Integer> result2 = hblv.checkHost("202.24.34.55", 4);
    endTime = System.currentTimeMillis();
    System.out.println("Tiempo total TEST 2: " + (endTime - startTime) + " ms");
    System.out.println("Resultado: " + result2 + "\n");

    System.out.println("==========================================\n");

    System.out.println("=== TEST 3: IP que no esta en ninguna lista (212.24.24.55) ===");
    startTime = System.currentTimeMillis();
    List<Integer> result3 = hblv.checkHost("212.24.24.55", 8);
    endTime = System.currentTimeMillis();
    System.out.println("Tiempo total TEST 3: " + (endTime - startTime) + " ms");
    System.out.println("Resultado: " + result3 + "\n");

    System.out.println("==========================================\n");

    System.out.println("=== TEST 4: COMPARACIÓN DE RENDIMIENTO ===");
    String testIP = "202.24.34.55";

    int[] threadCounts = { 1, 2, 4, 8 };

    for (int threads : threadCounts) {
      System.out.println("--- Probamos con " + threads + " hilo(s) ---");
      startTime = System.currentTimeMillis();
      List<Integer> result = hblv.checkHost(testIP, threads);
      endTime = System.currentTimeMillis();
      System.out.println("Tiempo con " + threads + " hilo(s): " + (endTime - startTime) + " ms");
      System.out.println("Ocurrencias encontradas: " + result.size() + "\n");
    }

  }
}
