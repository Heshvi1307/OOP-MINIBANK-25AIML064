
package service;

import model.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TransactionProcessor {

    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    private final ConcurrentHashMap<String, Account> sharedAccounts = new ConcurrentHashMap<>();

    public void addAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException(
                    "Account cannot be null.");
        }

        sharedAccounts.put(
                account.getAccountNumber(), account);
    }

    public Account getAccount(String accountNumber) {
        if (accountNumber == null) {
            return null;
        }

        return sharedAccounts.get(accountNumber);
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(sharedAccounts.values());
    }

    public ConcurrentHashMap<String, Account> getSharedAccounts() {
        return sharedAccounts;
    }

    public void submit(Runnable task) {
        if (task == null) {
            throw new IllegalArgumentException(
                    "Task cannot be null.");
        }

        executor.execute(task);
    }

    public void stop() {
        executor.shutdown();

        try {
            if (!executor.awaitTermination(
                    30, TimeUnit.SECONDS)) {

                System.out.println(
                        "Warning: tasks are taking too long to finish.");

                executor.shutdownNow();

                if (!executor.awaitTermination(
                        5, TimeUnit.SECONDS)) {

                    System.out.println(
                            "Some tasks did not terminate.");
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();

            System.out.println(
                    "Processor shutdown was interrupted.");
        }
    }
}
