package com.hollysmart.formmodule.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.blankj.utilcode.util.SizeUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import com.hjq.toast.ToastUtils;
import com.hollysmart.formmodule.R;
import com.hollysmart.formmodule.base.CaiBaseActivity;
import com.hollysmart.formmodule.bean.GPS;
import com.hollysmart.formmodule.common.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapCollectPlaneActivity extends CaiBaseActivity {

    @Override
    public boolean setTranslucent() {
        return true;
    }

    @Override
    public int layoutResID() {
        return R.layout.activity_map_collect_plane;
    }

    /**
     * 控件绑定
     */
    private ImageView iv_center;

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
        iv_center = findViewById(R.id.iv_center);
    }

    private MapView map;
    private AMap aMap;
    //    private LatLng treeLatlng;
    private Marker treeMarker;
    private LatLng locationLatLng;

    //线条顶点位置
    private List<LatLng> points = new ArrayList<>();
    private String markerLatlng;//坐标点
    private String dataJson;//表单对象数据
    private boolean isCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //初始化地图
        super.onCreate(savedInstanceState);
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

        aMap.addOnMapLoadedListener(onMapLoadedListener);

    }

    private AMap.OnMyLocationChangeListener myLocationChangeListener = new AMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
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


    @Override
    public void init() {
        isCheck = getIntent().getBooleanExtra("isCheck", false);
        markerLatlng = getIntent().getStringExtra("markerLatlngs");
        dataJson = getIntent().getStringExtra("dataJson");

        if (isCheck) {
            findViewById(R.id.btn_add).setVisibility(View.GONE);
            findViewById(R.id.btn_chexiao).setVisibility(View.GONE);
            findViewById(R.id.btn_save).setVisibility(View.GONE);
            findViewById(R.id.bn_dingwei).setVisibility(View.GONE);
            iv_center.setVisibility(View.GONE);
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
        int id = v.getId();
        if (id == R.id.iv_back) {
            finish();
        } else if (id == R.id.bn_weixing) {
            mapModle();
        } else if (id == R.id.bn_dingwei) {
            dingwei(aMap.getCameraPosition().zoom);
        } else if (id == R.id.imagbtn_zoomIn) {
            zoomIn();
        } else if (id == R.id.imagbtn_zoomOut) {
            zoomOut();
        } else if (id == R.id.btn_chexiao) {
            cheXiao();
        } else if (id == R.id.btn_save) {
            save();
        } else if (id == R.id.btn_add) {
            add();
        }
    }

    private void initPoints() {
        String propertyLabel = markerLatlng;
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();//存放所有点的经纬度
        if (!TextUtils.isEmpty(propertyLabel)) {
            if (propertyLabel.contains("|")) {
                String[] str_ranges = propertyLabel.split("\\|");
                for (int i = 0; i < str_ranges.length; i++) {
                    String[] latLng = str_ranges[i].split(",");
                    LatLng latLng1 = new LatLng(new Double(latLng[0]), new Double(latLng[1]));
                    points.add(latLng1);
                }
            } else {
                String[] latLng = propertyLabel.split(",");
                LatLng latLng1 = new LatLng(new Double(latLng[0]), new Double(latLng[1]));
                points.add(latLng1);
            }
            drowPlane(boundsBuilder);

            //画道路线
            drowLineInMap(null);
        } else {
            //画道路线
            drowLineInMap(boundsBuilder);
        }
    }

    /**
     * 地图上画点或线
     */
    private void drowPlane(LatLngBounds.Builder boundsBuilder) {
        aMap.clear();
        if (points.size() > 2) {
            //多个点时画面
            aMap.addPolygon(new PolygonOptions().addAll(points).strokeColor(R.color.form_module_color_map_line).fillColor(R.color.form_module_color_map_fill));
            if (boundsBuilder != null) {
                for (int i = 0; i < points.size(); i++) {
                    boundsBuilder.include(points.get(i));//把所有点都include进去（LatLng类型）
                    aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), SizeUtils.dp2px(120)));//第二个参数为四周留空宽度
                }
            }

        } else if (points.size() > 0) {
            //一个点时画点
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.form_module_resflag_add);
            for (LatLng latLng : points) {
                aMap.addMarker(new MarkerOptions().icon(bitmap).position(latLng));
                if (boundsBuilder != null) {
                    boundsBuilder.include(latLng);//把所有点都include进去（LatLng类型）
                    aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), SizeUtils.dp2px(120)));//第二个参数为四周留空宽度

                }
            }
        }
    }

    /**
     * 地图上画 道路的线
     */
    private void drowLineInMap(LatLngBounds.Builder boundsBuilder) {
        String propertyLabel = null;
        if (!TextUtils.isEmpty(propertyLabel)) {
            String[] gpsGroups = propertyLabel.split("\\|");
            List<LatLng> points = new ArrayList<>();
            for (int j = 0; j < gpsGroups.length; j++) {
                String firstGps = gpsGroups[j];
                String[] split = firstGps.split(",");
                GPS gps = new GPS(new Double(split[0]), new Double(split[1]));
                LatLng p1 = new LatLng(gps.getLat(), gps.getLon());
                points.add(p1);
            }
            if (points.size() > 1) {
                aMap.addPolyline(new PolylineOptions().addAll(points).width(10).color(R.color.form_module_color_map_line));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(locationLatLng, 16, 0, 0));
                aMap.animateCamera(cameraUpdate);
            }
        }
    }

    /***
     * 添加
     */
    private void add() {
        CameraPosition cameraPosition = aMap.getCameraPosition();
        LatLng latLng = cameraPosition.target;
        if (points.contains(latLng)) {
            ToastUtils.show("已添加该坐标点");
            return;
        }
        points.add(latLng);
        drowPlane(null);
    }

    /**
     * 撤销
     */
    private void cheXiao() {
        if (points != null && points.size() > 0) {
            points.remove(points.size() - 1);
            drowPlane(null);
        } else {
            if (points == null || points.size() == 0) {
                ToastUtils.show("暂无坐标点可撤销");
                return;
            }
        }
    }

    /**
     * 保存
     */
    private void save() {
        String strPoints = "";
        if (points.size() > 1) {
            for (LatLng latLng : points) {
                if (TextUtils.isEmpty(strPoints)) {
                    strPoints = latLng.latitude + "," + latLng.longitude;
                } else {
                    strPoints = strPoints + "|" + latLng.latitude + "," + latLng.longitude;
                }
            }
            Intent intent = new Intent();
            intent.putExtra("strPoints", strPoints);
            intent.putExtra("dataJson", dataJson);
            setResult(Constants.RESULT_CODE_MAP_PLANE, intent);
            finish();
        } else {
            ToastUtils.show("线最少需要2个点");
            return;
        }
    }

    private boolean isWeixingModle = false;  //true 卫星地图模式  false 普通模式

    /**
     * 改变地图模式
     */
    private void mapModle() {
        if (isWeixingModle) {
            aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式
        } else {
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 卫星地图模式
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
}