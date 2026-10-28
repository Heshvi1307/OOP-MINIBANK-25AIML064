
package model;

import model.annotation.Id;
import model.annotation.Positive;

import java.util.Objects;

public abstract class Account implements Transactable, InterestBearing {

    private static int accountCounter = 1000;

    @Id
    private final String accountNumber;

    private String ownerName;

    @Positive
    private long balance;

    private boolean active;

    public Account(String ownerName, long openingBalance) {
        this.accountNumber = String.format("AC%04d", ++accountCounter);
        this.ownerName = ownerName;
        this.balance = openingBalance;
        this.active = true;
    }

    public Account(String ownerName) {
        this(ownerName, 0);
    }

    @Override
    public void deposit(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        if (!active) {
            throw new IllegalStateException("Account is inactive");
        }

        balance += amount;
    }

    @Override
    public boolean withdraw(long amount) {
        if (amount <= 0 || !active || !canWithdraw(amount)) {
            return false;
        }

        balance -= amount;
        return true;
    }

    public abstract boolean canWithdraw(long amount);

    @Override
    public abstract double interestRate();

    @Override
    public double yearlyInterest() {
        return getBalance() * interestRate() / 100.0;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @Override
    public long getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }

    public void closeAccount() {
        active = false;
    }

    public String getStatement() {
        return "Account Number: " + accountNumber
                + "\nOwner: " + ownerName
                + "\nBalance: " + balance
                + "\nActive: " + active;
    }

    public static boolean transfer(
            Account from, Account to, long amount) {

        if (from == null || to == null || amount <= 0) {
            return false;
        }

        if (from.withdraw(amount)) {
            try {
                to.deposit(amount);
                return true;
            } catch (RuntimeException e) {
                from.deposit(amount);
                return false;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return getStatement();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Account)) {
            return false;
        }

        Account other = (Account) obj;
        return Objects.equals(accountNumber, other.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }
}
