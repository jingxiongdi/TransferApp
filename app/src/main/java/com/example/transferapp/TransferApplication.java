package com.example.transferapp;

import android.app.Application;

import com.example.transferapp.utils.LogUtils;

public class TransferApplication extends Application {
    /**
     * 1.底部菜单栏
     */
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d("app启动=========");
    }
}
