package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import model.Account;
import model.SavingsAccount;
import model.CurrentAccount;
import model.FixedDepositAccount;
import exception.BankException;
import exception.InvalidAmountException;

public class MiniBank {
        private static final Scanner sc = new Scanner(System.in);
        private static final List<Account> accounts = new ArrayList<>();

        public static void main(String[] args) {
                accounts.add(new SavingsAccount("Alice", 5000, 1000));
                accounts.add(new CurrentAccount("Bob", 10000, 2000));
                accounts.add(new FixedDepositAccount("Charlie", 15000));

                boolean running = true;
                System.out.println("================================");
                System.out.println("         MINIBANK SYSTEM");
                System.out.println("================================");

                while (running) {
                        System.out.println("\n1. Open Account");
                        System.out.println("2. Deposit Funds");
                        System.out.println("3. Withdraw Funds");
                        System.out.println("4. Transfer Funds");
                        System.out.println("5. View All Accounts");
                        System.out.println("6. Create Multiple Accounts");
                        System.out.println("7. Concurrency Demonstration");
                        System.out.println("8. Transaction Processing");
                        System.out.println("9. Exit");

                        switch (readInt("Enter your choice: ")) {
                                case 1 -> openAccount();
                                case 2 -> depositMoney();
                                case 3 -> withdrawMoney();
                                case 4 -> transferMoney();
                                case 5 -> viewAccounts();
                                case 6 -> createMultipleAccounts();
                                case 7 -> runConcurrencyDemo();
                                case 8 -> runTransactionProcessing();
                                case 9 -> {
                                        running = false;
                                        System.out.println("Thank you for using MiniBank.");
                                }
                                default -> System.out.println("Invalid choice. Please try again.");
                        }
                }
                sc.close();
        }

        private static void openAccount() {
                System.out.println("\n--- Open Account ---");
                Account account = createAccountFromInput();
                if (account != null) {
                        accounts.add(account);
                        System.out.println("Account created successfully.");
                        System.out.println(account);
                }
        }

        private static Account createAccountFromInput() {
                System.out.println("\nSelect account type:");
                System.out.println("1. Savings Account");
                System.out.println("2. Current Account");
                System.out.println("3. Fixed Deposit Account");

                int type = readInt("Enter account type: ");
                if (type < 1 || type > 3) {
                        System.out.println("Invalid account type.");
                        return null;
                }

                System.out.print("Enter owner name: ");
                String name = sc.nextLine().trim();
                if (name.isEmpty()) {
                        System.out.println("Owner name cannot be empty.");
                        return null;
                }

                long balance = readLong("Enter opening balance: ");
                if (balance < 0) {
                        System.out.println("Opening balance cannot be negative.");
                        return null;
                }

                if (type == 1) {
                        long minBalance = readLong("Enter minimum balance: ");
                        if (minBalance < 0) {
                                System.out.println("Minimum balance cannot be negative.");
                                return null;
                        }
                        return new SavingsAccount(name, balance, minBalance);
                }

                if (type == 2) {
                        long overdraftLimit = readLong("Enter overdraft limit: ");
                        if (overdraftLimit < 0) {
                                System.out.println("Overdraft limit cannot be negative.");
                                return null;
                        }
                        return new CurrentAccount(name, balance, overdraftLimit);
                }

                return new FixedDepositAccount(name, balance);
        }

        private static void createMultipleAccounts() {
                int n = readInt("Enter number of accounts to create: ");
                if (n <= 0) {
                        System.out.println("Number of accounts must be positive.");
                        return;
                }

                List<Account> created = new ArrayList<>();
                for (int i = 0; i < n; i++) {
                        System.out.println("\nAccount " + (i + 1) + " of " + n);
                        Account account = createAccountFromInput();

                        if (account != null) {
                                accounts.add(account);
                                created.add(account);
                                System.out.println("Account created successfully.");
                        } else {
                                System.out.println("Account skipped.");
                        }
                }

                System.out.println("\n--- Account Interest Summary ---");
                for (Account account : created) {
                        System.out.println(account);
                        System.out.println("Interest rate: " + account.interestRate() + "%");
                        System.out.println("Yearly interest: Rs." + account.yearlyInterest());
                }
        }

        private static void viewAccounts() {
                System.out.println("\n--- Account Summary ---");
                if (accounts.isEmpty()) {
                        System.out.println("No accounts available.");
                        return;
                }

                for (Account account : accounts) {
                        System.out.println("----------------------------");
                        System.out.println(account);
                        System.out.println("Interest rate: " + account.interestRate() + "%");
                        System.out.println("Yearly interest: Rs." + account.yearlyInterest());
                }
        }

        private static void depositMoney() {
                Account account = askForAccount();
                if (account == null)
                        return;

                long amount = readLong("Enter deposit amount: ");
                try {
                        account.deposit(amount);
                        System.out.println("Deposit successful.");
                        System.out.println("Updated balance: Rs." + account.getBalance());
                } catch (InvalidAmountException | IllegalStateException e) {
                        System.out.println("Deposit failed: " + e.getMessage());
                }
        }

        private static void withdrawMoney() {
                Account account = askForAccount();
                if (account == null)
                        return;

                long amount = readLong("Enter withdrawal amount: ");
                try {
                        if (account.withdraw(amount)) {
                                System.out.println("Withdrawal successful.");
                                System.out.println("Updated balance: Rs." + account.getBalance());
                        }
                } catch (Exception e) {
                        System.out.println("Withdrawal failed: " + e.getMessage());
                }
        }

        private static void transferMoney() {
                System.out.println("Enter sender account details:");
                Account sender = askForAccount();
                if (sender == null)
                        return;

                System.out.println("Enter receiver account details:");
                Account receiver = askForAccount();
                if (receiver == null)
                        return;

                if (sender == receiver) {
                        System.out.println("Sender and receiver must be different accounts.");
                        return;
                }

                long amount = readLong("Enter transfer amount: ");
                try {
                        sender.transfer(receiver, amount);
                        System.out.println("Sender balance: Rs." + sender.getBalance());
                        System.out.println("Receiver balance: Rs." + receiver.getBalance());
                } catch (BankException | IllegalStateException e) {
                        System.out.println("Transfer failed: " + e.getMessage());
                }
        }

        private static Account askForAccount() {
                System.out.print("Enter account number: ");
                Account account = findAccount(sc.nextLine().trim());
                if (account == null) {
                        System.out.println("Account not found.");
                }
                return account;
        }

        private static Account findAccount(String accountNumber) {
                for (Account account : accounts) {
                        if (account.getAccountNumber().equalsIgnoreCase(accountNumber)) {
                                return account;
                        }
                }
                return null;
        }

        private static void runConcurrencyDemo() {
                System.out.println("\n--- Concurrent Deposit Test ---");
                long startingBalance = readLong("Enter starting balance: ");
                int threadCount = readInt("Enter number of threads: ");
                int depositsPerThread = readInt("Enter deposits per thread: ");
                long amount = readLong("Enter amount per deposit: ");

                if (startingBalance < 0 || threadCount <= 0
                                || depositsPerThread <= 0 || amount <= 0) {
                        System.out.println("Please enter valid positive values.");
                        return;
                }

                Account account = new SavingsAccount("ConcurrencyTest", startingBalance, 0);
                Thread[] threads = new Thread[threadCount];
                long expected = startingBalance
                                + (long) threadCount * depositsPerThread * amount;

                for (int i = 0; i < threadCount; i++) {
                        threads[i] = new Thread(
                                        new AccountWorker(account, depositsPerThread, amount),
                                        "Deposit-Thread-" + (i + 1));
                        threads[i].start();
                }

                for (Thread thread : threads) {
                        try {
                                thread.join();
                        } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                System.out.println("Operation interrupted.");
                                return;
                        }
                }

                System.out.println("Expected balance: Rs." + expected);
                System.out.println("Actual balance: Rs." + account.getBalance());
                System.out.println(expected == account.getBalance()
                                ? "All deposits completed correctly."
                                : "Balance mismatch detected.");
        }

        private static void runTransactionProcessing() {
                System.out.println("\n--- Transaction Processing ---");
                System.out.println("Select the account to process transactions for:");
                Account account = askForAccount();
                if (account == null)
                        return;

                long startingBalance = account.getBalance();
                System.out.println("Selected: " + account.getAccountNumber()
                                + " | Owner: " + account.getOwnerName());
                System.out.println("Current balance: Rs." + startingBalance);

                int count = readInt("Enter number of deposit-withdrawal pairs: ");
                long amount = readLong("Enter amount per transaction: ");

                if (count <= 0 || amount <= 0) {
                        System.out.println("Transaction count and amount must be positive.");
                        return;
                }

                Map<String, Integer> taskCounts = new ConcurrentHashMap<>();
                AtomicInteger successfulDeposits = new AtomicInteger();
                AtomicInteger successfulWithdrawals = new AtomicInteger();
                AtomicInteger failedTransactions = new AtomicInteger();

                TransactionProcessor processor = new TransactionProcessor();

                for (int i = 0; i < count; i++) {
                        processor.submit(() -> {
                                taskCounts.merge(Thread.currentThread().getName(), 1, Integer::sum);
                                try {
                                        account.deposit(amount);
                                        successfulDeposits.incrementAndGet();
                                } catch (Exception e) {
                                        failedTransactions.incrementAndGet();
                                        System.out.println("Deposit failed: " + e.getMessage());
                                }
                        });

                        processor.submit(() -> {
                                taskCounts.merge(Thread.currentThread().getName(), 1, Integer::sum);
                                try {
                                        account.withdraw(amount);
                                        successfulWithdrawals.incrementAndGet();
                                } catch (Exception e) {
                                        failedTransactions.incrementAndGet();
                                        System.out.println("Withdrawal failed: " + e.getMessage());
                                }
                        });
                }

                processor.stop();

                System.out.println("\n--- Batch Results ---");
                System.out.println("Starting balance: Rs." + startingBalance);
                System.out.println("Successful deposits: " + successfulDeposits.get());
                System.out.println("Successful withdrawals: " + successfulWithdrawals.get());
                System.out.println("Failed transactions: " + failedTransactions.get());
                System.out.println("Actual balance: Rs." + account.getBalance());
                System.out.println("Tasks per worker: " + taskCounts);

                System.out.println("\n--- Producer-Consumer Processing ---");
                int bufferCount = readInt("Enter number of deposit transactions: ");
                long bufferAmount = readLong("Enter deposit amount: ");

                if (bufferCount <= 0 || bufferAmount <= 0) {
                        System.out.println("Transaction count and amount must be positive.");
                        return;
                }

                TransactionBuffer buffer = new TransactionBuffer(3);
                Runnable stopSignal = () -> {
                };

                Thread producer = new Thread(() -> {
                        try {
                                for (int i = 0; i < bufferCount; i++) {
                                        buffer.put(() -> {
                                                try {
                                                        account.deposit(bufferAmount);
                                                        System.out.println("Deposit processed: Rs." + bufferAmount);
                                                } catch (Exception e) {
                                                        System.out.println("Deposit failed: " + e.getMessage());
                                                }
                                        });
                                }
                                buffer.put(stopSignal);
                        } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                        }
                }, "Producer");

                Thread consumer = new Thread(() -> {
                        try {
                                while (true) {
                                        Runnable task = buffer.take();
                                        if (task == stopSignal)
                                                break;
                                        task.run();
                                }
                        } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                        }
                }, "Consumer");

                producer.start();
                consumer.start();

                try {
                        producer.join();
                        consumer.join();
                } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.out.println("Processing interrupted.");
                        return;
                }

                System.out.println("Balance after producer-consumer: Rs."
                                + account.getBalance());

                System.out.println("\n--- Deadlock Demonstration ---");
                DeadlockDemo.runDemo();
        }

        private static int readInt(String message) {
                while (true) {
                        System.out.print(message);
                        try {
                                return Integer.parseInt(sc.nextLine().trim());
                        } catch (NumberFormatException e) {
                                System.out.println("Please enter a valid whole number.");
                        }
                }
        }

        private static long readLong(String message) {
                while (true) {
                        System.out.print(message);
                        try {
                                return Long.parseLong(sc.nextLine().trim());
                        } catch (NumberFormatException e) {
                                System.out.println("Please enter a valid whole number.");
                        }
                }
        }
}