package com.hollysmart.formmodule.holder;

import static android.content.Context.VIBRATOR_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.hollysmart.formmodule.R;
import com.hollysmart.formmodule.bean.DictionaryBean;
import com.hollysmart.formmodule.bean.FormFiledBean;

import java.util.List;
import java.util.Map;

public class RecyclerSwtichHolder extends FormItemHolder {

    protected Context context = null;
    private View itemView;
    private TextView tv_bitian;
    private TextView tv_name;
    private TextView tv_tishi;
    private ImageView iv_switch;

    public RecyclerSwtichHolder(Context context, @NonNull View itemView) {
        super(itemView);
        this.context = context;
        this.itemView = itemView;
        tv_bitian = itemView.findViewById(R.id.tv_bitian);
        tv_name = itemView.findViewById(R.id.tv_name);
        tv_tishi = itemView.findViewById(R.id.tv_tishi);
        iv_switch = itemView.findViewById(R.id.iv_switch);

    }

    @Override
    public void setData(FormFiledBean formFiledBean, Map<String, List<DictionaryBean>> dicMap, JsonObject jsonObject, boolean isCheck, RecyclerView.Adapter adapter) {

        initFormData(context,formFiledBean,tv_bitian,iv_switch,itemView);//初始化表单控件
        tv_name.setText(formFiledBean.getDbFieldTxt());
        iv_switch.setImageResource(R.drawable.form_module_check_off);//初始化
        if (null != jsonObject.get(formFiledBean.getDbFieldName())) {
            String value = jsonObject.get(formFiledBean.getDbFieldName()).getAsString();
            if (!TextUtils.isEmpty(value)) {
                if(value.equals("1")){
                    iv_switch.setImageResource(R.drawable.form_module_check_on);
                }
            }
        }

        iv_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate(context);
                String value = jsonObject.get(formFiledBean.getDbFieldName()).getAsString();
                if (!TextUtils.isEmpty(value)) {
                    if(value.equals("1")){//开变为关
                        jsonObject.addProperty(formFiledBean.getDbFieldName(), "0");
                        adapter.notifyItemChanged(formFiledBean.getPosition());
                    }else{//关变为开
                        jsonObject.addProperty(formFiledBean.getDbFieldName(), "1");
                        adapter.notifyItemChanged(formFiledBean.getPosition());
                    }
                }else{
                    jsonObject.addProperty(formFiledBean.getDbFieldName(), "1");
                    adapter.notifyItemChanged(formFiledBean.getPosition());
                }
            }
        });

    }


    @SuppressLint("MissingPermission")
    private void vibrate(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }


}
