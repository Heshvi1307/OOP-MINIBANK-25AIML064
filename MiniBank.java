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