package com.movies.app.moviesapp;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class CustomApplication extends Application {

    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
