package com.example.transferapp.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.transferapp.R;
import com.example.transferapp.utils.LogUtils;
import com.example.transferapp.utils.NetUtils;
import com.example.transferapp.utils.ToastUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppFragment extends Fragment {

    private Button getAppBtn = null;
    private static final String BACKUP_PATH = "/sdcard/1backup/";
    private static final String APK = ".apk";
    private PackageManager pm;
    private List<ResolveInfo> mApps = new ArrayList<>();
    public AppFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_app, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getAppBtn = getActivity().findViewById(R.id.getInstalledApp_btn);
        getAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInstalledApp();
            }
        });
    }

    private void getInstalledApp() {
      MyTask myTask = new MyTask();
      myTask.execute();
    }

    class MyTask extends AsyncTask<String, Integer, String> {

        // 方法1：onPreExecute（）
        // 作用：执行 线程任务前的操作
        @Override
        protected void onPreExecute() {
            // 执行前显示提示
            ToastUtil.showShort(getActivity(),"加载中");
        }


        // 方法2：doInBackground（）
        // 作用：接收输入参数、执行任务中的耗时操作、返回 线程任务执行的结果
        // 此处通过计算从而模拟“加载进度”的情况
        @Override
        protected String doInBackground(String... params) {
            ToastUtil.showShort(getActivity(),"正在运行...");
            try {
                publishProgress(1);
                queryApps();
                publishProgress(100);


            }catch (Exception e) {
                ToastUtil.showShort(getActivity(),"异步任务异常 "+e.getMessage());
                e.printStackTrace();
            }

            return null;
        }

        // 方法3：onProgressUpdate（）
        // 作用：在主线程 显示线程任务执行的进度
        @Override
        protected void onProgressUpdate(Integer... progresses) {
            ToastUtil.showShort(getActivity(),"拷贝进度 "+progresses[0]);

        }

        // 方法4：onPostExecute（）
        // 作用：接收线程任务执行结果、将执行结果显示到UI组件
        @Override
        protected void onPostExecute(String result) {
            // 执行完毕后，则更新UI
            ToastUtil.showShort(getActivity(),"拷贝完毕");
        }

        // 方法5：onCancelled()
        // 作用：将异步任务设置为：取消状态
        @Override
        protected void onCancelled() {
            ToastUtil.showShort(getActivity(),"已取消");
        }
    }

    //查询已安装的APP
    private void queryApps() {
        pm = getActivity().getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        if (resolveInfos != null && resolveInfos.size() > 0) {
            for (int i = 0; i < resolveInfos.size(); i++) {
                mApps.add(resolveInfos.get(i));
            }
        }
        if (mApps.size() > 0 && mApps != null) {
            for (int i = 0; i < mApps.size(); i++) {
                String dir = getApk(mApps.get(i).activityInfo.packageName);
                copyApk(mApps.get(i).activityInfo.name,dir);
            }
        }
    }

    private void copyApk(String name,String path){
        String dest = BACKUP_PATH + name + APK;
        LogUtils.d(dest);
        //path:app程序源文件路径  dest:新的存储路径  name:app名称
        new CopyRunnable(path, dest, name).run();
    }

    private String getApk(String packageName) {
        String appDir = null;
        try {
            //通过包名获取程序源文件路径
            appDir = getActivity().getPackageManager().getApplicationInfo(packageName, 0).sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appDir;
    }


    /**
     * 将程序源文件Copy到指定目录
     */
    private class CopyRunnable {
        private String source;
        private String dest;
        private String key;

        public CopyRunnable(String source, String dest, String key) {
            this.source = source;
            this.dest = dest;
            this.key = key;
        }

        public void run() {
            // TODO Auto-generated method stub
            try {
                int length = 1024 * 1024;
                if (!new File(BACKUP_PATH).exists()) {
                    boolean mk = new File(BACKUP_PATH).mkdirs();
                    if(mk){
                        System.out.println("true");
                    }
                }

                File fDest = new File(dest);
                if (fDest.exists()) {
                    fDest.delete();
                }
                fDest.createNewFile();
                FileInputStream in = new FileInputStream(new File(source));
                FileOutputStream out = new FileOutputStream(fDest);
                FileChannel inC = in.getChannel();
                FileChannel outC = out.getChannel();
                int i = 0;
                while (true) {
                    if (inC.position() == inC.size()) {
                        inC.close();
                        outC.close();
                        //成功
                        break;
                    }
                    if ((inC.size() - inC.position()) < 1024 * 1024) {
                        length = (int) (inC.size() - inC.position());
                    } else {
                        length = 1024 * 1024;
                    }
                    inC.transferTo(inC.position(), length, outC);
                    inC.position(inC.position() + length);
                    i++;
                }
            } catch (Exception e) {
                // TODO: handle exception
                LogUtils.e( e.toString());
            }
        }
    }


}
