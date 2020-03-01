package com.example.transferapp.fragment;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.transferapp.R;
import com.example.transferapp.utils.LogUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class StepFragment extends Fragment implements SensorEventListener {

    private boolean isAvailable = false;
    private int sensorMode = -1;
    private SensorManager sensorManager = null;

    public StepFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_step, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        getSensorUseful();
    }

    private void getSensorUseful() {
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        Sensor detectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (sensorManager.registerListener(this, detectorSensor, SensorManager.SENSOR_DELAY_GAME)) {
            isAvailable = true;
            sensorMode = 0;
            LogUtils.d( "计步传感器Detector可用！");
        } else if (sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_GAME)) {
            isAvailable = true;
            sensorMode = 1;
            LogUtils.d(  "计步传感器Counter可用！");
        } else {
            isAvailable = false;
            LogUtils.d(  "计步传感器不可用！");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int liveStep = (int) event.values[0];
        if (sensorMode == 0) {
            LogUtils.d( "Detector步数："+liveStep);
        } else if (sensorMode == 1) {
            LogUtils.d( "Counter步数："+liveStep);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        LogUtils.d("onAccuracyChanged....");
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
