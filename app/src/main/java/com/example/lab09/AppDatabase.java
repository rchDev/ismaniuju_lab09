package com.example.lab09;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {GeoData.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract GeoDataDao geoDataDao();
    private static volatile AppDatabase INSTANCE;
    public static AppDatabase get(Context context) {
        synchronized (AppDatabase.class) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        "geoData.db"
                ).allowMainThreadQueries()
                        .build();
            }
        }
        return INSTANCE;
    }
}
