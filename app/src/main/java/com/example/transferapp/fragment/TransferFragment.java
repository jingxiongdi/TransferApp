package com.example.transferapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.transferapp.R;
import com.example.transferapp.utils.FileUtil;
import com.example.transferapp.utils.LogUtils;
import com.example.transferapp.utils.NetUtils;
import com.example.transferapp.utils.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TransferFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RadioButton radioButtonSend = null;
    private RadioButton radioButtonGet = null;

    private LinearLayout clientLinear = null;
    private Button serverBtn = null;
    private Button connectBtn = null;
    private Button getFileBtn = null;
    private ScrollView scrollView = null;

    private TextView hintText = null;

    private EditText editIP = null;
    private StringBuilder stringBuilder = new StringBuilder();
    private static final int GET_FILE_CODE = 10;
    private String choosedFilePath = "";
    private String currentIp = "";

    public TransferFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TransferFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TransferFragment newInstance(String param1, String param2) {
        TransferFragment fragment = new TransferFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        //LogUtils.d("TransferFragment onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //LogUtils.d("TransferFragment onCreateView");
        return inflater.inflate(R.layout.fragment_transfer, container, false);
    }

    @Override
    public void onResume() {
        //LogUtils.d("TransferFragment onResume "+mParam1);
        super.onResume();

        setViews();
    }

    private void setViews() {
        scrollView = getActivity().findViewById(R.id.scrollView);
        clientLinear = getActivity().findViewById(R.id.client_linear);
        serverBtn = getActivity().findViewById(R.id.start_socket_btn);
        radioButtonSend = getActivity().findViewById(R.id.radioButton1);
        radioButtonGet = getActivity().findViewById(R.id.radioButton2);

        serverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetFileTask getFileTask = new GetFileTask();
                getFileTask.execute();
            }
        });

        radioButtonSend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    radioButtonGet.setChecked(false);
                    serverBtn.setVisibility(View.GONE);
                    clientLinear.setVisibility(View.VISIBLE);
                    getFileBtn.setVisibility(View.VISIBLE);
                }
            }
        });

        radioButtonGet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    radioButtonSend.setChecked(false);
                    clientLinear.setVisibility(View.GONE);
                    serverBtn.setVisibility(View.VISIBLE);
                    getFileBtn.setVisibility(View.GONE);
                }
            }
        });
        editIP = getActivity().findViewById(R.id.input_ip_edit);
        editIP.setText("192.168.0.10");
        connectBtn = getActivity().findViewById(R.id.connect_btn);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startConnectIp();
            }
        });

        hintText = getActivity().findViewById(R.id.hint_text);

        getFileBtn = getActivity().findViewById(R.id.get_file_btn);
        getFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//无类型限制
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, GET_FILE_CODE);

            }
        });
    }

    private void createServerSocket() throws Exception {
        LogUtils.d("createServerSocket----------");
        ServerSocket ss = new ServerSocket(NetUtils.PORT1);
        while (true){
            LogUtils.d("getInputStream----------");
            Socket socket = ss.accept();
            InputStream in = socket.getInputStream();
            int content;
            //装载文件名的数组
            byte[] c = new byte[1024];
            //解析流中的文件名
            for(int i=0;(content = in.read())!=-1;i++) {
                //表示文件名已经读取完毕
                if(content=='#'){
                    break;
                }
                c[i] = (byte) content;
            }

            //得到文件名
            String fileName = new String(c,"utf-8").trim();
            LogUtils.d("fileName----------"+fileName);
            File savedFile = FileUtil.saveFile(fileName);
            OutputStream outputStream = new FileOutputStream(savedFile);
            byte[] buf = new byte[1024];
            int len;
            //判断是否读到文件末尾
            while ((len = in.read(buf))!=-1){
                outputStream.write(buf,0,len);
            }
            outputStream.flush();
            outputStream.close();
            LogUtils.d("file trans success----------");
            OutputStream outputStream2 = socket.getOutputStream();
            outputStream2.write("save_success".getBytes());
            outputStream2.flush();
            outputStream2.close();
            socket.close();
            LogUtils.d("send callback success----------");
            break;
        }
        ss.close();

    }

    String path;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            String path = FileUtil.getPath(getActivity(),uri);
            LogUtils.d("path "+path);
            setHintText("文件路径为 "+path);
            choosedFilePath = path;
        }
    }

    private void setHintText(final String s){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stringBuilder.append(s+"\n");
                hintText.setText(stringBuilder.toString());
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        });

    }

    private void startConnectIp() {
        LogUtils.d("startConnectIp");
        if(TextUtils.isEmpty(editIP.getText())){
            ToastUtil.showShort(getActivity(),"输入IP不能为空");
            setHintText("输入IP不能为空");
        }else if(!NetUtils.isIP(editIP.getText().toString())){
            ToastUtil.showShort(getActivity(),"输入IP不合法");
            setHintText("输入IP不合法");
        }else {
            setHintText("正在尝试连接");
            currentIp =  editIP.getText().toString();
            MyTask myTask = new MyTask();
            myTask.execute();
        }
    }

    @Override
    public void onAttach(Context context) {
        //LogUtils.d("TransferFragment onAttach");
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        //LogUtils.d("TransferFragment onDetach");
        super.onDetach();

    }

    class MyTask extends AsyncTask<String, Integer, String> {

        // 方法1：onPreExecute（）
        // 作用：执行 线程任务前的操作
        @Override
        protected void onPreExecute() {
            // 执行前显示提示
            setHintText("加载中");
        }


        // 方法2：doInBackground（）
        // 作用：接收输入参数、执行任务中的耗时操作、返回 线程任务执行的结果
        // 此处通过计算从而模拟“加载进度”的情况
        @Override
        protected String doInBackground(String... params) {
            setHintText("正在运行...");
            try {
                publishProgress(1);
                String str = NetUtils.sendFile(choosedFilePath,currentIp,NetUtils.PORT1);
                publishProgress(100);
                setHintText("sendFile "+str);

            }catch (Exception e) {
                setHintText("异步任务异常 "+e.getMessage());
                e.printStackTrace();
            }

            return null;
        }

        // 方法3：onProgressUpdate（）
        // 作用：在主线程 显示线程任务执行的进度
        @Override
        protected void onProgressUpdate(Integer... progresses) {
            setHintText("进度 "+progresses[0]);

        }

        // 方法4：onPostExecute（）
        // 作用：接收线程任务执行结果、将执行结果显示到UI组件
        @Override
        protected void onPostExecute(String result) {
            // 执行完毕后，则更新UI
            setHintText("加载完毕");
        }

        // 方法5：onCancelled()
        // 作用：将异步任务设置为：取消状态
        @Override
        protected void onCancelled() {
            setHintText("已取消");
        }
    }

    class GetFileTask extends AsyncTask<String, Integer, String> {

        // 方法1：onPreExecute（）
        // 作用：执行 线程任务前的操作
        @Override
        protected void onPreExecute() {
            // 执行前显示提示
            setHintText("加载中");
        }


        // 方法2：doInBackground（）
        // 作用：接收输入参数、执行任务中的耗时操作、返回 线程任务执行的结果
        // 此处通过计算从而模拟“加载进度”的情况
        @Override
        protected String doInBackground(String... params) {
            try {
                publishProgress(1);
                try{
                    setHintText("正在接收文件 ");
                    createServerSocket();
                    publishProgress(100);
                    setHintText("接受文件完成 ");
                }catch (Exception e){
                    LogUtils.e(e.getMessage());
                    setHintText(e.getMessage());
                }


            }catch (Exception e) {
                setHintText("异步任务异常 "+e.getMessage());
                e.printStackTrace();
            }

            return null;
        }

        // 方法3：onProgressUpdate（）
        // 作用：在主线程 显示线程任务执行的进度
        @Override
        protected void onProgressUpdate(Integer... progresses) {
            setHintText("进度 "+progresses[0]);

        }

        // 方法4：onPostExecute（）
        // 作用：接收线程任务执行结果、将执行结果显示到UI组件
        @Override
        protected void onPostExecute(String result) {
            // 执行完毕后，则更新UI
            setHintText("加载完毕");
        }

        // 方法5：onCancelled()
        // 作用：将异步任务设置为：取消状态
        @Override
        protected void onCancelled() {
            setHintText("已取消");
        }
    }
}
