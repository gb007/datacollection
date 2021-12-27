package com.hollysmart.formmodule.holder;

import android.content.Context;
import android.text.Selection;
import android.text.Spannable;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.google.gson.JsonObject;
import com.hollysmart.formmodule.R;
import com.hollysmart.formmodule.Utils.AnimationUtil;
import com.hollysmart.formmodule.bean.DictionaryBean;
import com.hollysmart.formmodule.bean.FormFiledBean;

import java.util.List;
import java.util.Map;

public abstract class FormItemHolder extends ViewHolder {

    public FormItemHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void setData(FormFiledBean formFiledBean, Map<String, List<DictionaryBean>> dicMap, JsonObject jsonObject, boolean isCheck, RecyclerView.Adapter adapter);


    /**
     * 初始化表单中的展示控件，（设置控件的展示隐藏；是否必填，是否可编辑，当前是否展示提示动画，）
     *
     * @param context
     * @param formFiledBean 当前控件
     * @param mustInputView 必填图标
     * @param childView     item中可操作的View
     * @param itemView      item本身
     */
    public void initFormData(Context context, FormFiledBean formFiledBean, View mustInputView, View childView, View itemView) {
        isShowForm(formFiledBean);
        isShowMustInput(formFiledBean, mustInputView);
        isReadOnly(context, formFiledBean, childView, itemView);
        isShowTips(context, formFiledBean);
    }

    /**
     * 设置item当前控件在表单中显示或者隐藏
     *
     * @param formFiledBean
     */
    public void isShowForm(FormFiledBean formFiledBean) {
        if (null != formFiledBean.getIsShowForm()) {
            if (formFiledBean.getIsShowForm() == 0) {//1展示 0隐藏
                setVisibility(false, itemView);
            } else {
                setVisibility(true, itemView);
            }
        }
    }

    /**
     * 设置是否展示提示动画弹出提示Toast
     *
     * @param context
     * @param formFiledBean
     */
    public void isShowTips(Context context, FormFiledBean formFiledBean) {
        if (formFiledBean.isShowTips()) {//提示
            AnimationUtil.startTranslateShakeByViewAnim(context, itemView, AnimationUtil.FROMXDELTA, AnimationUtil.TOXDELTA, AnimationUtil.FROMYDELTA, AnimationUtil.TOYDELTA, AnimationUtil.DURATION, AnimationUtil.REPEATCOUNT);
            formFiledBean.setShowTips(false);
        }
    }

    /**
     * 设置是否展示必填标识
     *
     * @param formFiledBean
     * @param mustInputView
     */
    public void isShowMustInput(FormFiledBean formFiledBean, View mustInputView) {
        if (formFiledBean.getFieldMustInput().equals("1")) {//1必填 0非必填
            mustInputView.setVisibility(View.VISIBLE);
        } else {
            mustInputView.setVisibility(View.GONE);
        }
    }

    /**
     * 设置item设置可编辑（另设置不编辑状态颜色为灰色）
     *
     * @param context
     * @param formFiledBean
     * @param childView
     * @param itemView
     */
    public void isReadOnly(Context context, FormFiledBean formFiledBean, View childView, View itemView) {
        if (formFiledBean.getIsReadOnly() == 1) {//1只读 0可编辑
            childView.setEnabled(false);
            itemView.setBackgroundColor(context.getResources().getColor(R.color.form_module_color_e6e6e6));
        } else {
            childView.setEnabled(true);
            itemView.setBackgroundColor(context.getResources().getColor(R.color.form_module_white));
        }
    }


    /**
     * 设置item高度以及隐藏或者显示
     *
     * @param isVisible
     * @param itemView
     */
    public void setVisibility(boolean isVisible, View itemView) {
        RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
        if (isVisible) {
            param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            param.width = LinearLayout.LayoutParams.MATCH_PARENT;
            itemView.setVisibility(View.VISIBLE);
        } else {
            itemView.setVisibility(View.GONE);
            param.height = 0;
            param.width = 0;
        }
        itemView.setLayoutParams(param);
    }


    /**
     * 根据字典value查询字典项title
     *
     * @param dicKey             字典的value字段(若多个需用","分割)
     * @param dictionaryBeanList 字典list
     * @return 返回字典的title
     */
    public String getDicValue(String dicKey, List<DictionaryBean> dictionaryBeanList) {
        String value = "";
        String[] dics = dicKey.split(",");
        if (dics != null && dics.length > 0) {
            for (DictionaryBean dictionaryBean : dictionaryBeanList) {
                for (String dic : dics) {
                    if (dictionaryBean.getValue().equals(dic)) {
                        value += (dictionaryBean.getTitle() + ",");
                    }
                }
            }
            if (value.length() > 1) {
                value = value.substring(0, value.length() - 1);
            }
        }
        return value;
    }

    /**
     * 根据字典title查询字典的value
     *
     * @param dicValue  字典title(若多个需用","分割)
     * @param dictionaryBeanList
     * @return 返回字典的value（若多个则用","分割）
     */
    public String setDicValue(String dicValue, List<DictionaryBean> dictionaryBeanList) {
        String value = "";
        String[] values = dicValue.split(",");
        if (values != null && values.length > 0) {
            for (DictionaryBean dictionaryBean : dictionaryBeanList) {
                for (String dic : values) {
                    if (dictionaryBean.getTitle().equals(dic)) {
                        value += (dictionaryBean.getValue() + ",");
                    }
                }
            }
            if (value.length() > 1) {
                value = value.substring(0, value.length() - 1);
            }
        }
        return value;
    }

    /**
     * 设置EditText光标到输入框尾部
     * @param editText
     */
    public void setSelection(EditText editText){
        CharSequence text = editText.getText();
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable)text;
            Selection.setSelection(spanText, text.length());
        }
    }

}
