package com.example.piotr.filmapp;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(1)
                .name("database")
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(config);
    }

}
