package com.example.akshiban.everything.hisaab;

import android.app.Application;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {WalletMetaData.class}, version = 1)
public abstract class WalletDatabase extends RoomDatabase {

    private static WalletDatabase instance;

    public abstract WalletDao walletDao();

    public static synchronized WalletDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), WalletDatabase.class, "wallet_data").fallbackToDestructiveMigration().build();
        }
        return instance;
    }


}
