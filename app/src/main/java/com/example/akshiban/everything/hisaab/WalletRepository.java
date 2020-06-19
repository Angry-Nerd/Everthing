package com.example.akshiban.everything.hisaab;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

public class WalletRepository {

    private static WalletDao walletDao;
    private LiveData<List<WalletMetaData>> allWallets;
    private static WalletRepository instance;


    public WalletRepository(Application application) {
        WalletDatabase walletDatabase = WalletDatabase.getInstance(application);
        walletDao = walletDatabase.walletDao();
        allWallets = walletDao.getAllWalletMetaDatas();
    }

    public void insert(WalletMetaData note) {
        new InsertWalletMetaDataAsyncTask(walletDao).execute(note);
    }

    public void update(WalletMetaData note) {
        new UpdateWalletMetaDataAsyncTask(walletDao).execute(note);
    }

    public void delete(WalletMetaData note) {
        new DeleteWalletMetaDataAsyncTask(walletDao).execute(note);
    }

    public void insertAll(List<WalletMetaData> metaData) {
        WalletMetaData[] walletMetaData = new WalletMetaData[metaData.size()];
        metaData.toArray(walletMetaData);
        new InsertAllMetadataAsyncTask(walletDao).execute(walletMetaData);
    }

    public void deleteAll() {
        new DeleteAllWalletMetaDataAsyncTask(walletDao).execute();
    }

    public LiveData<List<WalletMetaData>> getAllWalletMetaDatas() {
        return allWallets;
    }

    public static class InsertWalletMetaDataAsyncTask extends AsyncTask<WalletMetaData, Void, Void> {
        WalletDao walletDao;

        private InsertWalletMetaDataAsyncTask(WalletDao walletDao) {
            this.walletDao = walletDao;
        }
        @Override
        protected Void doInBackground(WalletMetaData... notes) {
            walletDao.insert(notes[0]);
            return null;
        }
    }

    public static class UpdateWalletMetaDataAsyncTask extends AsyncTask<WalletMetaData, Void, Void> {
        WalletDao walletDao;

        private UpdateWalletMetaDataAsyncTask(WalletDao walletDao) {
            this.walletDao = walletDao;
        }
        @Override
        protected Void doInBackground(WalletMetaData... notes) {
            walletDao.update(notes[0]);
            return null;
        }
    }

    public static class DeleteWalletMetaDataAsyncTask extends AsyncTask<WalletMetaData, Void, Void> {
        WalletDao walletDao;

        private DeleteWalletMetaDataAsyncTask(WalletDao walletDao) {
            this.walletDao = walletDao;
        }
        @Override
        protected Void doInBackground(WalletMetaData... notes) {
            walletDao.delete(notes[0]);
            return null;
        }
    }

    public static class DeleteAllWalletMetaDataAsyncTask extends AsyncTask<Void, Void, Void> {
        WalletDao walletDao;

        private DeleteAllWalletMetaDataAsyncTask(WalletDao walletDao) {
            this.walletDao = walletDao;
        }
        @Override
        protected Void doInBackground(Void... notes) {
            walletDao.deleteAllWalletMetaDatas();
            return null;
        }
    }

    public static class InsertAllMetadataAsyncTask extends AsyncTask<WalletMetaData, Void, Void> {
        WalletDao walletDao;

        private InsertAllMetadataAsyncTask(WalletDao walletDao) {
            this.walletDao = walletDao;
        }
        @Override
        protected Void doInBackground(WalletMetaData... mds) {
            walletDao.insertAll(mds);
            return null;
        }
    }








}
