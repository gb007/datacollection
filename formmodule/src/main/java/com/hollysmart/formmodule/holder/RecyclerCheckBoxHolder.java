package com.hollysmart.formmodule.holder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.hollysmart.formmodule.R;
import com.hollysmart.formmodule.bean.DictionaryBean;
import com.hollysmart.formmodule.bean.FormFiledBean;
import com.hollysmart.formmodule.dialog.BsSelectDialog;

import java.util.List;
import java.util.Map;

/**
 * 表单输复选框控件
 */
public class RecyclerCheckBoxHolder extends FormItemHolder {

    protected Context context;
    private View itemView;
    private TextView tv_bitian;
    private TextView tv_name;
    private TextView tv_value;
    private TextView tv_tishi;
    private LinearLayout ll_value;
    private ImageView iv_arrorw;

    public RecyclerCheckBoxHolder(Context context, @NonNull View itemView) {
        super(itemView);
        this.context = context;
        this.itemView = itemView;
        tv_bitian = itemView.findViewById(R.id.tv_bitian);
        tv_name = itemView.findViewById(R.id.tv_name);
        tv_tishi = itemView.findViewById(R.id.tv_tishi);
        tv_value = itemView.findViewById(R.id.tv_value);
        ll_value = itemView.findViewById(R.id.ll_value);
        iv_arrorw = itemView.findViewById(R.id.iv_arrorw);
    }


    @Override
    public void setData(FormFiledBean formFiledBean, Map<String, List<DictionaryBean>> dicMap, JsonObject jsonObject, boolean isCheck, RecyclerView.Adapter adapter) {

        List<DictionaryBean> dictionaryBeans = dicMap.get(formFiledBean.getDbFieldName());//字典
        initFormData(context,formFiledBean,tv_bitian,ll_value,itemView);//初始化表单控件
        tv_name.setText(formFiledBean.getDbFieldTxt());
        tv_value.setText("");//默认为空
        tv_value.setHint(String.format("请选择%s", formFiledBean.getDbFieldTxt()));
        if (null != jsonObject.get(formFiledBean.getDbFieldName())) {
            String value = jsonObject.get(formFiledBean.getDbFieldName()).getAsString();
            if (!TextUtils.isEmpty(value)) {
                if (null != dictionaryBeans && dictionaryBeans.size() > 0) {
                    tv_value.setText(getDicValue(value, dictionaryBeans));
                }
            }
        }

        BsSelectDialog bsSelectDialog = new BsSelectDialog(new BsSelectDialog.MulitiCheckBoxIF() {
            @Override
            public void checkBoxResult(String checkResult) {
                if(!TextUtils.isEmpty(checkResult)){
                tv_value.setText(checkResult.substring(0, checkResult.length() - 1));
                jsonObject.addProperty(formFiledBean.getDbFieldName(), setDicValue(checkResult, dictionaryBeans));
                }
            }
        });

        List<DictionaryBean> finalDictionaryBeans1 = dictionaryBeans;
        ll_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalDictionaryBeans1 != null && finalDictionaryBeans1.size() > 0) {
                    bsSelectDialog.showMUltiCheckBoxDialog(context, 0, formFiledBean.getDbFieldTxt(), finalDictionaryBeans1);
                }
            }
        });
    }
}


