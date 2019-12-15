package com.example.akshiban.everything.hisaab;

import android.os.Parcel;
import android.os.Parcelable;

class WalletMetaData implements Parcelable {
    private String walletName;
    private int currentBalance;
    private int initialBalance;

    protected WalletMetaData(Parcel in) {
        walletName = in.readString();
        currentBalance = in.readInt();
        initialBalance = in.readInt();
        id = in.readString();
    }

    public static final Creator<WalletMetaData> CREATOR = new Creator<WalletMetaData>() {
        @Override
        public WalletMetaData createFromParcel(Parcel in) {
            return new WalletMetaData(in);
        }

        @Override
        public WalletMetaData[] newArray(int size) {
            return new WalletMetaData[size];
        }
    };

    public int getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(int initialBalance) {
        this.initialBalance = initialBalance;
    }

    private String id;

    public WalletMetaData(String walletName, int balance, int initialBalance) {
        this.walletName = walletName;
        this.currentBalance = balance;
        this.initialBalance = initialBalance;
    }




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public WalletMetaData() {
    }


    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public int getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(int currentBalance) {
        this.currentBalance = currentBalance;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(walletName);
        dest.writeInt(currentBalance);
        dest.writeInt(initialBalance);
        dest.writeString(id);
    }
}
