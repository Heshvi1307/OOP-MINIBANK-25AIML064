
package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.Account;
import model.SavingsAccount;
import model.CurrentAccount;
import model.FixedDepositAccount;

import exception.BankException;
import exception.AccountNotFoundException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;

public class MiniBank {

        private static final Scanner sc = new Scanner(System.in);
        private static final List<Account> accounts = new ArrayList<>();

        public static void main(String[] args) {

                // Preloaded accounts for testing.
                accounts.add(new SavingsAccount("Alice", 5000, 1000));
                accounts.add(new CurrentAccount("Bob", 10000, 2000));
                accounts.add(new FixedDepositAccount("Charlie", 15000));

                System.out.println("================================");
                System.out.println("       WELCOME TO MINIBANK      ");
                System.out.println("================================");

                boolean running = true;

                while (running) {
                        System.out.println("\n========== MINIBANK ==========");
                        System.out.println("1. Open Account");
                        System.out.println("2. Deposit");
                        System.out.println("3. Withdraw");
                        System.out.println("4. Transfer");
                        System.out.println("5. View All Accounts");
                        System.out.println("6. Exit");
                        System.out.print("Enter your choice: ");

                        int choice = readInt();

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
                                        running = false;
                                        System.out.println("Thank you for using MiniBank!");
                                        break;
                                default:
                                        System.out.println("Invalid choice. Please select 1-6.");
                        }
                }

                sc.close();
        }

        private static void openAccount() {
                System.out.println("\n--- Open Account ---");
                System.out.println("1. Savings Account");
                System.out.println("2. Current Account");
                System.out.println("3. Fixed Deposit Account");
                System.out.print("Select account type: ");

                int type = readInt();

                if (type < 1 || type > 3) {
                        System.out.println("Invalid account type.");
                        return;
                }

                System.out.print("Enter account holder name: ");
                String name = sc.nextLine().trim();

                if (name.isEmpty()) {
                        System.out.println("Account holder name cannot be empty.");
                        return;
                }

                System.out.print("Enter opening balance: ");
                long balance = readLong();

                if (balance < 0) {
                        System.out.println("Opening balance cannot be negative.");
                        return;
                }

                Account account;

                switch (type) {
                        case 1:
                                account = new SavingsAccount(name, balance, 1000);
                                break;
                        case 2:
                                account = new CurrentAccount(name, balance, 2000);
                                break;
                        default:
                                account = new FixedDepositAccount(name, balance);
                                break;
                }

                accounts.add(account);

                System.out.println("\nAccount created successfully!");
                System.out.println("Your account details:");
                System.out.println(account);
                System.out.println("Please remember your account number.");
        }

        private static void viewAccounts() {
                System.out.println("\n--- All Accounts ---");

                if (accounts.isEmpty()) {
                        System.out.println("No accounts have been created yet.");
                        return;
                }

                for (Account account : accounts) {
                        System.out.println(account);
                        System.out.println("------------------------------");
                }
        }

        private static void depositMoney() {
                System.out.println("\n--- Deposit ---");

                Account account = askForAccount();

                if (account == null) {
                        return;
                }

                System.out.print("Enter deposit amount: ");
                long amount = readLong();

                try {
                        account.deposit(amount);
                        System.out.println("Deposit successful!");
                        System.out.println("Updated account details:");
                        System.out.println(account);
                } catch (InvalidAmountException e) {
                        System.out.println("Deposit failed: " + e.getMessage());
                }
        }

        private static void withdrawMoney() {
                System.out.println("\n--- Withdraw ---");

                Account account = askForAccount();

                if (account == null) {
                        return;
                }

                System.out.print("Enter withdrawal amount: ");
                long amount = readLong();

                try {
                        account.withdraw(amount);
                        System.out.println("Withdrawal successful!");
                        System.out.println("Updated account details:");
                        System.out.println(account);
                } catch (InsufficientFundsException e) {
                        System.out.println("Withdrawal failed: short by "
                                        + e.getShortfall());
                } catch (InvalidAmountException e) {
                        System.out.println("Withdrawal failed: " + e.getMessage());
                }
        }

        private static void transferMoney() {
                System.out.println("\n--- Transfer ---");

                System.out.println("Available accounts:");
                viewAccounts();

                System.out.print("Enter sender account number: ");
                String senderNumber = sc.nextLine().trim();

                System.out.print("Enter receiver account number: ");
                String receiverNumber = sc.nextLine().trim();

                try {
                        Account sender = findAccount(senderNumber);
                        Account receiver = findAccount(receiverNumber);

                        if (sender == receiver) {
                                System.out.println("Sender and receiver must be different accounts.");
                                return;
                        }

                        System.out.print("Enter transfer amount: ");
                        long amount = readLong();

                        sender.transfer(receiver, amount);

                        System.out.println("Transfer successful!");
                        System.out.println("\nUpdated sender account:");
                        System.out.println(sender);
                        System.out.println("\nUpdated receiver account:");
                        System.out.println(receiver);

                } catch (BankException e) {
                        System.out.println("Transfer failed: " + e.getMessage());
                }
        }

        private static Account askForAccount() {
                System.out.println("Available accounts:");
                viewAccounts();

                System.out.print("Enter account number: ");
                String number = sc.nextLine().trim();

                try {
                        return findAccount(number);
                } catch (AccountNotFoundException e) {
                        System.out.println(e.getMessage());
                        return null;
                }
        }

        private static Account findAccount(String number)
                        throws AccountNotFoundException {

                for (Account account : accounts) {
                        if (account.getAccountNumber().equalsIgnoreCase(number)) {
                                return account;
                        }
                }

                throw new AccountNotFoundException(number);
        }

        private static int readInt() {
                while (true) {
                        try {
                                return Integer.parseInt(sc.nextLine().trim());
                        } catch (NumberFormatException e) {
                                System.out.print("Invalid input. Enter a whole number: ");
                        }
                }
        }

        private static long readLong() {
                while (true) {
                        try {
                                return Long.parseLong(sc.nextLine().trim());
                        } catch (NumberFormatException e) {
                                System.out.print("Invalid amount. Enter a whole number: ");
                        }
                }
        }
}
