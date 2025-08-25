package edu.eci.arsw.blacklistvalidator;

import java.util.List;

/**
 * Entry point for parallel blacklist validation demonstration and performance testing.
 *
 * This class executes four distinct test scenarios:
 * - Less dispersed IP validation to test early detection performance
 * - More dispersed IP validation to test full search capabilities
 * - Clean IP validation to test negative case performance
 * - Performance comparison across multiple thread configurations
 *
 * Each test measures execution time and provides detailed analysis of results
 * including occurrence counts and performance metrics for educational purposes.
 *
 * @author hcadavid, David Velásquez, Jesús Pinzón
 * @version 2.0
 * @since 2025-08-24
 */
public class ParallelMain {

    private static final String LESS_DISPERSED_IP = "200.24.34.55";
    private static final String MORE_DISPERSED_IP = "202.24.34.55";
    private static final String CLEAN_IP = "212.24.24.55";

    private static final int[] PERFORMANCE_TEST_THREADS = {1, 2, 4, 8};

    private static final int SEPARATORS_NUMBER = 60;

    /**
     * Executes comprehensive parallel blacklist validation tests and performance analysis.
     *
     * Performs four sequential test scenarios to demonstrate different aspects of the parallel validation system:
     *
     * 1. Less dispersed IP test - demonstrates early detection scenarios
     * 2. More dispersed IP test - demonstrates comprehensive search scenarios
     * 3. Clean IP test - demonstrates negative validation scenarios
     * 4. Performance comparison - analyzes scaling behavior across thread counts
     *
     * Each test includes timing measurements and detailed result analysis.
     *
     * @param args command line arguments (not used in this implementation)
     */
    public static void main(String[] args) {

        HostBlackListsValidator validator = new HostBlackListsValidator();

        System.out.println("=".repeat(SEPARATORS_NUMBER));
        System.out.println("PARALLEL BLACKLIST VALIDATION DEMONSTRATION");
        System.out.println("=".repeat(SEPARATORS_NUMBER) + "\n");

        // Test 1: Less dispersed IP validation
        performLessDispersedIPTest(validator);

        printSeparator();

        // Test 2: More dispersed IP validation
        performMoreDispersedIPTest(validator);

        printSeparator();

        // Test 3: Clean IP validation
        performCleanIPTest(validator);

        printSeparator();

        // Test 4: Performance comparison
        performPerformanceComparison(validator);

        System.out.println("=".repeat(SEPARATORS_NUMBER));
        System.out.println("\nDEMONSTRATION COMPLETED");
        System.out.println("=".repeat(SEPARATORS_NUMBER));
    }

    /**
     * Tests validation performance with a less dispersed IP address.
     * This IP appears in blacklists with lower indices, demonstrating
     * scenarios where malicious IPs can be detected relatively quickly.
     *
     * @param validator the blacklist validator instance to use for testing
     */
    private static void performLessDispersedIPTest(HostBlackListsValidator validator) {
        System.out.println("=== TEST 1: LESS DISPERSED IP VALIDATION ===");
        System.out.println("Target IP: " + LESS_DISPERSED_IP);
        System.out.println("Expected: Early detection in lower-indexed servers");
        System.out.println("Thread count: 4\n");

        long startTime = System.currentTimeMillis();
        List<Integer> result = validator.checkHost(LESS_DISPERSED_IP, 4);
        long endTime = System.currentTimeMillis();

        long executionTime = endTime - startTime;
        System.out.println("\nTEST 1 RESULTS:");
        System.out.println("Total execution time: " + executionTime + " ms");
        System.out.println("Blacklists found: " + result);
        System.out.println("Occurrence count: " + result.size());
    }

    /**
     * Tests validation performance with a more dispersed IP address.
     * This IP appears in blacklists spread across higher indices,
     * demonstrating comprehensive search scenarios.
     *
     * @param validator the blacklist validator instance to use for testing
     */
    private static void performMoreDispersedIPTest(HostBlackListsValidator validator) {
        System.out.println("=== TEST 2: MORE DISPERSED IP VALIDATION ===");
        System.out.println("Target IP: " + MORE_DISPERSED_IP);
        System.out.println("Expected: Occurrences distributed across server range");
        System.out.println("Thread count: 4\n");

        long startTime = System.currentTimeMillis();
        List<Integer> result = validator.checkHost(MORE_DISPERSED_IP, 4);
        long endTime = System.currentTimeMillis();

        long executionTime = endTime - startTime;
        System.out.println("\nTEST 2 RESULTS:");
        System.out.println("Total execution time: " + executionTime + " ms");
        System.out.println("Blacklists found: " + result);
        System.out.println("Occurrence count: " + result.size());
    }

    /**
     * Tests validation performance with a clean IP address.
     * This IP does not appear in any blacklist, demonstrating
     * negative validation scenarios and full search completion.
     *
     * @param validator the blacklist validator instance to use for testing
     */
    private static void performCleanIPTest(HostBlackListsValidator validator) {
        System.out.println("=== TEST 3: CLEAN IP VALIDATION ===");
        System.out.println("Target IP: " + CLEAN_IP);
        System.out.println("Expected: No occurrences found (trustworthy result)");
        System.out.println("Thread count: 8\n");

        long startTime = System.currentTimeMillis();
        List<Integer> result = validator.checkHost(CLEAN_IP, 8);
        long endTime = System.currentTimeMillis();

        long executionTime = endTime - startTime;
        System.out.println("\nTEST 3 RESULTS:");
        System.out.println("Total execution time: " + executionTime + " ms");
        System.out.println("Blacklists found: " + result);
        System.out.println("Occurrence count: " + result.size());
    }

    /**
     * Performs performance comparison testing across multiple thread configurations.
     * Uses the more dispersed IP to ensure consistent workload across all tests
     * and demonstrates scaling behavior of the parallel validation system.
     *
     * @param validator the blacklist validator instance to use for testing
     */
    private static void performPerformanceComparison(HostBlackListsValidator validator) {
        System.out.println("=== TEST 4: PERFORMANCE COMPARISON ===");
        System.out.println("Target IP: " + MORE_DISPERSED_IP);
        System.out.println("Testing thread configurations: 1, 2, 4, 8");
        System.out.println("Purpose: Analyze parallel processing scaling\n");

        System.out.println("PERFORMANCE RESULTS:");
        System.out.println("Thread Count | Execution Time | Occurrences");
        System.out.println("-------------|----------------|------------");

        for (int threadCount : PERFORMANCE_TEST_THREADS) {
            long startTime = System.currentTimeMillis();
            List<Integer> result = validator.checkHost(MORE_DISPERSED_IP, threadCount);
            long endTime = System.currentTimeMillis();

            long executionTime = endTime - startTime;
            System.out.printf("%12d | %14d ms | %11d%n",
                threadCount, executionTime, result.size());
        }
    }

    /**
     * Prints a visual separator between test sections for improved readability.
     */
    private static void printSeparator() {
        System.out.println("\n" + "=".repeat(SEPARATORS_NUMBER) + "\n");
    }
}
