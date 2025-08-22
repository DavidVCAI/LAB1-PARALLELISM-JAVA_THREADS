/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.threads;

/**
 * 
 * @author hcadavid
 */
public class CountThreadsMain {

    public static void main(String args[]) {

        CountThread hilo1 = new CountThread(0, 99);
        CountThread hilo2 = new CountThread(99, 199);
        CountThread hilo3 = new CountThread(200, 299);

        hilo1.setName("HILO-1[0-99]");
        hilo2.setName("HILO-2[99-199]");
        hilo3.setName("HILO-3[200-299]");

        System.out.println("--- Probando con start() ---");

        long startTime = System.currentTimeMillis();

        hilo1.start();
        hilo2.start();
        hilo3.start();

        try {
            hilo1.join();
            hilo2.join();
            hilo3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("\n--- Termino! ---");
        System.out.println("Tiempo total con start(): " + (endTime - startTime) + " ms\n");

        // =====================================================

        System.out.println("--- Probando con run() ---");

        CountThread hiloA = new CountThread(0, 99);
        CountThread hiloB = new CountThread(99, 199);
        CountThread hiloC = new CountThread(200, 299);

        hiloA.setName("HILO-A[0-99]");
        hiloB.setName("HILO-B[99-199]");
        hiloC.setName("HILO-C[200-299]");

        startTime = System.currentTimeMillis();

        hiloA.run();
        hiloB.run();
        hiloC.run();

        endTime = System.currentTimeMillis();
        System.out.println("\n--- Termino! ---");
        System.out.println("Tiempo total con run(): " + (endTime - startTime) + " ms\n");

    }
}
