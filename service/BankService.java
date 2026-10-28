
package service;

import exception.AccountNotFoundException;
import exception.BankException;
import exception.InvalidAmountException;
import model.Account;
import model.CurrentAccount;
import model.FixedDepositAccount;
import model.SavingsAccount;
import util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BankService {

    private final Repository<Account> repository;
    private final TransactionProcessor processor;

    public BankService() {
        this(new Repository<>(), new TransactionProcessor());
    }

    public BankService(
            Repository<Account> repository,
            TransactionProcessor processor) {

        if (repository == null || processor == null) {
            throw new IllegalArgumentException(
                    "Repository and processor cannot be null.");
        }

        this.repository = repository;
        this.processor = processor;
    }

    public synchronized void registerAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException(
                    "Account cannot be null.");
        }

        Account existing = repository.findById(
                account.getAccountNumber());

        if (existing != null && existing != account) {
            throw new IllegalArgumentException(
                    "Duplicate account number: "
                            + account.getAccountNumber());
        }

        repository.save(account);
        processor.addAccount(account);
    }

    public Account openAccount(
            String ownerName,
            String type,
            long openingBalance,
            long accountRule) throws BankException {

        if (ownerName == null || ownerName.isBlank()) {
            throw new IllegalArgumentException(
                    "Owner name cannot be empty.");
        }

        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException(
                    "Account type cannot be empty.");
        }

        if (openingBalance < 0 || accountRule < 0) {
            throw new InvalidAmountException(
                    "Opening balance and account rule cannot be negative.");
        }

        String accountType = type.trim().toLowerCase(Locale.ROOT);
        Account account;

        switch (accountType) {
            case "savings" ->
                account = new SavingsAccount(
                        ownerName, openingBalance, accountRule);

            case "current" ->
                account = new CurrentAccount(
                        ownerName, openingBalance, accountRule);

            case "fixed", "fixed deposit", "fixeddeposit" ->
                account = new FixedDepositAccount(
                        ownerName, openingBalance);

            default -> throw new IllegalArgumentException(
                    "Unsupported account type: " + type);
        }

        registerAccount(account);
        return account;
    }

    public Account findAccount(String accountNumber)
            throws AccountNotFoundException {

        if (accountNumber != null && !accountNumber.isBlank()) {
            Account account = repository.findById(
                    accountNumber.trim());

            if (account != null) {
                return account;
            }
        }

        throw new AccountNotFoundException(accountNumber);
    }

    public synchronized void deposit(
            String accountNumber, long amount) throws BankException {

        validateAmount(amount);

        Account account = findAccount(accountNumber);
        account.deposit(amount);

        repository.save(account);
        processor.addAccount(account);
    }

    public synchronized void withdraw(
            String accountNumber, long amount) throws BankException {

        validateAmount(amount);

        Account account = findAccount(accountNumber);
        account.withdraw(amount);

        repository.save(account);
        processor.addAccount(account);
    }

    public synchronized void transfer(
            String fromAccountNumber,
            String toAccountNumber,
            long amount) throws BankException {

        validateAmount(amount);

        Account sender = findAccount(fromAccountNumber);
        Account receiver = findAccount(toAccountNumber);

        if (sender == receiver) {
            throw new IllegalArgumentException(
                    "Choose two different accounts.");
        }

        sender.transfer(receiver, amount);

        repository.save(sender);
        repository.save(receiver);

        processor.addAccount(sender);
        processor.addAccount(receiver);
    }

    public String statement(String accountNumber)
            throws AccountNotFoundException {
        return findAccount(accountNumber).getStatement();
    }

    public List<Account> listAccounts() {
        return new ArrayList<>(repository.findAll());
    }

    public List<Account> searchByOwnerName(String ownerName) {
        List<Account> results = new ArrayList<>();

        if (ownerName == null || ownerName.isBlank()) {
            return results;
        }

        String searchName = ownerName.trim();

        for (Account account : repository.findAll()) {
            String existingName = account.getOwnerName();

            if (existingName != null
                    && existingName.equalsIgnoreCase(searchName)) {
                results.add(account);
            }
        }

        return results;
    }

    public Repository<Account> getRepository() {
        return repository;
    }

    public TransactionProcessor getProcessor() {
        return processor;
    }

    private void validateAmount(long amount)
            throws InvalidAmountException {

        if (!Validator.isValidAmount(Long.toString(amount))) {
            throw new InvalidAmountException(
                    "Amount must be a positive whole number.");
        }
    }
}
