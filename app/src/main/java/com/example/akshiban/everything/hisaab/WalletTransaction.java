package com.example.akshiban.everything.hisaab;

public class WalletTransaction {

    private long date;
    private String description;
    private int openingBal, closingBal, transactionCost;
    private String id;

    public WalletTransaction(long date, String description, int openingBal, int closingBal, int transactionCost) {
        this.date = date;
        this.description = description;
        this.openingBal = openingBal;
        this.closingBal = closingBal;
        this.transactionCost = transactionCost;
    }

    public WalletTransaction() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOpeningBal() {
        return openingBal;
    }

    public void setOpeningBal(int openingBal) {
        this.openingBal = openingBal;
    }

    public int getClosingBal() {
        return closingBal;
    }

    public void setClosingBal(int closingBal) {
        this.closingBal = closingBal;
    }

    public int getTransactionCost() {
        return transactionCost;
    }

    public void setTransactionCost(int transactionCost) {
        this.transactionCost = transactionCost;
    }
}
