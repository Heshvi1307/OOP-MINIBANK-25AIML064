
package model;

import java.io.Serializable;
import java.util.Objects;

import exception.AccountNotFoundException;
import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.annotation.Id;
import model.annotation.Positive;

public abstract class Account
        implements Transactable, InterestBearing, Serializable,
        Comparable<Account> {

    private static final long serialVersionUID = 1L;
    private static int accountCounter = 1000;

    @Id
    private String accountNumber;

    private String ownerName;

    @Positive
    private long balance;

    private boolean active;

    public Account(String ownerName, long openingBalance) {
        if (ownerName == null || ownerName.isBlank()) {
            throw new IllegalArgumentException(
                    "Owner name cannot be empty.");
        }

        if (openingBalance < 0) {
            throw new IllegalArgumentException(
                    "Opening balance cannot be negative.");
        }

        synchronized (Account.class) {
            this.accountNumber = String.format("AC%04d", ++accountCounter);
        }

        this.ownerName = ownerName.trim();
        this.balance = openingBalance;
        this.active = true;
    }

    public Account(String ownerName) {
        this(ownerName, 0);
    }

    public static synchronized void updateAccountCounter(
            String accountNumber) {

        if (accountNumber == null
                || !accountNumber.matches("AC\\d+")) {
            return;
        }

        try {
            int number = Integer.parseInt(accountNumber.substring(2));
            accountCounter = Math.max(accountCounter, number);
        } catch (NumberFormatException ignored) {
            // Ignore malformed account numbers.
        }
    }

    /**
     * Repairs a duplicate ID discovered while loading saved data.
     * This method changes only the ID, not the account's balance
     * or other account details.
     */
    public synchronized void repairDuplicateAccountNumber() {
        synchronized (Account.class) {
            accountNumber = String.format("AC%04d", ++accountCounter);
        }
    }

    @Override
    public synchronized void deposit(long amount)
            throws InvalidAmountException {

        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Deposit amount must be greater than zero.");
        }

        if (!active) {
            throw new IllegalStateException(
                    "Account is inactive.");
        }

        if (Long.MAX_VALUE - balance < amount) {
            throw new InvalidAmountException(
                    "Deposit exceeds the maximum supported balance.");
        }

        balance += amount;
    }

    @Override
    public synchronized boolean withdraw(long amount)
            throws InsufficientFundsException,
            InvalidAmountException {

        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Withdrawal amount must be greater than zero.");
        }

        if (!active) {
            throw new IllegalStateException(
                    "Account is inactive.");
        }

        if (!canWithdraw(amount)) {
            if (amount > balance) {
                throw new InsufficientFundsException(
                        amount - balance);
            }

            throw new InvalidAmountException(
                    "Withdrawal is not permitted by account rules.");
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

    public synchronized String getAccountNumber() {
        return accountNumber;
    }

    public synchronized String getOwnerName() {
        return ownerName;
    }

    public synchronized void setOwnerName(String ownerName) {
        if (ownerName == null || ownerName.isBlank()) {
            throw new IllegalArgumentException(
                    "Owner name cannot be empty.");
        }

        this.ownerName = ownerName.trim();
    }

    @Override
    public synchronized long getBalance() {
        return balance;
    }

    public synchronized boolean isActive() {
        return active;
    }

    public synchronized void closeAccount() {
        active = false;
    }

    public synchronized String getStatement() {
        return "Account Number: " + accountNumber
                + "\nOwner: " + ownerName
                + "\nBalance: " + balance
                + "\nActive: " + active;
    }

    public void transfer(Account to, long amount)
            throws BankException {

        if (to == null) {
            throw new AccountNotFoundException(
                    "Destination account was not found.");
        }

        if (to == this) {
            throw new IllegalArgumentException(
                    "Cannot transfer to the same account.");
        }

        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Transfer amount must be greater than zero.");
        }

        // Lock accounts in a consistent order to reduce deadlock risk.
        Account first = this;
        Account second = to;

        if (first.getAccountNumber().compareTo(
                second.getAccountNumber()) > 0) {
            first = to;
            second = this;
        }

        synchronized (first) {
            synchronized (second) {
                withdraw(amount);

                try {
                    to.deposit(amount);
                } catch (Exception e) {
                    try {
                        deposit(amount);
                    } catch (Exception rollbackError) {
                        e.addSuppressed(rollbackError);
                    }

                    throw new IllegalStateException(
                            "Transfer failed and rollback was attempted.",
                            e);
                }
            }
        }
    }

    @Override
    public int compareTo(Account other) {
        if (other == null) {
            throw new NullPointerException(
                    "Cannot compare an account with null.");
        }

        return getAccountNumber().compareTo(
                other.getAccountNumber());
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

        return Objects.equals(
                getAccountNumber(), other.getAccountNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAccountNumber());
    }
}
