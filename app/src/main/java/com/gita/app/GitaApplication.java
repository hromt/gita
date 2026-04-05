package com.gita.app;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.gita.app.data.local.AppDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Provides a process-wide {@link AppDatabase} singleton and a single-thread executor for Room writes.
 */
public class GitaApplication extends Application {

    private AppDatabase database;
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate() {
        super.onCreate();
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "gita.db")
                .fallbackToDestructiveMigration()
                .build();
    }

    @NonNull
    public AppDatabase getDatabase() {
        return database;
    }

    @NonNull
    public ExecutorService getIoExecutor() {
        return ioExecutor;
    }
}
