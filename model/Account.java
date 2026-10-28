
package model;

import java.io.Serializable;
import java.util.Objects;
import exception.AccountNotFoundException;
import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.annotation.Id;
import model.annotation.Positive;

public abstract class Account implements Transactable, InterestBearing, Serializable {
    private static final long serialVersionUID = 1L;
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
    public synchronized void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be greater than zero.");
        }
        if (!active) {
            throw new IllegalStateException("Account is inactive.");
        }
        balance += amount;
    }

    @Override
    public synchronized boolean withdraw(long amount)
            throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be greater than zero.");
        }
        if (!active) {
            throw new IllegalStateException("Account is inactive.");
        }
        if (amount > balance && !canWithdraw(amount)) {
            throw new InsufficientFundsException(amount - balance);
        }
        if (!canWithdraw(amount)) {
            throw new InvalidAmountException("Withdrawal is not permitted by account rules.");
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
    public synchronized long getBalance() {
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

    public void transfer(Account to, long amount) throws BankException {
        if (to == null) {
            throw new AccountNotFoundException("Destination account was not found.");
        }
        if (to == this) {
            throw new IllegalArgumentException("Cannot transfer to the same account.");
        }

        synchronized (this) {
            synchronized (to) {
                withdraw(amount);
                try {
                    to.deposit(amount);
                } catch (Exception e) {
                    try {
                        deposit(amount);
                    } catch (Exception rollbackError) {
                        e.addSuppressed(rollbackError);
                    }
                    throw new IllegalStateException("Transfer failed and was rolled back.", e);
                }
            }
        }
        System.out.println("Transfer successful.");
        System.out.println("Transfer operation completed.");
    }

    @Override
    public String toString() {
        return getStatement();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Account))
            return false;
        Account other = (Account) obj;
        return Objects.equals(accountNumber, other.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }
}
