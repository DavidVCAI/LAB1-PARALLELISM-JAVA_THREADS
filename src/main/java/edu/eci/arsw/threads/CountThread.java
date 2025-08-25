package edu.eci.arsw.threads;

/**
 * Thread implementation that prints a sequence of numbers within a specified range.
 * Extends Thread class to demonstrate basic thread lifecycle and concurrent execution.
 *
 * @author hcadavid, David Velásquez, Jesús Pinzón
 * @version 2.0
 * @since 2025-08-18
 */
public class CountThread extends Thread {
    private int startNumber;
    private int endNumber;

    /**
    * Constructs a CountThread with specified number range.
    *
    * @param startNumber the initial number to print (inclusive)
    * @param endNumber the final number to print (inclusive)
    */
    public CountThread(int startNumber, int endNumber) {
        this.startNumber = startNumber;
        this.endNumber = endNumber;
    }

    /**
     * Defines the thread's execution lifecycle.
     * Prints all numbers from startNumber to endNumber inclusively.
     * Each printed number includes the current thread's name for identification.
     *
     * This method is automatically called when start() is invoked on the thread instance.
     */
    @Override
    public void run() {
        for (int i = startNumber; i <= endNumber; i++) {
        System.out.println("Thread " + Thread.currentThread().getName() + ": " + i);
        }
    }
}
