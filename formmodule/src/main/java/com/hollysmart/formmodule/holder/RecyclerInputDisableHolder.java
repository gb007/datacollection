package com.hollysmart.formmodule.holder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.hollysmart.formmodule.R;
import com.hollysmart.formmodule.bean.DictionaryBean;
import com.hollysmart.formmodule.bean.FormFiledBean;

import java.util.List;
import java.util.Map;

/**
 * 表单不可编辑输入框控件
 */
public class RecyclerInputDisableHolder extends FormItemHolder{

    protected Context context;
    private TextView tv_bitian;
    private TextView tv_name;
    private EditText et_value;
    private TextView tv_tishi;
    private View itemView;


    public RecyclerInputDisableHolder(Context context,@NonNull View itemView) {
        super(itemView);
        this.context = context;
        tv_bitian = itemView.findViewById(R.id.tv_bitian);
        tv_name = itemView.findViewById(R.id.tv_name);
        et_value = itemView.findViewById(R.id.et_value);
        tv_tishi = itemView.findViewById(R.id.tv_tishi);
        this.itemView = itemView;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void setData(FormFiledBean formFiledBean, Map<String, List<DictionaryBean>> dicMap, JsonObject jsonObject, boolean isCheck, RecyclerView.Adapter adapter) {

        if (null != formFiledBean.getIsShowForm()) {
            //经纬经度隐藏
            if (formFiledBean.getIsShowForm() == 0 || formFiledBean.getDbFieldName().equals("location_lon")|| formFiledBean.getDbFieldName().equals("location_lat")) {//1展示 0隐藏
                setVisibility(false, itemView);
            } else {
                setVisibility(true, itemView);
            }
        }
        //提示
        isShowTips(context,formFiledBean);
        //是否必填
        isShowMustInput(formFiledBean,tv_bitian);
        et_value.setEnabled(false);
        itemView.setBackgroundColor(context.getResources().getColor(R.color.form_module_color_e6e6e6));
        et_value.setFocusable(false);
        et_value.setFocusableInTouchMode(false);
        tv_name.setText(formFiledBean.getDbFieldTxt());
        et_value.setText("");//默认为空
        et_value.setHint(String.format("请输入%s",formFiledBean.getDbFieldTxt()));
        if (null != jsonObject.get(formFiledBean.getDbFieldName())) {
            String value = jsonObject.get(formFiledBean.getDbFieldName()).getAsString();
            if (!TextUtils.isEmpty(value)) {
                et_value.setText(value);
            }
        }
    }
}
