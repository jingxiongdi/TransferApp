package com.example.transferapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.transferapp.fragment.AboutFragment;
import com.example.transferapp.fragment.TransferFragment;
import com.example.transferapp.utils.LogUtils;
import com.example.transferapp.utils.VersionUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView = null;
    private ViewPager viewPager = null;
    private MyPagerAdapter myPagerAdapter = null;
    private MenuItem menuItem = null;
    private static final int RC_PERMISSION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestNeedPermission();
        setViews();
    }

    private void requestNeedPermission() {
        if(VersionUtil.getSystemVersion() > Build.VERSION_CODES.LOLLIPOP_MR1){
            LogUtils.d("进行动态权限申请");
            if (!(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    || !(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            || !(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED)
                    || !(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    || !(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)){
                //没有权限，申请权限
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};
                //申请权限，其中RC_PERMISSION是权限申请码，用来标志权限申请的
                ActivityCompat.requestPermissions(MainActivity.this,permissions, RC_PERMISSION);

            }else {
                //拥有权限
            }

        }else{
            LogUtils.d("无需进行动态权限申请");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RC_PERMISSION && grantResults.length == 5
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED
                && grantResults[3] == PackageManager.PERMISSION_GRANTED
                && grantResults[4] == PackageManager.PERMISSION_GRANTED
        ) {
            LogUtils.d( "权限申请成功");
        }else {
            LogUtils.d("权限申请失败");
        }
    }


    private void setViews() {
        viewPager = findViewById(R.id.viewpager);
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(),3);
        viewPager.setAdapter(myPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                menuItem = bottomNavigationView.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        //设置导航栏菜单项Item选中监听
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                String title = item.getTitle().toString();
                //ToastUtil.showShort(MainActivity.this,title);
               // LogUtils.d("onNavigationItemSelected "+title);
                switch (item.getItemId()) {
                    case R.id.item_bottom_1:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.item_bottom_2:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.item_bottom_3:
                        viewPager.setCurrentItem(2);
                        break;
                }
                return true;
            }
        });

    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {
        //fragment的数量
        int nNumOfTabs;
        public MyPagerAdapter(FragmentManager fm, int nNumOfTabs) {
            super(fm);
            this.nNumOfTabs=nNumOfTabs;
        }

        /**
         * 重写getItem方法
         *
         * @param position 指定的位置
         * @return 特定的Fragment
         */
        @Override
        public Fragment getItem(int position) {
            //LogUtils.d("MyPagerAdapter getItem "+position);
            switch(position) {
                case 0:
                    TransferFragment tab1= TransferFragment.newInstance("TransferFragment","TransferFragment");
                    return tab1;
                case 1:
                    AboutFragment tab2= AboutFragment.newInstance("AboutFragment","AboutFragment");
                    return tab2;
                case 2:
                    TransferFragment tab3= TransferFragment.newInstance("333","ccc");
                    return tab3;
            }
            TransferFragment tab1= TransferFragment.newInstance("TransferFragment","TransferFragment");
            return tab1;
        }

        /**
         * 重写getCount方法
         *
         * @return fragment的数量
         */
        @Override
        public int getCount() {
            return nNumOfTabs;
        }
    }
}
