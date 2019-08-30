package com.revolut.rates;

import android.app.Application;
import android.content.Context;


public class RateApplication extends Application {

    private static Context context;

    public synchronized static Context getContext(){
        return  context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }
}
