import java.util.*;

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
        System.out.println("Welcome to " + bank.bankname() + " - " + bank.branch());
        System.out.println("========================================");

        Account[] accounts = {
                new Account("Account 1", 1000),
                new Account("Account 2", 2000),
                new Account("Account 3", 3000)
        };

        accounts[0].deposit(500);
        accounts[1].deposit(1000);
        accounts[2].withdraw(500);

        System.out.println("\n========== ACCOUNT DETAILS ==========");

        for (Account account : accounts) {
            System.out.println(account);
        }

        System.out.println("\n========== ACCOUNT COMPARISON ==========");

        Account a1 = accounts[0];
        Account a2 = accounts[1];

        System.out.println("Are Account 1 and Account 2 equal? " + a1.equals(a2));

        System.out.println(
                "Is Account 1 an Account object? " + (a1 instanceof Account));

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
        System.out.println("Address: "
                + customer.getAddress().getLine() + ", "
                + customer.getAddress().getCity() + ", "
                + customer.getAddress().getPincode());

        Customer clonedCustomer = customer.clone();

        System.out.println(
                "Customer cloned successfully: "
                        + (clonedCustomer != null));

        System.out.println("\n========== ACCOUNT STATEMENT ==========");
        System.out.println(accounts[0].getStatement());

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

                case OPEN_ACCOUNT:
                    System.out.println("Open Account — to be implemented in a later lab.");
                    break;

                case DEPOSIT:
                    System.out.println("Deposit — to be implemented in a later lab.");
                    break;

                case WITHDRAW:
                    System.out.println("Withdraw — to be implemented in a later lab.");
                    break;

                case TRANSFER:
                    System.out.println("Transfer — to be implemented in a later lab.");
                    break;

                case EXIT:
                    exit = true;
                    System.out.println("Thank you for using MiniBank. Goodbye!");
                    break;
            }
        }

        sc.close();
    }
}