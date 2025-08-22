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
public class CountThread extends Thread {

  private int startNumber;
  private int endNumber;

  /**
   * Inicializa el rango de números a imprimir
   * 
   * @param startNumber Número inicial (A)
   * @param endNumber   Número final (B)
   */
  public CountThread(int startNumber, int endNumber) {
    this.startNumber = startNumber;
    this.endNumber = endNumber;
  }

  /**
   * Método que define el ciclo de vida del hilo
   * Imprime todos los números desde startNumber hasta endNumber (es inclusivo)
   */
  @Override
  public void run() {
    for (int i = startNumber; i <= endNumber; i++) {
      System.out.println("Hilo " + Thread.currentThread().getName() + ": " + i);
    }
  }
}
