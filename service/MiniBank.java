package service;

import exception.BankException;
import model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MiniBank {

        private static final Scanner sc = new Scanner(System.in);
        private static final List<Account> accounts = new ArrayList<>();
        private static final Repository<Customer> customerRepository = new Repository<>();
        private static final BankService bankService = new BankService();

        private static final Path DATA_FOLDER = Path.of("data");
        private static final Path ACCOUNT_FILE = DATA_FOLDER.resolve("accounts.dat");
        private static final Path LOG_FOLDER = DATA_FOLDER.resolve("logs");
        private static final Path TRANSACTION_FILE = LOG_FOLDER.resolve("transactions.log");
        private static final Path REPORT_FILE = DATA_FOLDER.resolve("end-of-day-report.txt");

        public static void main(String[] args) {
                loadAccountsAtStartup();

                System.out.println("\n==========================================");
                System.out.println("                 MINIBANK");
                System.out.println("          Banking Management System");
                System.out.println("==========================================");

                boolean running = true;

                while (running) {
                        printMainMenu();

                        switch (readInt("Enter your choice: ")) {
                                case 1 -> openAccount();
                                case 2 -> depositMoney();
                                case 3 -> withdrawMoney();
                                case 4 -> transferMoney();
                                case 5 -> accountManagementMenu();
                                case 6 -> saveAccountsAndGenerateReport();
                                case 7 -> systemToolsMenu();
                                case 8 -> {
                                        saveAccountsQuietly();
                                        bankService.getProcessor().stop();
                                        running = false;
                                        System.out.println(
                                                        "Thank you for banking with MiniBank.");
                                }
                                default -> System.out.println("Invalid choice.");
                        }
                }
        }

        private static void printMainMenu() {
                System.out.println("\n--------------- MAIN MENU ---------------");
                System.out.println("1. Open Account");
                System.out.println("2. Deposit Funds");
                System.out.println("3. Withdraw Funds");
                System.out.println("4. Transfer Funds");
                System.out.println("5. Account Management");
                System.out.println("6. Reports & Data Persistence");
                System.out.println("7. System & Academic Tools");
                System.out.println("8. Exit & Save");
        }

        private static void loadAccountsAtStartup() {
                try {
                        if (Files.exists(ACCOUNT_FILE)) {
                                Account[] loaded = StatePersister.load(ACCOUNT_FILE);
                                accounts.clear();

                                // Update the counter before generating replacement IDs.
                                for (Account account : loaded) {
                                        if (account != null) {
                                                Account.updateAccountCounter(
                                                                account.getAccountNumber());
                                        }
                                }

                                Set<String> seenIds = new HashSet<>();
                                int repaired = 0;

                                for (Account account : loaded) {
                                        if (account == null) {
                                                continue;
                                        }

                                        String id = account.getAccountNumber()
                                                        .toUpperCase(Locale.ROOT);

                                        if (!seenIds.add(id)) {
                                                account.repairDuplicateAccountNumber();

                                                seenIds.add(account.getAccountNumber()
                                                                .toUpperCase(Locale.ROOT));
                                                repaired++;
                                        }

                                        accounts.add(account);
                                }

                                registerAllAccounts();

                                if (repaired > 0) {
                                        saveAccountsQuietly();
                                        System.out.println(
                                                        "Repaired " + repaired
                                                                        + " duplicate account ID(s). "
                                                                        + "Balances were preserved.");
                                }

                                System.out.println(
                                                "Loaded " + accounts.size()
                                                                + " saved account(s).");
                        } else {
                                createDemoAccounts();
                                registerAllAccounts();
                                saveAccountsQuietly();
                        }
                } catch (IOException | ClassNotFoundException e) {
                        System.out.println(
                                        "Could not load accounts: " + e.getMessage());

                        accounts.clear();
                        createDemoAccounts();
                        registerAllAccounts();
                }
        }

        private static void registerAllAccounts() {
                for (Account account : accounts) {
                        bankService.registerAccount(account);
                }
        }

        private static void createDemoAccounts() {
                accounts.clear();
                accounts.add(new SavingsAccount("Alice", 5000, 1000));
                accounts.add(new CurrentAccount("Bob", 10000, 2000));
                accounts.add(new FixedDepositAccount("Charlie", 15000));

                System.out.println("Created initial demo accounts.");
        }

        private static void saveAccountsQuietly() {
                try {
                        StatePersister.save(
                                        accounts.toArray(new Account[0]), ACCOUNT_FILE);
                } catch (IOException e) {
                        System.out.println(
                                        "Warning: save failed: " + e.getMessage());
                }
        }

        private static void saveAccountsAndGenerateReport() {
                try {
                        StatePersister.save(
                                        accounts.toArray(new Account[0]), ACCOUNT_FILE);

                        System.out.println("Accounts saved to " + ACCOUNT_FILE);

                        for (Account account : StatePersister.load(ACCOUNT_FILE)) {
                                System.out.println(account);
                                System.out.println(
                                                "Interest rate: " + account.interestRate() + "%");
                                System.out.println(
                                                "Yearly interest: Rs." + account.yearlyInterest());
                        }

                        ReportGenerator.generate(LOG_FOLDER, REPORT_FILE);
                        System.out.println("Report saved to " + REPORT_FILE);
                } catch (IOException | ClassNotFoundException e) {
                        System.out.println(
                                        "Persistence/reporting failed: " + e.getMessage());
                }
        }

        private static void logTransaction(
                        String type, Account account, long amount) {
                try {
                        TransactionLog.append(
                                        TRANSACTION_FILE,
                                        type + " " + account.getAccountNumber()
                                                        + " " + amount);
                } catch (IOException e) {
                        System.out.println(
                                        "Warning: log failed: " + e.getMessage());
                }
        }

        // ---------------- LAB 14: BANK SERVICE ----------------

        private static void openAccount() {
                AccountInput input = readAccountInput();

                if (input == null) {
                        return;
                }

                try {
                        // openAccount registers the account with BankService.
                        Account account = bankService.openAccount(
                                        input.name,
                                        input.type,
                                        input.balance,
                                        input.rule);

                        accounts.add(account);
                        saveAccountsQuietly();

                        System.out.println(
                                        "Account created successfully:\n" + account);
                } catch (BankException | IllegalArgumentException e) {
                        System.out.println(
                                        "Account creation failed: " + e.getMessage());
                }
        }

        private static final class AccountInput {
                private final String name;
                private final String type;
                private final long balance;
                private final long rule;

                private AccountInput(
                                String name, String type, long balance, long rule) {
                        this.name = name;
                        this.type = type;
                        this.balance = balance;
                        this.rule = rule;
                }
        }

        private static AccountInput readAccountInput() {
                System.out.println("\n1. Savings Account");
                System.out.println("2. Current Account");
                System.out.println("3. Fixed Deposit Account");

                int typeChoice = readInt("Account type: ");

                if (typeChoice < 1 || typeChoice > 3) {
                        System.out.println("Invalid account type.");
                        return null;
                }

                String name = readText("Owner name: ");

                if (name.isBlank()) {
                        System.out.println("Owner name cannot be empty.");
                        return null;
                }

                long balance = readLong("Opening balance: ");

                if (balance < 0) {
                        System.out.println("Balance cannot be negative.");
                        return null;
                }

                String type = switch (typeChoice) {
                        case 1 -> "savings";
                        case 2 -> "current";
                        default -> "fixed";
                };

                long rule = 0;

                if (typeChoice == 1) {
                        rule = readLong("Minimum balance: ");
                } else if (typeChoice == 2) {
                        rule = readLong("Overdraft limit: ");
                }

                if (rule < 0) {
                        System.out.println(
                                        "Account rule cannot be negative.");
                        return null;
                }

                return new AccountInput(name, type, balance, rule);
        }

        // Retained for the Lab 13 repository creation workflow.
        private static Account createAccountFromInput() {
                AccountInput input = readAccountInput();

                if (input == null) {
                        return null;
                }

                try {
                        return switch (input.type) {
                                case "savings" ->
                                        new SavingsAccount(
                                                        input.name, input.balance, input.rule);
                                case "current" ->
                                        new CurrentAccount(
                                                        input.name, input.balance, input.rule);
                                default ->
                                        new FixedDepositAccount(
                                                        input.name, input.balance);
                        };
                } catch (IllegalArgumentException e) {
                        System.out.println(
                                        "Invalid account: " + e.getMessage());
                        return null;
                }
        }

        private static void createMultipleAccounts() {
                int n = readInt("Number of accounts to create: ");

                if (n <= 0 || n > 100) {
                        System.out.println("Enter a number from 1 to 100.");
                        return;
                }

                for (int i = 0; i < n; i++) {
                        System.out.println(
                                        "\nAccount " + (i + 1) + " of " + n);

                        Account account = createAccountFromInput();

                        if (account != null) {
                                accounts.add(account);
                                bankService.registerAccount(account);
                                System.out.println(account);
                        } else {
                                System.out.println("Account skipped.");
                        }
                }

                saveAccountsQuietly();
        }

        private static void viewAccounts() {
                if (accounts.isEmpty()) {
                        System.out.println("No accounts available.");
                        return;
                }

                for (Account account : bankService.listAccounts()) {
                        System.out.println("----------------------------");
                        System.out.println(account);
                        System.out.println(
                                        "Interest rate: " + account.interestRate() + "%");
                        System.out.println(
                                        "Yearly interest: Rs." + account.yearlyInterest());
                }
        }

        private static void depositMoney() {
                Account account = askForAccount();

                if (account == null) {
                        return;
                }

                long amount = readLong("Deposit amount: ");

                try {
                        bankService.deposit(account.getAccountNumber(), amount);
                        logTransaction("DEPOSIT", account, amount);
                        saveAccountsQuietly();

                        System.out.println(
                                        "Deposit successful. Balance: Rs."
                                                        + account.getBalance());
                } catch (BankException | IllegalStateException e) {
                        System.out.println(
                                        "Deposit failed: " + e.getMessage());
                }
        }

        private static void withdrawMoney() {
                Account account = askForAccount();

                if (account == null) {
                        return;
                }

                long amount = readLong("Withdrawal amount: ");

                try {
                        bankService.withdraw(account.getAccountNumber(), amount);
                        logTransaction("WITHDRAW", account, amount);
                        saveAccountsQuietly();

                        System.out.println(
                                        "Withdrawal successful. Balance: Rs."
                                                        + account.getBalance());
                } catch (BankException | IllegalStateException e) {
                        System.out.println(
                                        "Withdrawal failed: " + e.getMessage());
                }
        }

        private static void transferMoney() {
                System.out.println("Sender account:");
                Account sender = askForAccount();

                if (sender == null) {
                        return;
                }

                System.out.println("Receiver account:");
                Account receiver = askForAccount();

                if (receiver == null) {
                        return;
                }

                if (sender == receiver) {
                        System.out.println(
                                        "Choose two different accounts.");
                        return;
                }

                long amount = readLong("Transfer amount: ");

                try {
                        bankService.transfer(
                                        sender.getAccountNumber(),
                                        receiver.getAccountNumber(),
                                        amount);

                        logTransaction("WITHDRAW", sender, amount);
                        logTransaction("DEPOSIT", receiver, amount);
                        saveAccountsQuietly();

                        System.out.println("Transfer successful.");
                        System.out.println(
                                        "Sender balance: Rs." + sender.getBalance());
                        System.out.println(
                                        "Receiver balance: Rs." + receiver.getBalance());
                } catch (BankException | IllegalStateException
                                | IllegalArgumentException e) {
                        System.out.println(
                                        "Transfer failed: " + e.getMessage());
                }
        }

        private static Account askForAccount() {
                String id = readText("Account number: ");

                try {
                        return bankService.findAccount(id);
                } catch (BankException e) {
                        System.out.println("Account not found.");
                        return null;
                }
        }

        private static void searchAccountsByOwner() {
                String name = readText("Owner name to search: ");
                List<Account> matches = bankService.searchByOwnerName(name);

                if (matches.isEmpty()) {
                        System.out.println("No matching accounts found.");
                } else {
                        matches.forEach(System.out::println);
                }
        }

        private static void printAccountStatement() {
                String id = readText("Account number: ");

                try {
                        System.out.println(bankService.statement(id));
                } catch (BankException e) {
                        System.out.println(
                                        "Statement unavailable: " + e.getMessage());
                }
        }

        // ---------------- CONCURRENCY DEMONSTRATION ----------------

        private static void runConcurrencyDemo() {
                long start = readLong("Starting balance: ");
                int threads = readInt("Number of threads: ");
                int deposits = readInt("Deposits per thread: ");
                long amount = readLong("Amount per deposit: ");

                if (start < 0 || threads <= 0 || threads > 200
                                || deposits <= 0 || amount <= 0) {
                        System.out.println(
                                        "Enter valid values (threads max 200).");
                        return;
                }

                Account account = new SavingsAccount("ConcurrencyTest", start, 0);
                Thread[] workers = new Thread[threads];
                long expected;

                try {
                        expected = Math.addExact(
                                        start,
                                        Math.multiplyExact((long) threads * deposits, amount));
                } catch (ArithmeticException e) {
                        System.out.println(
                                        "Expected balance exceeds supported range.");
                        return;
                }

                for (int i = 0; i < threads; i++) {
                        workers[i] = new Thread(
                                        new AccountWorker(account, deposits, amount),
                                        "Deposit-Thread-" + (i + 1));
                        workers[i].start();
                }

                for (Thread worker : workers) {
                        try {
                                worker.join();
                        } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                System.out.println("Operation interrupted.");
                                return;
                        }
                }

                System.out.println("Expected balance: Rs." + expected);
                System.out.println(
                                "Actual balance: Rs." + account.getBalance());
                System.out.println(
                                expected == account.getBalance()
                                                ? "All deposits completed correctly."
                                                : "Balance mismatch detected.");
        }

        // ---------------- BATCH TRANSACTIONS / PRODUCER-CONSUMER ----------------

        private static void runTransactionProcessing() {
                Account account = askForAccount();

                if (account == null) {
                        return;
                }

                long startingBalance = account.getBalance();

                System.out.println(
                                "Selected: " + account.getAccountNumber()
                                                + " | Owner: " + account.getOwnerName()
                                                + " | Balance: Rs." + startingBalance);

                int count = readInt(
                                "Number of deposit-withdrawal pairs: ");
                long amount = readLong("Amount per transaction: ");

                if (count <= 0 || count > 10000 || amount <= 0) {
                        System.out.println(
                                        "Enter a positive count (max 10000) and amount.");
                        return;
                }

                Map<String, Integer> taskCounts = new ConcurrentHashMap<>();
                AtomicInteger deposits = new AtomicInteger();
                AtomicInteger withdrawals = new AtomicInteger();
                AtomicInteger failures = new AtomicInteger();

                TransactionProcessor processor = new TransactionProcessor();

                for (int i = 0; i < count; i++) {
                        processor.submit(() -> {
                                taskCounts.merge(
                                                Thread.currentThread().getName(),
                                                1, Integer::sum);

                                try {
                                        account.deposit(amount);
                                        deposits.incrementAndGet();
                                        logTransaction("DEPOSIT", account, amount);
                                } catch (Exception e) {
                                        failures.incrementAndGet();
                                }
                        });

                        processor.submit(() -> {
                                taskCounts.merge(
                                                Thread.currentThread().getName(),
                                                1, Integer::sum);

                                try {
                                        account.withdraw(amount);
                                        withdrawals.incrementAndGet();
                                        logTransaction("WITHDRAW", account, amount);
                                } catch (Exception e) {
                                        failures.incrementAndGet();
                                }
                        });
                }

                processor.stop();
                saveAccountsQuietly();

                System.out.println("\nStarting balance: Rs." + startingBalance);
                System.out.println("Successful deposits: " + deposits.get());
                System.out.println("Successful withdrawals: " + withdrawals.get());
                System.out.println("Failed transactions: " + failures.get());
                System.out.println("Actual balance: Rs." + account.getBalance());
                System.out.println("Tasks per worker: " + taskCounts);

                System.out.println("\n--- Producer-Consumer Processing ---");

                int n = readInt("Number of deposits: ");
                long depositAmount = readLong("Amount per deposit: ");

                if (n <= 0 || n > 10000 || depositAmount <= 0) {
                        System.out.println(
                                        "Enter a positive count (max 10000) and amount.");
                        return;
                }
                  Account demoAccount = new SavingsAccount(
                                "ProducerConsumerTest", 0, 0);
                TransactionBuffer buffer = new TransactionBuffer(3);
                Runnable stopSignal = () -> {
                };

                Thread producer = new Thread(() -> {
                        try {
                                for (int i = 0; i < n; i++) {
                                        buffer.put(() -> {
                                                try {
                                                        demoAccount.deposit(depositAmount);
                                                        logTransaction(
                                                                        "DEPOSIT", account, depositAmount);
                                                } catch (Exception e) {
                                                        System.out.println(
                                                                        "Deposit failed: " + e.getMessage());
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

                                        if (task == stopSignal) {
                                                break;
                                        }

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

                saveAccountsQuietly();

                System.out.println(
                                "Balance after producer-consumer: Rs."
                                                + demoAccount.getBalance());
                System.out.println("--- Deadlock Demonstration ---");
                DeadlockDemo.runDemo();
        }

        // ---------------- ACCOUNT MANAGEMENT ----------------

        private static void accountManagementMenu() {
                boolean back = false;

                while (!back) {
                        System.out.println("\n--- ACCOUNT MANAGEMENT ---");
                        System.out.println("1. View All Accounts");
                        System.out.println("2. Create Multiple Accounts");
                        System.out.println("3. Search by Owner Name");
                        System.out.println("4. Print Account Statement");
                        System.out.println("0. Back");

                        switch (readInt("Choice: ")) {
                                case 1 -> viewAccounts();
                                case 2 -> createMultipleAccounts();
                                case 3 -> searchAccountsByOwner();
                                case 4 -> printAccountStatement();
                                case 0 -> back = true;
                                default -> System.out.println("Invalid choice.");
                        }
                }
        }

        // ---------------- SYSTEM & ACADEMIC TOOLS ----------------

        private static void systemToolsMenu() {
                boolean back = false;

                while (!back) {
                        System.out.println("\n--- SYSTEM & ACADEMIC TOOLS ---");
                        System.out.println("1. Concurrency Demonstration");
                        System.out.println("2. Transaction Processing & Deadlock");
                        System.out.println("3. Collections Framework");
                        System.out.println("4. Generics & Repository (Lab 13)");
                        System.out.println("0. Back");

                        switch (readInt("Choice: ")) {
                                case 1 -> runConcurrencyDemo();
                                case 2 -> runTransactionProcessing();
                                case 3 -> runCollectionsDemo();
                                case 4 -> runGenericsRepositoryDemo();
                                case 0 -> back = true;
                                default -> System.out.println("Invalid choice.");
                        }
                }
        }

        private static void runCollectionsDemo() {
                if (accounts.isEmpty()) {
                        System.out.println("No accounts available.");
                        return;
                }

                List<Account> sorted = new ArrayList<>(accounts);
                Collections.sort(sorted);

                System.out.println("Accounts sorted using Comparable:");

                for (Account account : sorted) {
                        System.out.println(
                                        account.getAccountNumber() + " | "
                                                        + account.getOwnerName()
                                                        + " | Balance: Rs." + account.getBalance());
                }

                System.out.println("Total accounts: " + sorted.size());
        }

        // ---------------- LAB 13: GENERICS & REPOSITORY ----------------

        private static void runGenericsRepositoryDemo() {
                Repository<Account> repository = new Repository<>();
                repository.saveAll(accounts.toArray(new Account[0]));

                boolean back = false;

                while (!back) {
                        System.out.println(
                                        "\n--- GENERICS & REPOSITORY (LAB 13) ---");
                        System.out.println("1. Account Repository");
                        System.out.println("2. Customer Repository");
                        System.out.println("3. Generic Average");
                        System.out.println("0. Back");

                        switch (readInt("Choice: ")) {
                                case 1 -> accountRepositoryMenu(repository);
                                case 2 -> customerRepositoryMenu();
                                case 3 -> runAverageDemo();
                                case 0 -> back = true;
                                default -> System.out.println("Invalid choice.");
                        }
                }
        }

        private static void accountRepositoryMenu(
                        Repository<Account> repo) {
                boolean back = false;

                while (!back) {
                        System.out.println("\n--- ACCOUNT REPOSITORY ---");
                        System.out.println("1. Add Account");
                        System.out.println("2. Find Account");
                        System.out.println("3. Display All");
                        System.out.println("4. Delete Account");
                        System.out.println("5. Count Accounts");
                        System.out.println("0. Back");

                        switch (readInt("Choice: ")) {
                                case 1 -> {
                                        Account account = createAccountFromInput();

                                        if (account != null) {
                                                accounts.add(account);
                                                repo.save(account);
                                                bankService.registerAccount(account);
                                                saveAccountsQuietly();

                                                System.out.println(
                                                                "Account created and saved:\n" + account);
                                        }
                                }
                                case 2 -> {
                                        String id = readText("Account number: ");
                                        Account account = repo.findById(id);
                                        System.out.println(
                                                        account == null
                                                                        ? "Account not found."
                                                                        : account);
                                }
                                case 3 -> {
                                        for (Account account : repo.findAll()) {
                                                System.out.println(account);
                                        }
                                        System.out.println("Total: " + repo.count());
                                }
                                case 4 -> {
                                        String id = readText("Account number to delete: ");
                                        Account account = repo.findById(id);

                                        if (account == null) {
                                                System.out.println("Account not found.");
                                        } else {
                                                bankService.getRepository().delete(id);
                                                repo.delete(id);
                                                accounts.remove(account);
                                                saveAccountsQuietly();
                                                System.out.println("Account deleted.");
                                        }
                                }
                                case 5 ->
                                        System.out.println(
                                                        "Total accounts: " + repo.count());
                                case 0 -> back = true;
                                default -> System.out.println("Invalid choice.");
                        }
                }
        }

        private static void customerRepositoryMenu() {
                boolean back = false;

                while (!back) {
                        System.out.println("\n--- CUSTOMER REPOSITORY ---");
                        System.out.println("1. Register Customer");
                        System.out.println("2. Find Customer");
                        System.out.println("3. Display All Customers");
                        System.out.println("4. Delete Customer");
                        System.out.println("5. Count Customers");
                        System.out.println("0. Back");

                        switch (readInt("Choice: ")) {
                                case 1 -> {
                                        String name = readText("Customer name: ");
                                        String email = readText("Email: ");
                                        String mobile = readText("Mobile: ");

                                        if (name.isBlank()
                                                        || email.isBlank()
                                                        || mobile.isBlank()) {
                                                System.out.println(
                                                                "All fields are required.");
                                        } else {
                                                Customer customer = new Customer(name, email, mobile);
                                                customerRepository.save(customer);

                                                System.out.println(
                                                                "Customer registered:\n" + customer);
                                                System.out.println(
                                                                "Customer ID: "
                                                                                + customer.getCustomerId());
                                        }
                                }
                                case 2 -> {
                                        String id = readText("Customer ID: ");
                                        Customer customer = customerRepository.findById(id);

                                        System.out.println(
                                                        customer == null
                                                                        ? "Customer not found."
                                                                        : customer);
                                }
                                case 3 -> {
                                        List<Customer> customers = customerRepository.findAll();

                                        if (customers.isEmpty()) {
                                                System.out.println(
                                                                "No customers registered.");
                                        } else {
                                                customers.forEach(System.out::println);
                                        }

                                        System.out.println(
                                                        "Total customers: "
                                                                        + customerRepository.count());
                                }
                                case 4 -> {
                                        String id = readText("Customer ID to delete: ");

                                        System.out.println(
                                                        customerRepository.delete(id)
                                                                        ? "Customer deleted."
                                                                        : "Customer not found.");
                                }
                                case 5 ->
                                        System.out.println(
                                                        "Total customers: "
                                                                        + customerRepository.count());
                                case 0 -> back = true;
                                default -> System.out.println("Invalid choice.");
                        }
                }
        }

        private static void runAverageDemo() {
                int n = readInt("How many numbers (1-10)? ");

                if (n < 1 || n > 10) {
                        System.out.println("Enter a number from 1 to 10.");
                        return;
                }

                Integer[] values = new Integer[n];

                for (int i = 0; i < n; i++) {
                        long value = readLong("Number " + (i + 1) + ": ");

                        if (value < Integer.MIN_VALUE
                                        || value > Integer.MAX_VALUE) {
                                System.out.println(
                                                "Enter a number within the Integer range.");
                                return;
                        }

                        values[i] = (int) value;
                }

                System.out.println("Average: " + Repository.average(values));
        }

        // ---------------- INPUT HELPERS ----------------

        private static String readText(String message) {
                System.out.print(message);
                return sc.nextLine().trim();
        }

        private static int readInt(String message) {
                while (true) {
                        System.out.print(message);

                        try {
                                return Integer.parseInt(sc.nextLine().trim());
                        } catch (NumberFormatException e) {
                                System.out.println(
                                                "Enter a valid whole number.");
                        }
                }
        }

        private static long readLong(String message) {
                while (true) {
                        System.out.print(message);

                        try {
                                return Long.parseLong(sc.nextLine().trim());
                        } catch (NumberFormatException e) {
                                System.out.println(
                                                "Enter a valid whole number.");
                        }
                }
        }
}