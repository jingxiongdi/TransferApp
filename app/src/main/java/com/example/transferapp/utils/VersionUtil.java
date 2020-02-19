package com.example.transferapp.utils;

public class VersionUtil {
    /**
     * 获取当前手机系统版本号
     *
     * @return  系统版本号
     */
    public static int getSystemVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }
}
