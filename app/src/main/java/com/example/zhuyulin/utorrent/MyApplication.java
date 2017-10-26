package com.example.zhuyulin.utorrent;

import android.app.Application;
import android.content.Context;

/**
 * Created by zhuyulin on 2017/10/18.
 */

public class MyApplication extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

}
