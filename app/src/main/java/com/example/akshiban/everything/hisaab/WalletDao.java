package com.example.akshiban.everything.hisaab;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface WalletDao {

    @Insert
    void insert(WalletMetaData note);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(WalletMetaData... metaData);

    @Update
    void update(WalletMetaData note);

    @Delete
    void delete(WalletMetaData note);

    @Query("DELETE FROM WalletMetaData")
    void deleteAllWalletMetaDatas();

    @Query("SELECT * FROM WalletMetaData ORDER BY currentBalance desc")
    LiveData<List<WalletMetaData>> getAllWalletMetaDatas();

}

