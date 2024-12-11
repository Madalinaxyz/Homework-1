package com.luxoft.bankapp.model;

import java.util.Objects;

public abstract class AbstractAccount {

    private long id;

    private double balance;

    public AbstractAccount() {
    }
    public AbstractAccount(double balance) {
        this.balance = balance;
    }
    public void deposit(double amount) {
        if (amount < 0) {
            return;
        }

        balance += amount;
    }

    public abstract void withdraw(double amount);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractAccount that = (AbstractAccount) o;
        return id == that.id &&
                Double.compare(that.balance, balance) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, balance);
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        builder
                .append("\n")
                .append("\n\tbalance = ")
                .append(balance);

        return builder.toString();
    }

    public double getBalance() {
        return balance;
    }

    void setBalance(double balance) {
        this.balance = balance;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
