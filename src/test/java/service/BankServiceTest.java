
package service;

import exception.BankException;
import model.Account;
import model.SavingsAccount;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class BankServiceTest {

    @Test
    void depositIncreasesBalance() throws Exception {
        BankService bank = new BankService();
        Account account = new SavingsAccount("Test User", 5000, 0);
        bank.registerAccount(account);

        bank.deposit(account.getAccountNumber(), 1000);

        assertEquals(6000, account.getBalance());
    }

    @Test
    void withdrawalBeyondAvailableBalanceFails() throws Exception {
        BankService bank = new BankService();
        Account account = new SavingsAccount("Test User", 5000, 0);
        bank.registerAccount(account);

        assertThrows(
                BankException.class,
                () -> bank.withdraw(account.getAccountNumber(), 6000));

        assertEquals(5000, account.getBalance());
    }

    @Test
    void concurrentDepositsProduceCorrectBalance() throws Exception {
        BankService bank = new BankService();
        Account account = new SavingsAccount("Test User", 1000, 0);
        bank.registerAccount(account);

        int threadCount = 10;
        int depositsPerThread = 100;
        long amount = 10;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        try {
            List<Future<?>> tasks = new ArrayList<>();

            for (int i = 0; i < threadCount; i++) {
                tasks.add(executor.submit(() -> {
                    for (int j = 0; j < depositsPerThread; j++) {
                        try {
                            bank.deposit(account.getAccountNumber(), amount);
                        } catch (BankException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }));
            }

            for (Future<?> task : tasks) {
                task.get();
            }
        } finally {
            executor.shutdown();
        }

        long expectedBalance = 1000L + (long) threadCount * depositsPerThread * amount;

        assertEquals(expectedBalance, account.getBalance());
    }
}
