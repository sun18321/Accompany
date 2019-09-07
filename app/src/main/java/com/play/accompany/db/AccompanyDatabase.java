package com.play.accompany.db;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import android.content.Context;
import androidx.annotation.NonNull;

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
