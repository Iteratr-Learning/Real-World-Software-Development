package com.iteratrlearning.shu_book.chapter_02;

import java.time.LocalDate;

public class BankStatement {
    private final LocalDate date;
    private final double amount;
    private final String description;


    public BankStatement(LocalDate date, double amount, String description) {
        this.date = date;
        this.amount = amount;
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "BankStatement{" +
                "date=" + date +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BankStatement that = (BankStatement) o;

        if (Double.compare(that.getAmount(), getAmount()) != 0) return false;
        if (!getDate().equals(that.getDate())) return false;
        return getDescription().equals(that.getDescription());
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getDate().hashCode();
        temp = Double.doubleToLongBits(getAmount());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + getDescription().hashCode();
        return result;
    }
}
