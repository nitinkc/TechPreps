package com.interview.algorithms;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

public class BankingSystemTest {

    @Test
    public void testTopKAndBalances() {
        BankingSystem bank = new BankingSystem();
        bank.deposit("a", 1000);
        bank.deposit("b", 1500);
        bank.deposit("c", 800);

        List<String> top2 = bank.getTopKCustomers(2);
        assertEquals(2, top2.size());
        assertTrue(top2.contains("b"));

        assertEquals(1000.0, bank.getBalance("a"), 0.0001);
    }

    @Test
    public void testConcurrentDeposits() throws InterruptedException {
        BankingSystem bank = new BankingSystem();
        int threads = 10;
        int opsPerThread = 100;
        ExecutorService svc = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            final String id = "user" + (i % 3); // three users
            svc.submit(() -> {
                for (int j = 0; j < opsPerThread; j++) {
                    bank.deposit(id, 10);
                }
                latch.countDown();
            });
        }

        latch.await();
        svc.shutdown();

        // total deposits: threads * opsPerThread * 10
        double total = threads * opsPerThread * 10;
        double sum = bank.getBalance("user0") + bank.getBalance("user1") + bank.getBalance("user2");
        assertEquals(total, sum, 0.001);
    }
}

