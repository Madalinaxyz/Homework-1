package com.luxoft.bankapp.model;

import com.luxoft.bankapp.exceptions.OverDraftLimitExceededException;

public class CheckingAccount extends AbstractAccount {

    private double overdraft = 0;

    public CheckingAccount() {
    }

    public CheckingAccount(double overdraft) {

        setOverdraft(overdraft);
    }
    public CheckingAccount(double balance, double overdraft) { // New constructor
        super(balance); // Set balance
        setOverdraft(overdraft); // Set overdraft
    }

    public void setOverdraft(double amount) {

        if (overdraft < 0) {
            return;
        }

        overdraft = amount;
    }

    public double getOverdraft() {

        return overdraft;
    }

    @Override
    public void withdraw(double amount) throws OverDraftLimitExceededException {

        if (getBalance() + overdraft < amount) {

            throw new OverDraftLimitExceededException(
                    this.getClass().getSimpleName(), getBalance() + overdraft);
        }

        setBalance(getBalance() - amount);
    }
}
