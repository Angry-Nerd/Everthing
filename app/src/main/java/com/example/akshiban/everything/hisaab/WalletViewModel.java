package com.example.akshiban.everything.hisaab;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import java.util.List;

public class WalletViewModel extends AndroidViewModel {
    private LiveData<List<WalletMetaData>> roomData;
    private static WalletRepository repository;


    public WalletViewModel(@NonNull Application application) {
        super(application);
        repository = new WalletRepository(application);
        roomData = repository.getAllWalletMetaDatas();
    }


    public void addWalletToRoom(WalletMetaData metaData) {
        repository.insert(metaData);
    }

    public void insertAllWallets(List<WalletMetaData> walletMetaData) {
        repository.insertAll(walletMetaData);
    }

    public static void update(WalletMetaData metaData) {
        repository.update(metaData);
    }

    public static void delete(WalletMetaData walletMetaData) {
        repository.delete(walletMetaData);
    }

    public LiveData<List<WalletMetaData>> getRoomData() {
        return roomData;
    }

    public static void deleteAll() {
        repository.deleteAll();
    }

}
