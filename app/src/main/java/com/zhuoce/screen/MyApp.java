package com.zhuoce.screen;

import android.app.Application;
import android.util.Log;

public class MyApp extends Application {
    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i("MyApp", "执行了方法");
    }
}
