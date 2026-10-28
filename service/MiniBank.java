
package service;

import static util.StatementFormatter.buildStatement;

import java.util.Scanner;

import model.Account;
import model.CurrentAccount;
import model.Customer;
import model.FixedDepositAccount;
import model.Premium;
import model.SavingsAccount;
import model.WithdrawRule;
import util.Command;
import util.CommandParser;
import util.Validator;

record BankInfo(String bankname, String branch) {
}

enum MenuOption {
        OPEN_ACCOUNT, DEPOSIT, WITHDRAW, TRANSFER, EXIT
}

public class MiniBank {

        public static void main(String[] args) {

                BankInfo bank = new BankInfo("MiniBank", "Main Branch");
                Scanner sc = new Scanner(System.in);

                System.out.println("========================================");
                System.out.println("Welcome to " + bank.bankname()
                                + " - " + bank.branch());
                System.out.println("========================================");

                System.out.println("\n========== CREATE ACCOUNTS ==========");
                System.out.print("Enter number of accounts: ");

                int n = sc.nextInt();
                sc.nextLine();

                if (n <= 0) {
                        System.out.println("Number of accounts must be positive.");
                        sc.close();
                        return;
                }

                Account[] accounts = new Account[n];

                for (int i = 0; i < n; i++) {

                        System.out.println("\nAccount " + (i + 1));
                        System.out.println("1. Savings Account");
                        System.out.println("2. Current Account");
                        System.out.println("3. Fixed Deposit Account");

                        System.out.print("Enter account type: ");
                        int type = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Enter owner name: ");
                        String ownerName = sc.nextLine();

                        System.out.print("Enter opening balance: ");
                        long balance = sc.nextLong();

                        if (type == 1) {
                                System.out.print("Enter minimum balance: ");
                                long minBalance = sc.nextLong();

                                accounts[i] = new SavingsAccount(
                                                ownerName, balance, minBalance);

                        } else if (type == 2) {
                                System.out.print("Enter overdraft limit: ");
                                long overdraftLimit = sc.nextLong();

                                accounts[i] = new CurrentAccount(
                                                ownerName, balance, overdraftLimit);

                        } else if (type == 3) {
                                accounts[i] = new FixedDepositAccount(
                                                ownerName, balance);

                        } else {
                                System.out.println("Invalid account type.");
                                i--;
                                continue;
                        }

                        sc.nextLine();
                }

                System.out.println("\n========== ACCOUNT DETAILS ==========");

                for (Account account : accounts) {
                        System.out.println(account);
                }

                System.out.println("\n========== INTEREST RATES ==========");

                for (Account account : accounts) {
                        System.out.println(account.getOwnerName() + " : "
                                        + account.interestRate() + "%");

                        System.out.println("Yearly interest: Rs."
                                        + account.yearlyInterest());

                        if (account instanceof SavingsAccount) {
                                System.out.println("This is a Savings Account.");
                        }

                        if (account instanceof Premium) {
                                System.out.println("Premium account marker detected.");
                        }
                }

                System.out.println("\n========== WITHDRAW RULES ==========");

                Account testAccount = accounts[0];
                long testAmount = 100;

                WithdrawRule anonymousRule = new WithdrawRule() {
                        @Override
                        public boolean allow(Account account, long amount) {
                                return amount > 0 && account.canWithdraw(amount);
                        }
                };

                WithdrawRule lambdaRule = (account, amount) -> amount > 0 && account.canWithdraw(amount);

                System.out.println("Anonymous class allows withdrawal: "
                                + anonymousRule.allow(testAccount, testAmount));

                System.out.println("Lambda allows withdrawal: "
                                + lambdaRule.allow(testAccount, testAmount));

                System.out.println("\n========== ACCOUNT COMPARISON ==========");

                Account a1 = accounts[0];
                Account a2 = accounts.length > 1 ? accounts[1] : accounts[0];

                System.out.println("Are Account 1 and Account 2 equal? "
                                + a1.equals(a2));

                System.out.println("Is Account 1 an Account object? "
                                + (a1 instanceof Account));

                System.out.println("\n========== CUSTOMER ==========");

                System.out.print("Enter customer name: ");
                String name = sc.nextLine();

                System.out.print("Enter email: ");
                String email = sc.nextLine();

                System.out.print("Enter mobile: ");
                String mobile = sc.nextLine();

                Customer customer = new Customer(name, email, mobile);

                System.out.println("\nEnter address details:");

                System.out.print("Address: ");
                String line = sc.nextLine();

                System.out.print("City: ");
                String city = sc.nextLine();

                System.out.print("Pincode: ");
                String pincode = sc.nextLine();

                Customer.Address address = new Customer.Address(line, city, pincode);

                customer.setAddress(address);

                System.out.println("\nCustomer ID: " + customer.getCustomerId());
                System.out.println("Name: " + customer.getName());
                System.out.println("Email: " + customer.getEmail());
                System.out.println("Mobile: " + customer.getMobile());

                System.out.println("Address: "
                                + customer.getAddress().getLine() + ", "
                                + customer.getAddress().getCity() + ", "
                                + customer.getAddress().getPincode());

                Customer clonedCustomer = customer.clone();

                System.out.println("Customer cloned successfully: "
                                + (clonedCustomer != null));

                System.out.println("\n========== VALIDATOR TESTS ==========");

                System.out.println("Valid mobile: "
                                + Validator.isValidMobile("9876543210"));

                System.out.println("Invalid mobile: "
                                + Validator.isValidMobile("12345"));

                System.out.println("Valid email: "
                                + Validator.isValidEmail("abc@xyz.com"));

                System.out.println("Invalid email: "
                                + Validator.isValidEmail("abc@xyz"));

                System.out.println("Valid PAN: "
                                + Validator.isValidPan("ABCDE1234F"));

                System.out.println("Invalid PAN: "
                                + Validator.isValidPan("ABC123"));

                System.out.println("Valid IFSC: "
                                + Validator.isValidIfsc("SBIN0001234"));

                System.out.println("Invalid IFSC: "
                                + Validator.isValidIfsc("SBIN123"));

                System.out.println("\n========== COMMAND PARSER ==========");

                Command command = CommandParser.parse("DEPOSIT AC0001 500");

                System.out.println("Type: " + command.type());
                System.out.println("Account Number: " + command.accountNumber());
                System.out.println("Amount: " + command.amount());

                System.out.println("\n========== ACCOUNT STATEMENT ==========");

                System.out.println(buildStatement(accounts[0]));

                boolean exit = false;

                while (!exit) {

                        System.out.println("\nPlease select an option:");
                        System.out.println("1. Open Account");
                        System.out.println("2. Deposit");
                        System.out.println("3. Withdraw");
                        System.out.println("4. Transfer");
                        System.out.println("5. Exit");
                        System.out.print("Enter your choice: ");

                        int choice = sc.nextInt();

                        MenuOption option = switch (choice) {
                                case 1 -> MenuOption.OPEN_ACCOUNT;
                                case 2 -> MenuOption.DEPOSIT;
                                case 3 -> MenuOption.WITHDRAW;
                                case 4 -> MenuOption.TRANSFER;
                                case 5 -> MenuOption.EXIT;
                                default -> null;
                        };

                        if (option == null) {
                                System.out.println("Invalid option. Please try again.");
                                continue;
                        }

                        switch (option) {
                                case OPEN_ACCOUNT ->
                                        System.out.println("Open Account — to be implemented in a later lab.");

                                case DEPOSIT ->
                                        System.out.println("Deposit — to be implemented in a later lab.");

                                case WITHDRAW ->
                                        System.out.println("Withdraw — to be implemented in a later lab.");

                                case TRANSFER ->
                                        System.out.println("Transfer — to be implemented in a later lab.");

                                case EXIT -> {
                                        exit = true;
                                        System.out.println(
                                                        "Thank you for using MiniBank. Goodbye!");
                                }
                        }
                }

                sc.close();
        }
}
