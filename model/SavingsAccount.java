
package model;

import java.io.Serializable;

public class SavingsAccount extends Account implements Premium, Serializable {
    private static final long serialVersionUID = 1L;

    private long minBalance;

    public SavingsAccount(String ownerName, long openingBalance, long minBalance) {
        super(ownerName, openingBalance);
        this.minBalance = minBalance;
    }

    @Override
    public double interestRate() {
        return 4.0;
    }

    @Override
    public boolean canWithdraw(long amount) {
        return amount > 0 && getBalance() - amount >= minBalance;
    }
}
