import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("Enter number of accounts:");
        int n = sc.nextInt();
        sc.nextLine();

        Account[] accounts = new Account[n];

        for (int i = 0; i < n; i++) {

            System.out.println("\nEnter account type:");
            System.out.println("1. Savings Account");
            System.out.println("2. Current Account");
            System.out.println("3. Fixed Deposit Account");

            int choice = sc.nextInt();
            sc.nextLine();

            System.out.println("Enter owner name:");
            String name = sc.nextLine();

            System.out.println("Enter opening balance:");
            long balance = sc.nextLong();

            if (choice == 1) {

                System.out.println("Enter minimum balance:");
                long minBalance = sc.nextLong();

                accounts[i] = new SavingsAccount(
                        name, balance, minBalance
                );

            } else if (choice == 2) {

                System.out.println("Enter overdraft limit:");
                long overdraftLimit = sc.nextLong();

                accounts[i] = new CurrentAccount(
                        name, balance, overdraftLimit
                );

            } else if (choice == 3) {

                accounts[i] = new FixedDepositAccount(
                        name, balance
                );

            } else {
                System.out.println("Invalid account type.");
                i--;
            }
        }

        System.out.println("\nInterest Rates:");

        for (Account account : accounts) {
            System.out.println(account.interestRate());

            if (account instanceof SavingsAccount) {
                System.out.println("Savings Account");
            }
        }

        sc.close();
    }
}
