package com.example.threaddownloads;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AppApplication extends Application {
    private static AppApplication mAxsApplication;
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mAxsApplication = this;
        this.mContext = getApplicationContext();
        /**
         * 优先且环境；或者在调用initPassport接口之前，调用
         * DxmAccountManager.getInstance().dxmConfigEnvironment(DxmEnvironmentType.DXM_ENVIRONMENT_QA);
         * 进行环境设置；默认是online环境
         */
    }



    public static AppApplication getInstance() {
        return mAxsApplication;
    }
    public static Context getContext() {
        return mContext;
    }

}
