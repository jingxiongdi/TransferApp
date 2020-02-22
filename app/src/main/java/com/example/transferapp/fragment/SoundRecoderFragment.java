package com.example.transferapp.fragment;


import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.transferapp.R;
import com.example.transferapp.utils.LogUtils;
import com.example.transferapp.utils.ToastUtil;
import com.example.transferapp.utils.WeakHandler;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class SoundRecoderFragment extends Fragment {
    private Button startRecordBtn = null;
    private Button stopRecordBtn = null;
    private ButtonClickListener buttonClickListener = new ButtonClickListener();
    private boolean bStarted = false;

    private MediaRecorder mr = null;
    private TextView hintText = null;
    private long recordTime = 0;
    private WeakHandler handler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 0:
                    recordTime++;
                    hintText.setText("正在录音"+recordTime+"秒");
                    handler.sendEmptyMessageDelayed(0,1000);
                    break;
            }
            return false;
        }
    });

    public SoundRecoderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sound_recoder, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        setViews();
    }

    private void setViews() {
        startRecordBtn = getActivity().findViewById(R.id.start_record);
        stopRecordBtn = getActivity().findViewById(R.id.stop_record);

        startRecordBtn.setOnClickListener(buttonClickListener);
        stopRecordBtn.setOnClickListener(buttonClickListener);
        hintText = getActivity().findViewById(R.id.show_hint);
    }

    public class ButtonClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.start_record:
                    LogUtils.d("start_record");
                    if(bStarted){
                        ToastUtil.showShort(getActivity(),"已经在录音了");
                    }else {
                        startRecord();
                        hintText.setText("正在录音");
                        handler.sendEmptyMessageDelayed(0,1000);
                    }
                    bStarted = true;
                    break;
                case R.id.stop_record:
                    LogUtils.d("stop_record");
                    if(bStarted){
                        stopRecord();
                        handler.removeMessages(0);
                        hintText.setText("已停止录音,录音文件保存在/sdcard/1Asounds");
                    }else {
                        ToastUtil.showShort(getActivity(),"还没开始录音呢");
                    }
                    bStarted = false;
                    break;
            }
        }
    }


    //开始录制
    private void startRecord(){
        if(mr == null){
            File dir = new File(Environment.getExternalStorageDirectory(),"1Asounds");
            if(!dir.exists()){
                dir.mkdirs();
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String time = simpleDateFormat.format(new Date());
            File soundFile = new File(dir,time+".amr");
            if(!soundFile.exists()){
                try {
                    soundFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            mr = new MediaRecorder();
            mr.setAudioSource(MediaRecorder.AudioSource.MIC);  //音频输入源
            mr.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);   //设置输出格式
            mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);   //设置编码格式
            mr.setOutputFile(soundFile.getAbsolutePath());
            try {
                mr.prepare();
                mr.start();  //开始录制
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    //停止录制，资源释放
    private void stopRecord(){
        if(mr != null){
            mr.stop();
            mr.release();
            mr = null;
        }
    }


}
