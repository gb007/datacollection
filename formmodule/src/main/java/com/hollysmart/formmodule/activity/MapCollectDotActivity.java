package com.hollysmart.formmodule.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.hollysmart.formmodule.R;
import com.hollysmart.formmodule.Utils.CCM_Delay;
import com.hollysmart.formmodule.Utils.LogUtils;
import com.hollysmart.formmodule.base.CaiBaseActivity;
import com.hollysmart.formmodule.common.Constants;

public class MapCollectDotActivity extends CaiBaseActivity {


    /**
     * 控件绑定
     */
    private ImageView iv_center;
    private TextView tv_mark_lat;
    private TextView tv_mark_lon;
    private TextView tv_save_loc;
    private MapView map;
    private AMap aMap;
    private Marker mapMarker;
    private LatLng locationLatLng;
    private boolean isWeixingModle = false;  //true 卫星地图模式  false 普通模式
    private String markerLatlng;//选中坐标点
    private String dataJson;//表单对象数据
    private String mark_lat;
    private String mark_lon;

    private AMap.OnMyLocationChangeListener myLocationChangeListener = new AMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LogUtils.d("定位点", location.getLatitude() + " , " + location.getLongitude());
            if (locationLatLng == null) {
                locationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            } else {
                locationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            }
        }
    };
    private AMap.OnPolylineClickListener polylineClickListener = new AMap.OnPolylineClickListener() {
        @Override
        public void onPolylineClick(Polyline polyline) {
        }
    };


    private AMap.OnMapLoadedListener onMapLoadedListener = new AMap.OnMapLoadedListener() {
        @Override
        public void onMapLoaded() {
            initPoints();
        }
    };

    private AMap.OnCameraChangeListener onCameraChangeListener = new AMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {

        }

        @Override
        public void onCameraChangeFinish(CameraPosition cameraPosition) {
            markerLatlng = cameraPosition.target.latitude + "," + cameraPosition.target.longitude;
            LogUtils.d("当前中心点：：", markerLatlng);
            mark_lat = cameraPosition.target.latitude + "";
            mark_lon = cameraPosition.target.longitude + "";
            tv_mark_lon.setText("经度：" + cameraPosition.target.longitude);
            tv_mark_lat.setText("纬度：" + cameraPosition.target.latitude);
        }
    };

    @Override
    public int layoutResID() {
        return R.layout.form_module_activity_map_collect_dot;
    }

    @Override
    public boolean setTranslucent() {
        return true;
    }

    @Override
    public void findView() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.bn_weixing).setOnClickListener(this);
        findViewById(R.id.bn_dingwei).setOnClickListener(this);
        findViewById(R.id.imagbtn_zoomIn).setOnClickListener(this);
        findViewById(R.id.imagbtn_zoomOut).setOnClickListener(this);
        findViewById(R.id.btn_chexiao).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);
        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.tv_save_loc).setOnClickListener(this);
        iv_center = findViewById(R.id.iv_center);
        tv_mark_lat = findViewById(R.id.tv_mark_lat);
        tv_mark_lon = findViewById(R.id.tv_mark_lon);
//        tv_save_loc = findViewById(R.id.tv_save_loc);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //由于个人信息保护法的实施，从地图8.1.0版本起对旧版本SDK不兼容，
        // 请务必确保调用SDK任何接口前先调用更新隐私合规updatePrivacyShow、updatePrivacyAgree两个接口
        MapsInitializer.updatePrivacyShow(this, true, true);
        MapsInitializer.updatePrivacyAgree(this, true);
        map = findViewById(R.id.map);
        map.onCreate(savedInstanceState);
        aMap = map.getMap();
        MyLocationStyle myLocationStyle;
        //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、
        // 且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle = new MyLocationStyle();
        //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.interval(2000);
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);

        myLocationStyle.strokeColor(R.color.form_module_transparent);
        myLocationStyle.radiusFillColor(R.color.form_module_transparent);

        //设置定位蓝点的Style
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.getUiSettings().setMyLocationButtonEnabled(false); //设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false
        aMap.addOnMyLocationChangeListener(myLocationChangeListener);
        aMap.addOnPolylineClickListener(polylineClickListener);

//        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(Constants.DEFAULT_LONGITUDE, Constants.DEFAULT_LATITUDE), 13, 0, 0));
//        aMap.moveCamera(mCameraUpdate);
        aMap.addOnMapLoadedListener(onMapLoadedListener);
        aMap.addOnCameraChangeListener(onCameraChangeListener);
        //隐藏高德地图右下角的缩放按钮
        aMap.getUiSettings().setZoomControlsEnabled(false);

    }

    @Override
    public void init() {
        markerLatlng = getIntent().getStringExtra("markerLatlng");
        dataJson = getIntent().getStringExtra("dataJson");
    }


    /**
     * 地图上初始化坐标点
     */
    private void initPoints() {

        if (!TextUtils.isEmpty(markerLatlng)) {
            String[] latLng = markerLatlng.split(",");
            LatLng markLatlng = new LatLng(new Double(latLng[0]), new Double(latLng[1]));
            drowMarkerInMap(markLatlng);
        } else {
            //位移到定位点
            new CCM_Delay(2000, new CCM_Delay.DelayIF() {
                @Override
                public void operate() {
                    CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(locationLatLng, 17, 0, 0));
                    aMap.animateCamera(mCameraUpdate);
                }
            });
        }
    }

    /**
     * 地图上画点或线
     */
    private void drowMarkerInMap(LatLng latLng) {

        if (latLng != null) {
            //画点
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.form_module_icon_map_mark);
            mapMarker = aMap.addMarker(new MarkerOptions().icon(bitmap).position(latLng));
            aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 17, 0, 0)));
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        map.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        map.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        map.onSaveInstanceState(outState);
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.iv_back) {
            finish();
        } else if (v.getId() == R.id.bn_weixing) {
            mapModle();
        } else if (v.getId() == R.id.bn_dingwei) {
            dingwei(aMap.getCameraPosition().zoom);
        } else if (v.getId() == R.id.imagbtn_zoomIn) {
            zoomIn();
        } else if (v.getId() == R.id.imagbtn_zoomOut) {
            zoomOut();
        } else if (v.getId() == R.id.tv_save_loc) {
            save();
        }
    }


    /**
     * 改变地图模式
     */
    private void mapModle() {
        if (isWeixingModle) {
            aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式
        } else {
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 地图模式
        }
        isWeixingModle = !isWeixingModle;
    }

    /**
     * 动态移动地图中心点到定位位置
     */
    private void dingwei(float zoom) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(locationLatLng, zoom, 0, 0));
        aMap.animateCamera(cameraUpdate);
    }

    /**
     * 放大地图级别
     */
    private void zoomIn() {
        aMap.moveCamera(CameraUpdateFactory.zoomIn());
    }

    /**
     * 缩小地图级别
     */
    private void zoomOut() {
        aMap.moveCamera(CameraUpdateFactory.zoomOut());
    }

    /**
     * 保存
     */
    private void save() {
        String strPoints = markerLatlng;
        Intent intent = new Intent();
        intent.putExtra("mark_lat", mark_lat);
        intent.putExtra("mark_lon", mark_lon);
        intent.putExtra("dataJson", dataJson);
        setResult(Constants.RESULT_CODE_MAP, intent);
        finish();
    }

}