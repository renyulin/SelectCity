package com.selectcity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button getLocation;
    private Button selectCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getLocation = findViewById(R.id.getLocation);
        getLocation.setOnClickListener(this);
        selectCity = findViewById(R.id.selectCity);
        selectCity.setOnClickListener(this);
    }

    /**
     * 调用本地GPS来获取经纬度
     */
    private void getLocation() {
        String locationProvider;
        //1.获取位置管理器
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //2.获取位置提供器，GPS或是NetWork
        List<String> providers = locationManager.getAllProviders();
        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是网络定位
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS定位
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.PASSIVE_PROVIDER)) {
            //如果是PASSIVE定位
            locationProvider = LocationManager.PASSIVE_PROVIDER;
        } else {
//            Toast.makeText(context, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            return;
        }

        //3.获取上次的位置，一般第一次运行，此值为null   获取一个就行
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            showLocation(location);
        } else {
            // 监视地理位置变化，第二个和第三个参数分别为更新的最短时间minTime和最短距离minDistace
            locationManager.requestLocationUpdates(locationProvider, 30, 5, mListener);
        }
    }


    LocationListener mListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        // 如果位置发生变化，重新显示
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }
    };

    /**
     * 获取经纬度
     *
     * @param location
     */
    private void showLocation(Location location) {
        String longtitude = String.valueOf(location.getLongitude());
        String latitude = String.valueOf(location.getLatitude());
        getAddress(location);
    }

    // 获取地址信息
    private List<Address> getAddress(Location location) {
        //用来接收位置的详细信息
        List<Address> result = null;
        String city = "";
        try {
            if (location != null) {
                Geocoder gc = new Geocoder(this, Locale.getDefault());
                result = gc.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);
                for (int i = 0; i < result.size(); i++) {
                    Address address = result.get(i);
                    city = address.toString();

                    //
                    //getAdminArea     省
                    //getLocality      沈阳市
                    //getSubLocality   和平区
                    //getThoroughfare  11纬路
                }
                getLocation.setText(city);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getLocation:
                getLocation();
                break;
            case R.id.selectCity:
                selectCity();
                break;
        }
    }

    private void selectCity() {
        SelectAddressPopWindow popWindow = new SelectAddressPopWindow(this);
        popWindow.setAddress("辽宁", "沈阳市", "和平区");
        popWindow.showAtLocation(selectCity, Gravity.BOTTOM, 0, 0);
        popWindow.setAddresskListener(new SelectAddressPopWindow.OnAddressCListener() {
            @Override
            public void onClick(String province, String city, String area) {
                selectCity.setText(province + "-" + city + "-" + area);
            }
        });
    }
}
