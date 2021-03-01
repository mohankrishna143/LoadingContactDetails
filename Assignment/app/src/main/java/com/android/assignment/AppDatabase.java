package com.android.assignment;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import Interface.UserListDao;
import Model.ProfileDetails;


@Database(entities = {ProfileDetails.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static  AppDatabase INSTANCE ;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "room_databse")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    public abstract UserListDao userDao();
}
