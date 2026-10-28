
package service;

import model.Account;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;

public class AccountStore {

    private final HashMap<String, Account> accounts = new HashMap<>();
    private final ArrayList<String> transactionHistory = new ArrayList<>();

    public synchronized void add(Account account) {
        if (account == null) {
            throw new IllegalArgumentException(
                    "Account cannot be null.");
        }

        accounts.put(account.getAccountNumber(), account);
    }

    public synchronized Account get(String accountNumber) {
        if (accountNumber == null) {
            return null;
        }

        return accounts.get(accountNumber);
    }

    public synchronized ArrayList<Account> getAll() {
        return new ArrayList<>(accounts.values());
    }

    public synchronized void addTransaction(String transaction) {
        if (transaction != null && !transaction.isBlank()) {
            transactionHistory.add(transaction);
        }
    }

    public synchronized ArrayList<String> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }

    public synchronized ArrayList<Account> sortedByBalance() {
        ArrayList<Account> sorted = getAll();

        sorted.sort(
                Comparator.comparingLong(Account::getBalance)
                        .reversed());

        return sorted;
    }

    public synchronized ArrayList<Account> getAccountsAboveBalance(
            long amount) {

        ArrayList<Account> result = new ArrayList<>();

        for (Account account : accounts.values()) {
            if (account.getBalance() > amount) {
                result.add(account);
            }
        }

        result.sort(
                Comparator.comparingLong(Account::getBalance)
                        .reversed());

        return result;
    }

    public synchronized TreeMap<String, Account> getAccountsByAccountNumber() {

        return new TreeMap<>(accounts);
    }

    public synchronized int size() {
        return accounts.size();
    }
}
