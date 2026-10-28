
package service;

import model.Account;
import model.SavingsAccount;

import java.util.concurrent.CountDownLatch;

public class DeadlockDemo {

    public static void runDemo() {
        System.out.println("\n--- Deadlock Demonstration ---");

        Account a = new SavingsAccount("Account A", 5000, 1000);
        Account b = new SavingsAccount("Account B", 5000, 1000);

        CountDownLatch bothLocked = new CountDownLatch(2);

        Thread t1 = new Thread(() -> {
            synchronized (a) {
                System.out.println("Thread 1 locked Account A");
                bothLocked.countDown();
                awaitBoth(bothLocked);

                synchronized (b) {
                    System.out.println("Thread 1 locked Account B");
                }
            }
        }, "Deadlock-Thread-1");

        Thread t2 = new Thread(() -> {
            synchronized (b) {
                System.out.println("Thread 2 locked Account B");
                bothLocked.countDown();
                awaitBoth(bothLocked);

                synchronized (a) {
                    System.out.println("Thread 2 locked Account A");
                }
            }
        }, "Deadlock-Thread-2");

        t1.setDaemon(true);
        t2.setDaemon(true);

        t1.start();
        t2.start();

        try {
            t1.join(1000);
            t2.join(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        if (t1.isAlive() && t2.isAlive()) {
            System.out.println(
                    "Deadlock detected: both threads are waiting.");
        }

        System.out.println("\n--- Fixed Lock Ordering ---");

        Account c = new SavingsAccount("Account C", 5000, 1000);
        Account d = new SavingsAccount("Account D", 5000, 1000);

        Thread t3 = new Thread(() -> safeTransfer(c, d, 500));
        Thread t4 = new Thread(() -> safeTransfer(d, c, 300));

        t3.start();
        t4.start();

        try {
            t3.join();
            t4.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Account C balance: " + c.getBalance());
        System.out.println("Account D balance: " + d.getBalance());
        System.out.println("Fixed transfers completed.");
    }

    private static void awaitBoth(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void safeTransfer(
            Account from, Account to, long amount) {

        Account first;
        Account second;

        if (from.getAccountNumber().compareTo(
                to.getAccountNumber()) < 0) {
            first = from;
            second = to;
        } else {
            first = to;
            second = from;
        }

        synchronized (first) {
            synchronized (second) {
                try {
                    from.withdraw(amount);
                    to.deposit(amount);
                    System.out.println(
                            Thread.currentThread().getName()
                                    + " transferred " + amount);
                } catch (Exception e) {
                    System.out.println(
                            "Transfer failed: " + e.getMessage());
                }
            }
        }
    }
}
