package com.play.accompany.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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
