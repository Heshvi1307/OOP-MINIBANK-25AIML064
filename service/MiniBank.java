
package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

                // Preloaded demo accounts
                accounts.add(new SavingsAccount("Alice", 5000, 1000));
                accounts.add(new CurrentAccount("Bob", 10000, 2000));
                accounts.add(new FixedDepositAccount("Charlie", 15000));

                boolean running = true;

                System.out.println("================================");
                System.out.println("       WELCOME TO MINIBANK");
                System.out.println("================================");

                while (running) {

                        System.out.println("\nPlease select an option:");
                        System.out.println("1. Open Account");
                        System.out.println("2. Deposit");
                        System.out.println("3. Withdraw");
                        System.out.println("4. Transfer");
                        System.out.println("5. View All Accounts");
                        System.out.println("6. Create Multiple Accounts & Calculate Interest");
                        System.out.println("7. Lab 09 - Multithreading Demo");
                        System.out.println("8. Exit");

                        int choice = readInt("Enter your choice: ");

                        switch (choice) {
                                case 1:
                                        openAccount();
                                        break;

                                case 2:
                                        depositMoney();
                                        break;

                                case 3:
                                        withdrawMoney();
                                        break;

                                case 4:
                                        transferMoney();
                                        break;

                                case 5:
                                        viewAccounts();
                                        break;

                                case 6:
                                        createMultipleAccounts();
                                        break;

                                case 7:
                                        runLab09Demo();
                                        break;

                                case 8:
                                        running = false;
                                        System.out.println("Thank you for using MiniBank!");
                                        break;

                                default:
                                        System.out.println("Invalid choice. Please try again.");
                        }
                }

                sc.close();
        }

        // Open a single account
        private static void openAccount() {

                System.out.println("\n--- Open Account ---");

                Account account = createAccountFromInput();

                if (account != null) {
                        accounts.add(account);

                        System.out.println("Account created successfully!");
                        System.out.println(account);
                }
        }

        // Create an account using interactive input
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

                } else if (type == 2) {

                        long overdraftLimit = readLong("Enter overdraft limit: ");

                        if (overdraftLimit < 0) {
                                System.out.println("Overdraft limit cannot be negative.");
                                return null;
                        }

                        return new CurrentAccount(
                                        name, balance, overdraftLimit);

                } else {

                        return new FixedDepositAccount(name, balance);
                }
        }

        // Create multiple accounts and calculate interest
        private static void createMultipleAccounts() {

                System.out.println("\n--- Multiple Account Creation ---");

                int n = readInt("Enter number of accounts to create: ");

                if (n <= 0) {
                        System.out.println("Number of accounts must be positive.");
                        return;
                }

                List<Account> newAccounts = new ArrayList<>();

                for (int i = 0; i < n; i++) {

                        System.out.println("\nAccount " + (i + 1) + " of " + n);

                        Account account = createAccountFromInput();

                        if (account != null) {
                                accounts.add(account);
                                newAccounts.add(account);
                                System.out.println("Account created successfully!");
                        } else {
                                System.out.println("Account was not created.");
                                i--;
                        }
                }

                System.out.println("\n--- Interest Calculation Results ---");

                for (Account account : newAccounts) {

                        System.out.println("\n" + account);

                        System.out.println(
                                        "Interest rate: " + account.interestRate() + "%");

                        System.out.println(
                                        "Yearly interest: Rs." + account.yearlyInterest());

                        System.out.println(
                                        "Current balance: Rs." + account.getBalance());
                }
        }

        // Display all accounts
        private static void viewAccounts() {

                System.out.println("\n--- All Accounts ---");

                if (accounts.isEmpty()) {
                        System.out.println("No accounts available.");
                        return;
                }

                for (Account account : accounts) {
                        System.out.println("----------------------------");
                        System.out.println(account);
                        System.out.println(
                                        "Interest rate: " + account.interestRate() + "%");
                        System.out.println(
                                        "Yearly interest: Rs." + account.yearlyInterest());
                }
        }

        // Deposit money
        private static void depositMoney() {

                System.out.println("\n--- Deposit Money ---");

                Account account = askForAccount();

                if (account == null) {
                        return;
                }

                long amount = readLong("Enter deposit amount: ");

                try {
                        account.deposit(amount);
                        System.out.println("Deposit successful!");
                        System.out.println(
                                        "Updated balance: Rs." + account.getBalance());

                } catch (InvalidAmountException e) {
                        System.out.println("Deposit failed: " + e.getMessage());
                }
        }

        // Withdraw money
        private static void withdrawMoney() {

                System.out.println("\n--- Withdraw Money ---");

                Account account = askForAccount();

                if (account == null) {
                        return;
                }

                long amount = readLong("Enter withdrawal amount: ");

                try {
                        boolean success = account.withdraw(amount);

                        if (success) {
                                System.out.println("Withdrawal successful!");
                                System.out.println(
                                                "Updated balance: Rs." + account.getBalance());
                        } else {
                                System.out.println(
                                                "Withdrawal rejected by the account's rules.");
                        }

                } catch (Exception e) {
                        System.out.println("Withdrawal failed: " + e.getMessage());
                }
        }

        // Transfer money
        private static void transferMoney() {

                System.out.println("\n--- Transfer Money ---");

                System.out.println("Enter sender account details:");
                Account sender = askForAccount();

                if (sender == null) {
                        return;
                }

                System.out.println("Enter receiver account details:");
                Account receiver = askForAccount();

                if (receiver == null) {
                        return;
                }

                if (sender == receiver) {
                        System.out.println(
                                        "Sender and receiver must be different accounts.");
                        return;
                }

                long amount = readLong("Enter transfer amount: ");

                try {
                        sender.transfer(receiver, amount);

                        System.out.println("Transfer successful!");
                        System.out.println(
                                        "Sender balance: Rs." + sender.getBalance());
                        System.out.println(
                                        "Receiver balance: Rs." + receiver.getBalance());

                } catch (BankException e) {
                        System.out.println("Transfer failed: " + e.getMessage());
                } catch (Exception e) {
                        System.out.println("Transfer failed: " + e.getMessage());
                }
        }

        // Find an account by account number
        private static Account askForAccount() {

                System.out.print("Enter account number: ");
                String accountNumber = sc.nextLine().trim();

                Account account = findAccount(accountNumber);

                if (account == null) {
                        System.out.println("Account not found.");
                }

                return account;
        }

        private static Account findAccount(String accountNumber) {

                for (Account account : accounts) {

                        if (account.getAccountNumber().equalsIgnoreCase(
                                        accountNumber)) {
                                return account;
                        }
                }

                return null;
        }

        // Lab 09: Multithreading and shared account balance
        private static void runLab09Demo() {

                System.out.println("\n--- Lab 09: Multithreading Demo ---");

                long startingBalance = readLong("Enter starting balance: ");

                int threadCount = readInt("Enter number of threads: ");

                int depositsPerThread = readInt("Enter deposits per thread: ");

                long amount = readLong("Enter amount per deposit: ");

                if (startingBalance < 0 || threadCount <= 0
                                || depositsPerThread <= 0 || amount <= 0) {

                        System.out.println(
                                        "Balance must be non-negative; thread count, "
                                                        + "deposit count, and amount must be positive.");
                        return;
                }

                Account testAccount = new SavingsAccount("ThreadTest", startingBalance, 0);

                Thread[] threads = new Thread[threadCount];

                long expectedBalance = startingBalance
                                + (long) threadCount * depositsPerThread * amount;

                System.out.println("\nStarting balance: Rs." + startingBalance);
                System.out.println("Expected final balance: Rs." + expectedBalance);
                System.out.println("\nStarting threads...");

                for (int i = 0; i < threadCount; i++) {

                        AccountWorker worker = new AccountWorker(
                                        testAccount, depositsPerThread, amount);

                        threads[i] = new Thread(worker, "Deposit-Thread-" + (i + 1));
                        threads[i].start();
                }

                for (Thread thread : threads) {

                        try {
                                thread.join();

                        } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                System.out.println("Main thread was interrupted.");
                                return;
                        }
                }

                long actualBalance = testAccount.getBalance();

                System.out.println("\n--- Multithreading Results ---");
                System.out.println("Expected balance: Rs." + expectedBalance);
                System.out.println("Actual balance: Rs." + actualBalance);

                if (actualBalance == expectedBalance) {
                        System.out.println("Result: Balance matches expected value.");
                } else {
                        System.out.println("Result: Race condition may have occurred.");
                }
        }

        // Read an integer safely
        private static int readInt(String message) {

                while (true) {

                        System.out.print(message);
                        String input = sc.nextLine().trim();

                        try {
                                return Integer.parseInt(input);

                        } catch (NumberFormatException e) {
                                System.out.println("Please enter a valid whole number.");
                        }
                }
        }

        // Read a long integer safely
        private static long readLong(String message) {

                while (true) {

                        System.out.print(message);
                        String input = sc.nextLine().trim();

                        try {
                                return Long.parseLong(input);

                        } catch (NumberFormatException e) {
                                System.out.println("Please enter a valid whole number.");
                        }
                }
        }
}
