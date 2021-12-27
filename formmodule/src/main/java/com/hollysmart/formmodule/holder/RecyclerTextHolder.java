package com.hollysmart.formmodule.holder;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.hollysmart.formmodule.R;
import com.hollysmart.formmodule.Utils.EditTextUtil;
import com.hollysmart.formmodule.Utils.LogUtils;
import com.hollysmart.formmodule.Utils.RegularExpressionUtils;
import com.hollysmart.formmodule.bean.DictionaryBean;
import com.hollysmart.formmodule.bean.FormFiledBean;

import java.util.List;
import java.util.Map;

/**
 * 表单输入框控件
 */
public class RecyclerTextHolder extends FormItemHolder {

    protected Context context = null;
    private TextView tv_bitian;
    private TextView tv_name;
    private EditText et_value;
    private TextView tv_tishi;
    private View itemView;
    List<RegularExpressionUtils.Regular> regularList;

    public RecyclerTextHolder(Context context, @NonNull View itemView) {
        super(itemView);
        this.context = context;
        tv_bitian = itemView.findViewById(R.id.tv_bitian);
        tv_name = itemView.findViewById(R.id.tv_name);
        et_value = itemView.findViewById(R.id.et_value);
        tv_tishi = itemView.findViewById(R.id.tv_tishi);
        this.itemView = itemView;
        regularList = RegularExpressionUtils.getInstatce().getRegularList();//获取正则列表

    }

    @Override
    public void setData(FormFiledBean formFiledBean, Map<String, List<DictionaryBean>> dicMap, JsonObject jsonObject, boolean isCheck, RecyclerView.Adapter adapter) {

        if (et_value.getTag() instanceof TextWatcher) {//复用Holder则移除之前的text变化监听
            et_value.removeTextChangedListener((TextWatcher) et_value.getTag());
            et_value.clearFocus();
        }
        initFormData(context,formFiledBean,tv_bitian,et_value,itemView);//初始化表单控件
        tv_name.setText(formFiledBean.getDbFieldTxt());
        et_value.setText("");//默认为空
        et_value.setHint(String.format("请输入%s", formFiledBean.getDbFieldTxt()));
        if (null != jsonObject.get(formFiledBean.getDbFieldName())) {
            String value = jsonObject.get(formFiledBean.getDbFieldName()).getAsString();
            if (!TextUtils.isEmpty(value)) {
                et_value.setText(value);
            }
        }
//        setSelection(et_value);//设置光标在尾部

        String field_vaild_type = "";
        if (null != formFiledBean.getFieldValidType()) {
            field_vaild_type = formFiledBean.getFieldValidType().toString();
        }

        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                LogUtils.d("onChanged:::", formFiledBean.getDbFieldName());
                if (et_value.hasFocus()) {
                    if (s.length() > 0) {
                        LogUtils.d("onChangedAndsetValue:::", formFiledBean.getDbFieldName());
                        jsonObject.addProperty(formFiledBean.getDbFieldName(), s.toString());
                    } else {
                        jsonObject.addProperty(formFiledBean.getDbFieldName(), "");
                    }
                }

            }
        };

        et_value.addTextChangedListener(tw);
        et_value.setTag(tw);

        String finalField_vaild_type = field_vaild_type;
        et_value.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                LogUtils.d("hasFocus = " + hasFocus);

                //聚焦时设置监听，失去焦点时去除，防止holder复用时发生错乱
                //聚焦时重设输入框InputType类型防止holer复用时错乱
                if (hasFocus) {
                    et_value.addTextChangedListener(tw);
                    EditTextUtil.setInputType(et_value, finalField_vaild_type); //重设输入框类型
                } else {
                    et_value.clearFocus();
                    et_value.removeTextChangedListener(tw);
//                    if (!TextUtils.isEmpty(finalField_vaild_type)) {//失去焦点时数据正则验证
//                        boolean isMatch = EditTextUtil.matachText(finalField_vaild_type, et_value.getText().toString(), regularList, true);
//                        if (!isMatch) {
//                            formFiledBean.setShowTips(true);
//                            adapter.notifyItemChanged(formFiledBean.getPosition());
//                        }
//                    }
                }
            }
        });
    }

}
