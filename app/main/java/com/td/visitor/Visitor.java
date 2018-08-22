package com.td.visitor;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by Glenn on 4/10/2018.
 */
public class Visitor extends Application {

    @Override
    public void onCreate() {

        Firebase.setAndroidContext(this);
        super.onCreate();
    }
}
