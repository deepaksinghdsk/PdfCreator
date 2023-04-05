package com.example.PdfCreator;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Auto_update_service extends Service {

    static boolean connected = false;
    private bgThread bgThread;
    private String tag = "Class service";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new myLocalBinder();
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work doesn't disrupt our UI.
        bgThread = new bgThread();
        connected = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bgThread.execute(startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        connected = false;
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(tag, "onUnbind is called");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(tag, "onRebind is called");
        super.onRebind(intent);
    }

    class myLocalBinder extends Binder {
        Auto_update_service getService() {
            return Auto_update_service.this;
        }
    }

    private static final class bgThread extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... startIds) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference databaseRef = database.getReference();

            return null;
        }
    }
}
