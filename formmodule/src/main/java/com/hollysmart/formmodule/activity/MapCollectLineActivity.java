package com.hollysmart.formmodule.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.blankj.utilcode.util.SizeUtils;
import com.google.gson.Gson;
import com.hjq.toast.ToastUtils;
import com.hollysmart.formmodule.R;
import com.hollysmart.formmodule.base.CaiBaseActivity;
import com.hollysmart.formmodule.bean.Postions;
import com.hollysmart.formmodule.common.Constants;

import java.util.ArrayList;
import java.util.List;

public class MapCollectLineActivity extends CaiBaseActivity {

    @Override
    public boolean setTranslucent() {
        return true;
    }

    @Override
    public int layoutResID() {
        return R.layout.activity_map_collect_line;
    }


    /**
     * 控件绑定
     */
    private ImageView iv_center;
    private TextView tv_save_loc;
    private MapView map;
    private AMap aMap;
    private LatLng locationLatLng;


    /**
     * 逻辑操作
     */
    private boolean isCheck;
    //传入绘制线条顶点位置
    private List<LatLng> points = new ArrayList<>();
    //新绘制线条顶点位置
    private List<LatLng> addLinePoints = new ArrayList<>();
    //存放所有新增点的经纬度
    private LatLngBounds.Builder addLineBoundsBuilder;
    private boolean isFrist = true;

    private String markerLatlng;//选中坐标点
    private String dataJson;//表单对象数据

    //传入的需绘制的坐标点
    private Postions postions;


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
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //由于个人信息保护法的实施，从地图8.1.0版本起对旧版本SDK不兼容，
        // 请务必确保调用SDK任何接口前先调用更新隐私合规updatePrivacyShow、updatePrivacyAgree两个接口
        MapsInitializer.updatePrivacyShow(this, true, true);
        MapsInitializer.updatePrivacyAgree(this, true);

        //初始化地图
        super.onCreate(savedInstanceState);
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

                if (hasMore) {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(locationLatLng, 17, 0, 0));
                    aMap.animateCamera(cameraUpdate);
                }
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


        markerLatlng = "{\n" +
                "    \"postions\":[\n" +
                "        {\n" +
                "            \"fd_lng\":\"116.291524\",\n" +
                "            \"fd_lnt\":\"39.974314\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"fd_lng\":\"116.306201\",\n" +
                "            \"fd_lnt\":\"39.975761\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"fd_lng\":\"116.308175\",\n" +
                "            \"fd_lnt\":\"39.962013\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"fd_lng\":\"116.294271\",\n" +
                "            \"fd_lnt\":\"39.958657\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"fd_lng\":\"116.289207\",\n" +
                "            \"fd_lnt\":\"39.96596\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"fd_lng\":\"116.291524\",\n" +
                "            \"fd_lnt\":\"39.974314\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";


        Gson gson = new Gson();
        postions = gson.fromJson(markerLatlng, Postions.class);


        if (isCheck) {
            findViewById(R.id.btn_add).setVisibility(View.GONE);
            findViewById(R.id.btn_chexiao).setVisibility(View.GONE);
            findViewById(R.id.btn_save).setVisibility(View.GONE);
            findViewById(R.id.tv_save_loc).setVisibility(View.GONE);
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
        } else if (id == R.id.tv_save_loc) {
            save();
        } else if (id == R.id.btn_add) {
            add();
        }
    }

    private boolean hasMore;

    private void initPoints() {
        //存放所有点的经纬度
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        addLineBoundsBuilder = new LatLngBounds.Builder();
        if (null != postions && postions.getPostions() != null && postions.getPostions().size() > 0) {
            List<Postions.Point> line_points = postions.getPostions();
            if (null != line_points && line_points.size() > 0) {
                for (int i = 0; i < line_points.size(); i++) {
                    Postions.Point point = line_points.get(i);
                    LatLng lineLatlng = new LatLng(new Double(point.getFdLnt()), new Double(point.getFdLng()));
                    points.add(lineLatlng);
                }
            }

            initLineInMap(boundsBuilder);
        } else {
            if (locationLatLng != null) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(locationLatLng, 17, 0, 0));
                aMap.animateCamera(cameraUpdate);
            } else {
                hasMore = true;
            }
        }
    }

    /**
     * 初始化已有线条
     *
     * @param boundsBuilder
     */
    private void initLineInMap(LatLngBounds.Builder boundsBuilder) {

        if (points.size() > 1) {
            //多个点时画线
            aMap.addPolyline(new PolylineOptions().addAll(points).width(10).color(R.color.form_module_color_map_line));
            if (boundsBuilder != null) {
                for (int i = 0; i < points.size(); i++) {
                    boundsBuilder.include(points.get(i));//把所有点都include进去（LatLng类型）
                }
                aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), SizeUtils.dp2px(60)));//第二个参数为四周留空宽度
            }

        } else if (points.size() > 0) {
            //一个点时画点
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.form_module_icon_map_mark);
            aMap.addMarker(new MarkerOptions().icon(bitmap).position(points.get(0)));
        }
    }


    /**
     * 地图上画点或线
     */
    private void drowLineInMap() {
        aMap.clear();
        initPoints();
        if (addLinePoints.size() > 1) {
            //多个点时画线
            aMap.addPolyline(new PolylineOptions().addAll(addLinePoints).width(10).color(R.color.form_module_color_map_add_line));
            if (addLineBoundsBuilder != null) {
                for (int i = 0; i < addLinePoints.size(); i++) {
                    addLineBoundsBuilder.include(addLinePoints.get(i));//把所有点都include进去（LatLng类型）
                }
                aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(addLineBoundsBuilder.build(), SizeUtils.dp2px(60)));//第二个参数为四周留空宽度
            }

        } else if (addLinePoints.size() > 0) {
            //一个点时画点
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.form_module_icon_map_mark);
            aMap.addMarker(new MarkerOptions().icon(bitmap).position(addLinePoints.get(0)));
        }
    }

    /***
     * 添加
     */
    private void add() {
        CameraPosition cameraPosition = aMap.getCameraPosition();
        LatLng latLng = cameraPosition.target;
        if (addLinePoints.contains(latLng)) {
            ToastUtils.show("已添加该坐标点");
            return;
        }
        addLinePoints.add(latLng);
        drowLineInMap();
    }


    /**
     * 撤销
     */
    private void cheXiao() {
        if (addLinePoints != null && addLinePoints.size() > 0) {
            addLinePoints.remove(addLinePoints.size() - 1);
            drowLineInMap();
        } else {
            if (addLinePoints == null || addLinePoints.size() == 0) {
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
        if (addLinePoints.size() > 1) {
            Postions postions = new Postions();
            List<Postions.Point> postions_points = new ArrayList<>();
            for (int i = 0; i < addLinePoints.size(); i++) {
                LatLng latLng = addLinePoints.get(i);
                Postions.Point point = new Postions.Point();
                point.setFdLng(latLng.longitude + "");
                point.setFdLnt(latLng.latitude + "");
                postions_points.add(point);
            }
            postions.setPostions(postions_points);
            Gson gson = new Gson();
            strPoints = gson.toJson(postions);

            Intent intent = new Intent();
            intent.putExtra("strPoints", strPoints);
            intent.putExtra("dataJson", dataJson);
            setResult(Constants.RESULT_CODE_MAP_LINE, intent);
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