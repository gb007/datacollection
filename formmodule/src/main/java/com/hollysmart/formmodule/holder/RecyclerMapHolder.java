package com.hollysmart.formmodule.holder;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hollysmart.formmodule.R;
import com.hollysmart.formmodule.activity.MapCollectDotActivity;
import com.hollysmart.formmodule.activity.MapCollectDotsActivity;
import com.hollysmart.formmodule.activity.MapCollectLineActivity;
import com.hollysmart.formmodule.bean.DictionaryBean;
import com.hollysmart.formmodule.bean.FormFiledBean;
import com.hollysmart.formmodule.common.Constants;

import java.util.List;
import java.util.Map;

/**
 * 表单地图控件
 */
public class RecyclerMapHolder extends FormItemHolder {

    protected Context context;
    private TextView tv_bitian;
    private TextView tv_name;
    private EditText et_value;
    private TextView tv_tishi;
    private LinearLayout ll_value;
    private ImageView iv_arrorw;
    private View itemView;

    public RecyclerMapHolder(Context context, @NonNull View itemView) {
        super(itemView);
        this.context = context;
        this.itemView = itemView;
        tv_bitian = itemView.findViewById(R.id.tv_bitian);
        tv_name = itemView.findViewById(R.id.tv_name);
        et_value = itemView.findViewById(R.id.tv_value);
        tv_tishi = itemView.findViewById(R.id.tv_tishi);
        ll_value = itemView.findViewById(R.id.ll_value);
        iv_arrorw = itemView.findViewById(R.id.iv_arrorw);
    }


    @Override
    public void setData(FormFiledBean formFiledBean, Map<String, List<DictionaryBean>> dicMap, JsonObject jsonObject, boolean isCheck, RecyclerView.Adapter adapter) {

        initFormData(context, formFiledBean, tv_bitian, et_value, itemView);//初始化表单控件
        tv_name.setText("GPS");
        et_value.setText("");//默认为空
        if (null != jsonObject.get("location_lon") && null != jsonObject.get("location_lat")) {
            String lat = jsonObject.get("location_lat").getAsString();
            String lon = jsonObject.get("location_lon").getAsString();
            if (!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lon)) {
                et_value.setText(lon + "," + lat);
            }
        }

//        --------------------------常规逻辑---------------------------
//        tv_name.setText(formFiledBean.getDbFieldTxt());
//        tv_value.setHint(String.format("请输入%s", formFiledBean.getDbFieldTxt()));
//        if (null != jsonObject.get(formFiledBean.getDbFieldName())) {
//            String value = jsonObject.get(formFiledBean.getDbFieldName()).getAsString();
//            if (!TextUtils.isEmpty(value)) {
//                tv_value.setText(value);
//            } else {
//                tv_value.setText("");
//            }
//        }else{
//            tv_value.setText("");
//        }
//        --------------------------常规逻辑结束---------------------------

        et_value.setOnClickListener(v -> startMapActivity(jsonObject, isCheck));
        iv_arrorw.setOnClickListener(v -> startMapActivity(jsonObject, isCheck));
    }

    /**
     * 启动地图页面
     *
     * @param jsonObject 地点经纬度
     * @param isCheck    是否可编辑重设地点
     */
    private void startMapActivity(JsonObject jsonObject, boolean isCheck) {
        //定位权限申请
        Activity activity = (Activity) context;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {//通过GPS芯片接收卫星的定位信息，定位精度达10米以内
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {//通过WiFi或移动基站的方式获取用户错略的经纬度信息，定位精度大概误差在30~1500米
                //权限都存在
                String location_lon = jsonObject.get("location_lon").getAsString();
                String location_lat = jsonObject.get("location_lat").getAsString();
                String markerLatlng = "";
                if (!TextUtils.isEmpty(location_lon) && !TextUtils.isEmpty(location_lat)) {
                    markerLatlng = location_lat + "," + location_lon;
                }

                String personincharge = jsonObject.get("personincharge").getAsString();


                if (!TextUtils.isEmpty(personincharge)) {

                    Gson gson = new Gson();
                    String dataJson = gson.toJson(jsonObject);
                    Intent intent = new Intent(context, MapCollectDotsActivity.class);
                    intent.putExtra("markerLatlng", markerLatlng);
                    intent.putExtra("isCheck", isCheck);
                    intent.putExtra("dataJson", dataJson);
                    activity.startActivityForResult(intent, Constants.REQUEST_CODE_MAP);
                } else {

                    Gson gson = new Gson();
                    String dataJson = gson.toJson(jsonObject);
                    Intent intent = new Intent(context, MapCollectLineActivity.class);
                    intent.putExtra("markerLatlngs", markerLatlng);
                    intent.putExtra("isCheck", isCheck);
                    intent.putExtra("dataJson", dataJson);
                    activity.startActivityForResult(intent, Constants.REQUEST_CODE_MAP);

                }


            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 3);
            }
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);
        }
    }
}
