package com.hollysmart.formmodule.holder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.hollysmart.formmodule.R;
import com.hollysmart.formmodule.adapter.ItemPicAdater;
import com.hollysmart.formmodule.bean.DictionaryBean;
import com.hollysmart.formmodule.bean.FormFiledBean;
import com.hollysmart.formmodule.bean.PicBean;
import com.hollysmart.formmodule.view.linearlayoutforlistview.MyLinearLayoutForListView;

import java.util.List;
import java.util.Map;

/**
 * 表单输图片选择/展示控件
 */
public class RecyclerPicHolder extends FormItemHolder {

    protected Context context = null;
    private MyLinearLayoutForListView ll_jingdian_pic;
    private TextView tv_name;
    private TextView tv_tishi;
    private TextView tv_bitian;
    private TextView tv_value;
    private TextView tv_hint_tishi;
    private View itemView;


    public RecyclerPicHolder(Context context, @NonNull View itemView) {
        super(itemView);
        this.context = context;
        this.itemView = itemView;
        ll_jingdian_pic = itemView.findViewById(R.id.ll_jingdian_pic);
        tv_name = itemView.findViewById(R.id.tv_name);
        tv_name = itemView.findViewById(R.id.tv_name);
        tv_bitian = itemView.findViewById(R.id.tv_bitian);
        tv_value = itemView.findViewById(R.id.tv_value);
        tv_tishi = itemView.findViewById(R.id.tv_tishi);
        tv_hint_tishi = itemView.findViewById(R.id.tv_hint_tishi);
    }

    @Override
    public void setData(FormFiledBean formFiledBean, Map<String, List<DictionaryBean>> dicMap, JsonObject jsonObject, boolean isCheck, RecyclerView.Adapter adapter) {
        //展示隐藏
        isShowForm(formFiledBean);
        //是否必填
        isShowMustInput(formFiledBean, tv_bitian);
        //提示
        isShowTips(context, formFiledBean);
        List<PicBean> pics = formFiledBean.getPic();
        ItemPicAdater itemPicAdater = new ItemPicAdater(context, jsonObject, formFiledBean.getDbFieldName(), pics, isCheck);
        if (!TextUtils.isEmpty(formFiledBean.getDbFieldTxt())) {
            if (formFiledBean.getDbFieldTxt().equals("照片")) {
                itemPicAdater.setMaxSize(3);
            } else {
                itemPicAdater.setMaxSize(9);
            }
        }
        ll_jingdian_pic.removeAllViews();
        ll_jingdian_pic.setAdapter(itemPicAdater);
        tv_name.setText(formFiledBean.getDbFieldTxt());
        if (!TextUtils.isEmpty(formFiledBean.getDbFieldTxt())) {
            if (formFiledBean.getDbFieldTxt().equals("照片")) {
                if (isCheck) {
                    tv_hint_tishi.setVisibility(View.GONE);
                } else {
                    tv_hint_tishi.setVisibility(View.VISIBLE);
                }
            } else {
                tv_hint_tishi.setVisibility(View.GONE);
            }
        }
    }
}
