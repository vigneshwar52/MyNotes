package com.example.myapplication.Database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.myapplication.Models.Notes;

@Database(entities =
        Notes.class,
        version =2,
        exportSchema = false)

public abstract class RoomDB extends RoomDatabase {
    private static RoomDB database;
    public synchronized static RoomDB getInstance(Context context){
        if(database == null){
            String DATABASE_NAME = "notedb";
            database = Room.databaseBuilder(context.getApplicationContext(),
                    RoomDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;
    }
    public abstract MainDAObj mainDAObj();
}
