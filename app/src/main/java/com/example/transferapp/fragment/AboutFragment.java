package com.example.transferapp.fragment;


import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.transferapp.R;
import com.example.transferapp.utils.LocationUtils;
import com.example.transferapp.utils.LogUtils;
import com.example.transferapp.utils.ToastUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button locationBtn = null;
    private Button locationNetwork = null;
    private Button locationGPS = null;


    public AboutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AboutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AboutFragment newInstance(String param1, String param2) {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // LogUtils.d("AboutFragment onCreate");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       // LogUtils.d("AboutFragment onCreateView");
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        locationBtn = getActivity().findViewById(R.id.location_btn);
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d("getLocation=======");
                Location location = LocationUtils.getBestLocation(getActivity(),null);
                String msg = "location 纬度 "+location.getLatitude()+" 经度 "+location.getLongitude()+" 海拔 "+location.getAltitude();
                LogUtils.d(msg);
                ToastUtil.showShort(getActivity(),msg);
            }
        });

        locationNetwork = getActivity().findViewById(R.id.location_network_btn);
        locationNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d("location_network_btn onClick");
                Location location =LocationUtils.getNetWorkLocation(getActivity());
                String msg = "location 纬度 "+location.getLatitude()+" 经度 "+location.getLongitude()+" 海拔 "+location.getAltitude();
                LogUtils.d(msg);
                ToastUtil.showShort(getActivity(),msg);
            }
        });

        locationGPS = getActivity().findViewById(R.id.location_gps_btn);
        locationGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d("location_gps_btn onClick");
                Location location =LocationUtils.getGPSLocation(getActivity());
                if(location==null){
                    LogUtils.d("location==null");
                    ToastUtil.showShort(getActivity(),"location==null");
                    //设置定位监听，因为GPS定位，第一次进来可能获取不到，通过设置监听，可以在有效的时间范围内获取定位信息
                    LocationUtils.addLocationListener(getActivity(), LocationManager.GPS_PROVIDER, new LocationUtils.ILocationListener() {
                        @Override
                        public void onSuccessLocation(Location location) {
                            if (location != null) {
                                LogUtils.d("gps onSuccessLocation location:  lat==" + location.getLatitude() + "     lng==" + location.getLongitude());
                                ToastUtil.showShort(getActivity(), "gps onSuccessLocation location:  lat==" + location.getLatitude() + "     lng==" + location.getLongitude());
                            } else {
                                LogUtils.d("gps location is null");
                                ToastUtil.showShort(getActivity(),"gps location is null");
                            }
                        }
                    });
                    return;
                }
                String msg = "location 纬度 "+location.getLatitude()+" 经度 "+location.getLongitude()+" 海拔 "+location.getAltitude();
                LogUtils.d(msg);
                ToastUtil.showShort(getActivity(),msg);
            }
        });
    }

}
