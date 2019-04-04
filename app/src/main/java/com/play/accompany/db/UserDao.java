package com.play.accompany.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.play.accompany.bean.UserInfo;

@Dao
public interface UserDao {

    @Insert
    long insert(UserInfo userInfo);

    @Delete
    int delete(UserInfo userInfo);

    @Update
    int update(UserInfo userInfo);

    @Query("select * from userinfo where user_id =:userId")
    UserInfo getUserInfo(String userId);

}
