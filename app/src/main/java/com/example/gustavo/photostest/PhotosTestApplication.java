package com.example.gustavo.photostest;

import android.content.Context;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.example.gustavo.photostest.controllers.ApiController;
import com.example.gustavo.photostest.utils.BusProvider;
import com.squareup.otto.Bus;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;

/**
 * Created by gustavomedina on 11/04/17.
 */

public class PhotosTestApplication extends MultiDexApplication
        implements Thread.UncaughtExceptionHandler {

    public static final String BASE_URL = "https://api.myjson.com/";

    private static PhotosTestApplication instance;
    private final Thread.UncaughtExceptionHandler ueh;
    private static Bus mBus;
    private static RealmConfiguration realmConfig;

    public static PhotosTestApplication getInstance() {
        return instance;
    }

    public PhotosTestApplication() {
        this.ueh = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        mBus = BusProvider.getInstance();

        ApiController.init(BASE_URL);

        realmConfig = new RealmConfiguration.Builder(this).build();

        realmConfig = new RealmConfiguration.Builder(this)
                .name("app.realm")
                .modules(Realm.getDefaultModule())
                .build();

        Realm.setDefaultConfiguration(realmConfig);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static synchronized Realm getRealmInstance() {
        try {
            return Realm.getInstance(realmConfig);
        } catch (RealmMigrationNeededException e){
            try {
                Realm.deleteRealm(realmConfig);
                //Realm file has been deleted.
                return Realm.getInstance(realmConfig);
            } catch (Exception ex){
                throw ex;
                //No Realm file to remove.
            }
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        this.ueh.uncaughtException(thread, ex);
    }

    public static Bus getBusProvider() {
        return mBus;
    }
}

