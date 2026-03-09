package com.interview.algorithms;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BankingSystem maintains balances and supports retrieving top-K customers.
 * This is an in-memory implementation intended for demo/testing.
 *
 * Concurrency: updates are synchronized; read methods that iterate indexes are also synchronized
 * to provide consistent views in concurrent scenarios.
 */
public class BankingSystem {
    private final Map<String, Double> balances;
    private final TreeMap<Double, Set<String>> balanceIndex;

    public BankingSystem() {
        this.balances = new ConcurrentHashMap<>();
        this.balanceIndex = new TreeMap<>(Collections.reverseOrder());
    }

    public void deposit(String customerId, double amount) {
        updateBalance(customerId, amount);
    }

    public void withdraw(String customerId, double amount) {
        updateBalance(customerId, -amount);
    }

    public synchronized double getBalance(String customerId) {
        return balances.getOrDefault(customerId, 0.0);
    }

    public synchronized List<String> getTopKCustomers(int k) {
        List<String> result = new ArrayList<>();
        int count = 0;
        for (Map.Entry<Double, Set<String>> entry : balanceIndex.entrySet()) {
            for (String customerId : entry.getValue()) {
                if (count >= k) break;
                result.add(customerId);
                count++;
            }
            if (count >= k) break;
        }
        return result;
    }

    public synchronized void payoutToExistingCustomers(double amount) {
        Set<String> existingCustomers = new HashSet<>(balances.keySet());
        for (String customerId : existingCustomers) {
            updateBalance(customerId, amount);
        }
    }

    private synchronized void updateBalance(String customerId, double amount) {
        double oldBalance = balances.getOrDefault(customerId, 0.0);
        double newBalance = oldBalance + amount;

        // Remove from old index
        if (oldBalance > 0) {
            Set<String> oldSet = balanceIndex.get(oldBalance);
            if (oldSet != null) {
                oldSet.remove(customerId);
                if (oldSet.isEmpty()) {
                    balanceIndex.remove(oldBalance);
                }
            }
        }

        // Update balance
        balances.put(customerId, newBalance);

        // Add to new index
        if (newBalance > 0) {
            balanceIndex.computeIfAbsent(newBalance, k -> new HashSet<>()).add(customerId);
        }
    }
}
