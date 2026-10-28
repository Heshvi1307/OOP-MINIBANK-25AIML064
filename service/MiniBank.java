
package service;

import java.util.Scanner;

import model.Account;
import model.SavingsAccount;
import model.CurrentAccount;
import model.FixedDepositAccount;
import model.Customer;
import model.WithdrawRule;
import model.Premium;
import util.AnnotationValidator;
import util.Validator;
import util.Command;
import util.CommandParser;

import static util.StatementFormatter.buildStatement;

public class MiniBank {

        public static void main(String[] args) {

                Scanner sc = new Scanner(System.in);

                System.out.println("================================");
                System.out.println("       WELCOME TO MINIBANK      ");
                System.out.println("================================");

                Account savings = new SavingsAccount("Alice", 5000, 1000);
                Account current = new CurrentAccount("Bob", 10000, 2000);
                Account fixedDeposit = new FixedDepositAccount("Charlie", 15000);

                System.out.println("\n--- Account Details ---");
                System.out.println(savings);
                System.out.println();
                System.out.println(current);
                System.out.println();
                System.out.println(fixedDeposit);

                System.out.println("\n--- Yearly Interest ---");
                System.out.println("Savings Interest: " + savings.yearlyInterest());
                System.out.println("Current Interest: " + current.yearlyInterest());
                System.out.println("Fixed Deposit Interest: "
                                + fixedDeposit.yearlyInterest());

                System.out.println("\n--- Premium Account Check ---");
                if (savings instanceof Premium) {
                        System.out.println("Savings account has Premium status.");
                } else {
                        System.out.println("Savings account is not Premium.");
                }

                System.out.println("\n--- WithdrawRule: Anonymous Class ---");
                WithdrawRule anonymousRule = new WithdrawRule() {
                        @Override
                        public boolean allow(Account account, long amount) {
                                return amount > 0 && amount <= account.getBalance();
                        }
                };

                System.out.println("Can withdraw 1000: "
                                + anonymousRule.allow(savings, 1000));

                System.out.println("\n--- WithdrawRule: Lambda ---");
                WithdrawRule lambdaRule = (account, amount) -> amount > 0
                                && amount <= account.getBalance();

                System.out.println("Can withdraw 2000: "
                                + lambdaRule.allow(savings, 2000));

                System.out.println("\n--- Customer Details ---");
                Customer customer = new Customer(
                                "AlexandriaTest", "alice@example.com", "9876543210");
                System.out.println(customer);

                Customer clonedCustomer = customer.clone();
                System.out.println("Cloned Customer: " + clonedCustomer);

                System.out.println("\n--- Validator Tests ---");
                System.out.println("Valid mobile: "
                                + Validator.isValidMobile("9876543210"));
                System.out.println("Valid email: "
                                + Validator.isValidEmail("alice@example.com"));

                System.out.println("\n--- Command Parser ---");
                Command command = CommandParser.parse("DEPOSIT AC0001 500");
                System.out.println(command);

                System.out.println("\n--- Account Statement ---");
                System.out.println(buildStatement(savings));

                // Lab 07: Annotation Validation
                System.out.println("\n--- Lab 07: Annotation Validation ---");

                System.out.print("Enter account holder name: ");
                String name = sc.nextLine();

                System.out.print("Enter account balance (enter -100 to test validation): ");
                long balance = sc.nextLong();

                Account testAccount = new Account(name, balance) {
                        @Override
                        public boolean canWithdraw(long amount) {
                                return false;
                        }

                        @Override
                        public double interestRate() {
                                return 0.0;
                        }
                };

                String[] errors = AnnotationValidator.validate(testAccount);

                if (errors.length == 0) {
                        System.out.println("No validation errors.");
                } else {
                        System.out.println("Validation Errors:");
                        for (String error : errors) {
                                System.out.println(error);
                        }
                }

                System.out.println("\n--- Valid Account Validation ---");
                String[] validErrors = AnnotationValidator.validate(savings);

                if (validErrors.length == 0) {
                        System.out.println("No validation errors.");
                } else {
                        for (String error : validErrors) {
                                System.out.println(error);
                        }
                }

                System.out.println("\n--- MaxLength Annotation Test ---");
                String[] customerErrors = AnnotationValidator.validate(customer);

                if (customerErrors.length == 0) {
                        System.out.println("Customer fields are valid.");
                } else {
                        for (String error : customerErrors) {
                                System.out.println(error);
                        }
                }

                System.out.println("\n================================");
                System.out.println("       MINIBANK DEMO ENDED      ");
                System.out.println("================================");

                sc.close();
        }
}
