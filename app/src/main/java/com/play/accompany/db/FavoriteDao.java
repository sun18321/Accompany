package com.play.accompany.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.play.accompany.bean.FavoriteInfo;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Insert
    List<Long> insertList(List<FavoriteInfo> list);

    @Insert
    long insertSingle(FavoriteInfo favoriteInfo);

    @Delete
    int delete(FavoriteInfo favoriteInfo);

    @Delete
    void deleteAll(List<FavoriteInfo> list);

    @Update
    void update(List<FavoriteInfo> list);

    @Query("select * from favoriteinfo where user_id =:userId")
    List<FavoriteInfo> getAllFavoriteByUserId(String userId);

}

