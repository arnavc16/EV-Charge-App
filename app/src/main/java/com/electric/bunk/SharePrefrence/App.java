package com.electric.bunk.SharePrefrence;

import android.app.Application;
import android.content.Context;

import com.omninos.util_data.AppPreferences;

public class App  extends Application {
    private Context context;

    private static AppPreferences appPreference;

    private static SingltonPojo sinltonPojo;

    public static App appInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;



        appPreference = AppPreferences.init(context, "Bunk");
        sinltonPojo=new SingltonPojo();
    }

    public static AppPreferences getAppPreference() {
        return appPreference;
    }


    public static SingltonPojo getSinltonPojo() {
        return sinltonPojo;
    }

}
