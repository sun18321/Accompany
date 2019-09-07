package com.play.accompany.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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

