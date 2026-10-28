
package service;

import model.Account;
import exception.InvalidAmountException;

public class AccountWorker implements Runnable {

    private Account account;
    private int times;
    private long amount;

    public AccountWorker(Account account, int times, long amount) {
        this.account = account;
        this.times = times;
        this.amount = amount;
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        System.out.println(name + " started.");

        for (int i = 0; i < times; i++) {
            try {
                account.deposit(amount);
            } catch (InvalidAmountException e) {
                System.out.println(name + ": " + e.getMessage());
                return;
            }
        }

        System.out.println(name + " finished.");
    }
}
