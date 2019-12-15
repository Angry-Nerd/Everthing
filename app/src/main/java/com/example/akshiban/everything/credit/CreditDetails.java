package com.example.akshiban.everything.credit;

class CreditDetails {
    private String id, name;
    private int balance;

    public CreditDetails(String name, int balance) {
        this.name = name;
        this.balance = balance;
    }

    public CreditDetails() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
