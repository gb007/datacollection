package com.hollysmart.formmodule.adapter;

import android.text.TextUtils;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.hollysmart.formmodule.Utils.LogUtils;
import com.hollysmart.formmodule.bean.DictionaryBean;
import com.hollysmart.formmodule.bean.FormFiledBean;
import com.hollysmart.formmodule.common.Constants;
import com.hollysmart.formmodule.holder.FormItemHolder;
import com.hollysmart.formmodule.holder.FormItemHolderFactory;
import com.hollysmart.formmodule.holder.RecyclerLinkDownHolder;

import java.util.List;
import java.util.Map;

public class FormAdapter extends RecyclerView.Adapter<FormItemHolder> {

    private List<FormFiledBean> filedBeanList;
    private Map<String, List<DictionaryBean>> dicMap;
    private JsonObject dataJson;
    private boolean isCheck;

    public FormAdapter(List<FormFiledBean> filedBeanList, Map<String, List<DictionaryBean>> dicMap, JsonObject dataJson, boolean isCheck) {
        this.filedBeanList = filedBeanList;
        this.dicMap = dicMap;
        this.dataJson = dataJson;
        this.isCheck = isCheck;
    }


    @NonNull
    @Override
    public FormItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return FormItemHolderFactory.getFormItemHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull FormItemHolder holder, int position) {
        holder.setData(filedBeanList.get(position), dicMap, dataJson, isCheck, this);
        if(holder instanceof RecyclerLinkDownHolder){// 设置表单组件list，用来重设子组件字典项
            RecyclerLinkDownHolder linkDownHolder = (RecyclerLinkDownHolder) holder;
            linkDownHolder.setFormBeanList(filedBeanList);
        }
        LogUtils.d("onBindViewHolder:::", position + "");
    }

    @Override
    public int getItemCount() {
        return filedBeanList.size();
    }

    @Override
    public int getItemViewType(int position) {
        int itemType = 1;
        FormFiledBean formFiledBean = filedBeanList.get(position);
        formFiledBean.setPosition(position);
        String showType = formFiledBean.getFieldShowType();
        if (!TextUtils.isEmpty(showType)) {

            if (showType.equals("text")) {//文本框和多级联动子项

                if (formFiledBean.isLinkDownChild()) {//多级联动子项
                    itemType = Constants.FORM_FILED_TYPE_LINK_DOWN;
                } else {//文本框
                    itemType = Constants.FORM_FILED_TYPE_TEXT;
                }

            } else if (showType.equals("textarea")) {//多行文本

                itemType = Constants.FORM_FILED_TYPE_TEXTAREA;

            } else if (showType.equals("password")) {//密码

                itemType = Constants.FORM_FILED_TYPE_PASSWORD;

            } else if (showType.equals("list")) {//下拉框

                itemType = Constants.FORM_FILED_TYPE_LIST;

            } else if (showType.equals("radio")) {//单选框

//              itemType = Constants.FORM_FILED_TYPE_RADIO;
                itemType = Constants.FORM_FILED_TYPE_LIST;

            } else if (showType.equals("checkbox")) {//多选框

                itemType = Constants.FORM_FILED_TYPE_CHECK_BOX;

            } else if (showType.equals("switch")) {//开关

                itemType = Constants.FORM_FILED_TYPE_SWITACH;

            } else if (showType.equals("date")) {//日期

                itemType = Constants.FORM_FILED_TYPE_DATE;

            } else if (showType.equals("datetime")) {//日期时间

                itemType = Constants.FORM_FILED_TYPE_DATE_TIME;

            } else if (showType.equals("time")) {//时间

                itemType = Constants.FORM_FILED_TYPE_TIME;

            } else if (showType.equals("image")) {//图片

                itemType = Constants.FORM_FILED_TYPE_IMAGE;

            } else if (showType.equals("list_multi")) {//下拉多选

//              itemType = Constants.FORM_FILED_TYPE_LIST_MULTI;
                itemType = Constants.FORM_FILED_TYPE_CHECK_BOX;

            } else if (showType.equals("dot_map")) {//地图

                itemType = Constants.FORM_FILED_TYPE_DOT_MAP;

            } else if (showType.equals("input_disabled")) {//文本框禁用

                itemType = Constants.FORM_FILED_TYPE_INPUT_DISABLED;

            } else if (showType.equals("link_down")) {//多级联动

                itemType = Constants.FORM_FILED_TYPE_LINK_DOWN;

            }

        }
        return itemType;
    }
}
