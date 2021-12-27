package com.hollysmart.formmodule.holder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 表单输下拉弹窗单选控件
 */
public class RecyclerDownListHolder extends FormItemHolder {

    protected Context context = null;
    private View itemView;
    private TextView tv_bitian;
    private TextView tv_name;
    private TextView tv_value;
    private TextView tv_tishi;
    private LinearLayout ll_value;
    private ImageView iv_arrorw;
    private DictionaryBean cancelbean;

    public RecyclerDownListHolder(Context context, @NonNull View itemView) {
        super(itemView);
        this.context = context;
        tv_bitian = itemView.findViewById(R.id.tv_bitian);
        tv_name = itemView.findViewById(R.id.tv_name);
        tv_tishi = itemView.findViewById(R.id.tv_tishi);
        tv_value = itemView.findViewById(R.id.tv_value);
        ll_value = itemView.findViewById(R.id.ll_value);
        iv_arrorw = itemView.findViewById(R.id.iv_arrorw);
        this.itemView = itemView;

        cancelbean = new DictionaryBean();
        cancelbean.setValue("");
        cancelbean.setText("取消");
        cancelbean.setTitle("取消");

    }


    @Override
    public void setData(FormFiledBean formFiledBean, Map<String, List<DictionaryBean>> dicMap, JsonObject jsonObject, boolean isCheck, RecyclerView.Adapter adapter) {

        List<DictionaryBean> dictionaryBeans = dicMap.get(formFiledBean.getDbFieldName());//字典
        if(dictionaryBeans == null || dictionaryBeans.size() < 1) {
            dictionaryBeans = new ArrayList<>();
        }
        if (!dictionaryBeans.contains(cancelbean)) {//取消项
            dictionaryBeans.add(cancelbean);
        }
        initFormData(context,formFiledBean,tv_bitian,ll_value,itemView);//初始化表单控件
        tv_value.setHint(String.format("请选择%s",formFiledBean.getDbFieldTxt()));
        tv_name.setText(formFiledBean.getDbFieldTxt());
        tv_value.setText("");//默认为空
        if (null != jsonObject.get(formFiledBean.getDbFieldName())) {
            String value = jsonObject.get(formFiledBean.getDbFieldName()).getAsString();
            if (!TextUtils.isEmpty(value)) {
                List<DictionaryBean> dictionaryBeanList = dicMap.get(formFiledBean.getDbFieldName());
                if (null != dictionaryBeanList && dictionaryBeanList.size() > 0) {
                    tv_value.setText(getDicValue(value, dictionaryBeans));
                }
            }
        }

        AlertDialog.Builder editdialog = new AlertDialog.Builder(context);//选其他时探出输入框，用户自定义
        List<DictionaryBean> finalDictionaryBeans = dictionaryBeans;
        BsSelectDialog bsSelectDialog = new BsSelectDialog(new BsSelectDialog.BsSelectIF() {//选择框
            @Override
            public void onBsSelect(int type, int index) {
                //点击取消 清除原来选中的数据
                if (index == finalDictionaryBeans.size() - 1) {
                    tv_value.setText("");
                    jsonObject.addProperty(formFiledBean.getDbFieldName(),"");
                } else {
                    DictionaryBean dictionaryBean = finalDictionaryBeans.get(index);
                    tv_value.setText(dictionaryBean.getTitle());
                    jsonObject.addProperty(formFiledBean.getDbFieldName(),dictionaryBean.getValue());
                    final EditText inputServer = new EditText(context);
                    //名称选择其他的时候，弹出输入框用户自己输入
                    if (formFiledBean.getDbFieldName().equals("name")) {
                        if (dictionaryBean.getTitle().equals("其他")) {
                            editdialog.setTitle("请输入名称").setView(inputServer)
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int
                                                which) {
                                            dialog.dismiss();
                                        }
                                    });
                            editdialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String text = inputServer.getText().toString();
                                    tv_value.setText(text);
                                    jsonObject.addProperty(formFiledBean.getDbFieldName(),text);
                                }
                            });
                            editdialog.show();
                        }
                        //服务商名称选择其他的时候，弹出输入框用户自己输入
                    } else if (formFiledBean.getDbFieldName().equals("serviceproviders_name") && dictionaryBean.getTitle().equals("其他")) {

                        editdialog.setTitle("服务商名称")
                                .setView(inputServer)
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int
                                            which) {
                                        dialog.dismiss();
                                    }
                                });
                        editdialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String text = inputServer.getText().toString();
                                tv_value.setText(text);
                                jsonObject.addProperty(formFiledBean.getDbFieldName(),text);
                            }
                        });
                        editdialog.show();

                    }
                }

            }
        });

        List<DictionaryBean> finalDictionaryBeans1 = dictionaryBeans;
        ll_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalDictionaryBeans1 != null && finalDictionaryBeans1.size() > 0) {
                    bsSelectDialog.showPopuWindow_DictListData(context, 0, formFiledBean.getDbFieldTxt(), finalDictionaryBeans1);
                }
            }
        });
    }
}
