package com.example.akshiban.everything.hisaab;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

public class WalletTransaction {

    private String walletId;

    private long date;
    private String description;
    private int openingBal, closingBal, transactionCost;

    private String id;

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public WalletTransaction(long date, String description, int openingBal, int closingBal, int transactionCost, String walletId) {
        this.date = date;
        this.description = description;
        this.openingBal = openingBal;
        this.closingBal = closingBal;
        this.transactionCost = transactionCost;
        this.walletId = walletId;
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
