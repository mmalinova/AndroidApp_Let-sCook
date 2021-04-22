package com.example.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// Create database
@Database(entities = {Product.class}, version = 1, exportSchema = false)
public abstract class RoomDB extends RoomDatabase {
    // Create db instance
    private static RoomDB database;
    // Define db name
    private static final String DATABASE_NAME = "LetsCookDB";

    public synchronized static RoomDB getInstance(Context context) {
        if (database == null) {
            // Initialize db
            database = Room.databaseBuilder(context.getApplicationContext(), RoomDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;
    }

    // DAO
    public abstract ProductDao productDao();
}
