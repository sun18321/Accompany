package com.play.accompany.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.play.accompany.bean.FavoriteInfo;
import com.play.accompany.bean.UserInfo;

@Database(entities = {UserInfo.class, FavoriteInfo.class}, version = 2, exportSchema = false)
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
        return Room.databaseBuilder(context, AccompanyDatabase.class, DB_NAME).addMigrations(MIGRATION_1_2).build();
    }

    private static Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'UserInfo' ADD COLUMN 'show_id' TEXT");
        }
    };

    public abstract UserDao getUserDao();

    public abstract FavoriteDao getFavoriteDao();
}
