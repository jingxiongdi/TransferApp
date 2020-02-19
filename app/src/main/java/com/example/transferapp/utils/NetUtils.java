package com.example.transferapp.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetUtils {
    public static final int PORT1 = 8888;
    public static boolean isIP(String addr)
    {
        if(addr.length() < 7 || addr.length() > 15 || "".equals(addr))
        {
            return false;
        }
        /**
         * 判断IP格式和范围
         */
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

        Pattern pat = Pattern.compile(rexp);

        Matcher mat = pat.matcher(addr);

        boolean ipAddress = mat.find();

        return ipAddress;
    }


    public static String sendFile(String path, String ipAddress, int port) {
        try {
            File file = new File(path);
            SocketAddress socketAddress = new InetSocketAddress(ipAddress, port);
            Socket data = new Socket();
            data.connect(socketAddress,5000);
            OutputStream outputData = data.getOutputStream();
            outputData.write((file.getName()+"#").getBytes());

            FileInputStream fileInput = new FileInputStream(path);
            int size = -1;
            byte[] buffer = new byte[1024];
            while ((size = fileInput.read(buffer, 0, 1024)) != -1) {
                outputData.write(buffer, 0, size);
            }
            outputData.close();
            fileInput.close();
            data.close();
            return file.getName() + " 发送完成";
        } catch (Exception e) {
            return "发送错误:\n" + e.getMessage();
        }
    }
}
