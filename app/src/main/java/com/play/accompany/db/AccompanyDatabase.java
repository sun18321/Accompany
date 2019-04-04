package com.play.accompany.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.play.accompany.bean.UserInfo;

@Database(entities = UserInfo.class, version = 1, exportSchema = false)
public abstract class AccompanyDatabase extends RoomDatabase {
    private static final String DB_NAME = "accompany.db";
    private static volatile AccompanyDatabase sInstance;

    public static synchronized AccompanyDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = AccompanyDatabase.create(context);
        }
        return sInstance;
    }

    private static AccompanyDatabase create(Context context) {
        return Room.databaseBuilder(context, AccompanyDatabase.class, DB_NAME).build();
    }

    public abstract UserDao getUserDao();
}
