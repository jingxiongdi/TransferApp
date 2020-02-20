package com.example.transferapp;

import android.app.Application;

import com.example.transferapp.utils.LogUtils;
import com.tencent.bugly.crashreport.CrashReport;

public class TransferApplication extends Application {
    /**
     * 1.底部菜单栏
     */
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d("app启动=========");
        CrashReport.initCrashReport(getApplicationContext(), "62b16e4cf6", false);
    }
}
