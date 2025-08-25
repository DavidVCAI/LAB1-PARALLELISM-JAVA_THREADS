package edu.eci.arsw.threads;

/**
 * Main class demonstrating the behavioral differences between Thread.start() and Thread.run().
 *
 * Creates multiple CountThread instances and executes them using both methods to illustrate:
 * - Concurrent execution with start() method
 * - Sequential execution with run() method
 * - Performance timing analysis between both approaches
 *
 * @author hcadavid, David Velásquez, Jesús Pinzón
 * @version 2.0
 * @since 2025-08-18
 */
public class CountThreadsMain {

    private static final int FIRST_RANGE_START = 0;
    private static final int FIRST_RANGE_END = 99;
    private static final int SECOND_RANGE_START = 99;
    private static final int SECOND_RANGE_END = 199;
    private static final int THIRD_RANGE_START = 200;
    private static final int THIRD_RANGE_END = 299;
    private static final int SEPARATORS_NUMBER = 60;

    /**
     * Entry point demonstrating thread execution patterns and performance analysis.
     * Measures execution time for both approaches and displays comparative results to highlight
     * the performance and behavioral differences.
     *
     * @param args command line arguments (not used in this implementation)
     */
    public static void main(String args[]) {

        System.out.println("=".repeat(SEPARATORS_NUMBER));
        System.out.println("THREADING BEHAVIOR DEMONSTRATION");
        System.out.println("=".repeat(SEPARATORS_NUMBER));
        System.out.println();

        // Test 1: Concurrent execution using start()
        performConcurrentExecution();

        System.out.println("\n" + "=".repeat(SEPARATORS_NUMBER) + "\n");

        // Test 2: Sequential execution using run()
        performSequentialExecution();

        System.out.println("\n" + "=".repeat(SEPARATORS_NUMBER));
        System.out.println("DEMONSTRATION COMPLETED");
        System.out.println("=".repeat(SEPARATORS_NUMBER));
    }

    /**
     * Demonstrates concurrent thread execution using the start() method.
     *
     * Creates three CountThread instances and executes them concurrently.
     * Uses join() to wait for all threads to complete before measuring total time.
     * This approach creates actual new threads for true parallel execution.
     */
    private static void performConcurrentExecution() {
        System.out.println("-".repeat(SEPARATORS_NUMBER));
        System.out.println("TEST 1: CONCURRENT EXECUTION WITH start() METHOD");
        System.out.println("-".repeat(SEPARATORS_NUMBER));

        // Create thread instances for concurrent execution
        CountThread firstThread = new CountThread(FIRST_RANGE_START, FIRST_RANGE_END);
        CountThread secondThread = new CountThread(SECOND_RANGE_START, SECOND_RANGE_END);
        CountThread thirdThread = new CountThread(THIRD_RANGE_START, THIRD_RANGE_END);

        // Set descriptive names for thread identification
        firstThread.setName("1 [" + FIRST_RANGE_START + "-" + FIRST_RANGE_END + "]");
        secondThread.setName("2 [" + SECOND_RANGE_START + "-" + SECOND_RANGE_END + "]");
        thirdThread.setName("3 [" + THIRD_RANGE_START + "-" + THIRD_RANGE_END + "]");

        System.out.println("Starting concurrent execution...");
        System.out.println("Thread 1: Range [" + FIRST_RANGE_START + "-" + FIRST_RANGE_END + "]");
        System.out.println("Thread 2: Range [" + SECOND_RANGE_START + "-" + SECOND_RANGE_END + "]");
        System.out.println("Thread 3: Range [" + THIRD_RANGE_START + "-" + THIRD_RANGE_END + "]");
        System.out.println();

        long startTime = System.currentTimeMillis();

        // Start threads for concurrent execution
        firstThread.start();
        secondThread.start();
        thirdThread.start();

        try {
            // Wait for all threads to complete
            firstThread.join();
            secondThread.join();
            thirdThread.join();
        } catch (InterruptedException e) {
            System.err.println("Thread interruption occurred during concurrent execution: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore interrupted status
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        System.out.println("\n--- CONCURRENT EXECUTION COMPLETED ---");
        System.out.println("Total execution time with start(): " + executionTime + " ms");
        System.out.println("Execution pattern: PARALLEL (threads run concurrently)");
    }

    /**
     * Demonstrates sequential thread execution using the run() method.
     *
     * Creates three CountThread instances and executes them sequentially.
     * This approach does NOT create new threads; instead, it executes the run()
     * method directly in the main thread, resulting in sequential behavior.
     */
    private static void performSequentialExecution() {
        System.out.println("-".repeat(SEPARATORS_NUMBER));
        System.out.println("TEST 2: SEQUENTIAL EXECUTION WITH run() METHOD");
        System.out.println("-".repeat(SEPARATORS_NUMBER));

        // Create thread instances for sequential execution
        CountThread firstThread = new CountThread(FIRST_RANGE_START, FIRST_RANGE_END);
        CountThread secondThread = new CountThread(SECOND_RANGE_START, SECOND_RANGE_END);
        CountThread thirdThread = new CountThread(THIRD_RANGE_START, THIRD_RANGE_END);

        // Set descriptive names for thread identification
        firstThread.setName("SEQUENTIAL-THREAD-A[" + FIRST_RANGE_START + "-" + FIRST_RANGE_END + "]");
        secondThread.setName("SEQUENTIAL-THREAD-B[" + SECOND_RANGE_START + "-" + SECOND_RANGE_END + "]");
        thirdThread.setName("SEQUENTIAL-THREAD-C[" + THIRD_RANGE_START + "-" + THIRD_RANGE_END + "]");

        System.out.println("Starting sequential execution...");
        System.out.println("Thread A: Range [" + FIRST_RANGE_START + "-" + FIRST_RANGE_END + "]");
        System.out.println("Thread B: Range [" + SECOND_RANGE_START + "-" + SECOND_RANGE_END + "]");
        System.out.println("Thread C: Range [" + THIRD_RANGE_START + "-" + THIRD_RANGE_END + "]");
        System.out.println();

        long startTime = System.currentTimeMillis();

        // Execute run() methods sequentially (no new threads created)
        firstThread.run();
        secondThread.run();
        thirdThread.run();

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        System.out.println("\n--- SEQUENTIAL EXECUTION COMPLETED ---");
        System.out.println("Total execution time with run(): " + executionTime + " ms");
        System.out.println("Execution pattern: SEQUENTIAL (methods run in main thread)");
    }
}
