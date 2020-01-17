/*
 * Copyright (c) 2019. 上海感悟通信科技有限公司
 * All rights reserved
 * Project：LeapLinker
 * Last Modify：2019年12月16日 17:38:42
 * Author：上海感悟通信科技有限公司
 * http://www.sensethink.com
 */

package com.vily.vediodemo1;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Handler;

/**
 *  * description : appid 固定是1
 *  
 **/
public class BaseApplication extends Application {

    private static final String TAG = "BaseApplication";
    public static boolean start = false;

    private static Context mContext;
    //全局的handler
    private static Handler mHandler;
    //主线程
    private static Thread mMainThread;
    //主线程id
    private static int mMainThreadId;


    @Override
    public void onCreate() {
        super.onCreate();


        mContext = this;
        mHandler = new Handler();
        mMainThread = Thread.currentThread();
        mMainThreadId = android.os.Process.myTid();

    }

    public static Context getContext() {
        return mContext;
    }

    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        if (resources != null && resources.getConfiguration().fontScale != 1.0f) {
            Configuration configuration = resources.getConfiguration();
            configuration.fontScale = 1.0f;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
        return resources;
    }

}
