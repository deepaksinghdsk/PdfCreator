package com.example.PdfCreator;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.PdfCreator.imagePicker.image_picker_adapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    //private String tag = "MainActivity";
    Context c;
    Auto_update_service service;
    private boolean bind;
    //Fragment fragment1, fragment2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the height of the screen
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        image_picker_adapter.screenHeight = screenHeight;

        c = this.getApplicationContext();
        BottomNavigationView navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navView = findViewById(R.id.bottomNav);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_create)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        Intent auto_update_service = new Intent(this, Auto_update_service.class);
        if (Auto_update_service.connected)
            startService(auto_update_service);
        bindService(auto_update_service, connection, BIND_NOT_FOREGROUND);

    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            Auto_update_service.myLocalBinder binder = (Auto_update_service.myLocalBinder) iBinder;
            service = binder.getService();
            bind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bind = false;
        }
    };

    @Override
    protected void onDestroy() {
        if (bind)
            unbindService(connection);
        super.onDestroy();
    }
}
