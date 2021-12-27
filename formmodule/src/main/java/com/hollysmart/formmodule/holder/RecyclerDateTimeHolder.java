package com.hollysmart.formmodule.holder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.hollysmart.formmodule.R;
import com.hollysmart.formmodule.bean.DictionaryBean;
import com.hollysmart.formmodule.bean.FormFiledBean;
import com.hollysmart.formmodule.common.Constants;
import com.hollysmart.formmodule.dialog.TimePickerDialog;

import java.util.List;
import java.util.Map;

/**
 * 表单输时间控件
 */
public class RecyclerDateTimeHolder extends FormItemHolder {

    protected Context context = null;

    private TextView tv_bitian;
    private TextView tv_name;
    private EditText et_value;
    private TextView tv_tishi;
    private ImageView iv_arrorw;
    private View itemView;


    public RecyclerDateTimeHolder(Context context, @NonNull View itemView) {
        super(itemView);
        this.context = context;
        this.itemView = itemView;
        tv_bitian = itemView.findViewById(R.id.tv_bitian);
        tv_name = itemView.findViewById(R.id.tv_name);
        et_value = itemView.findViewById(R.id.tv_value);
        tv_tishi = itemView.findViewById(R.id.tv_tishi);
        iv_arrorw = itemView.findViewById(R.id.iv_arrorw);
    }


    @Override
    public void setData(FormFiledBean formFiledBean, Map<String, List<DictionaryBean>> dicMap, JsonObject jsonObject, boolean isCheck, RecyclerView.Adapter adapter) {

        int dataType = 7;//时间格式  7日期 9时间 8日期和时间 默认7
        if (formFiledBean.getFieldShowType().equals("date")) {
            dataType = Constants.FORM_FILED_TYPE_DATE;
        } else if (formFiledBean.getFieldShowType().equals("time")) {
            dataType = Constants.FORM_FILED_TYPE_TIME;
        } else if (formFiledBean.getFieldShowType().equals("datetime")) {
            dataType = Constants.FORM_FILED_TYPE_DATE_TIME;
        }

        initFormData(context, formFiledBean, tv_bitian, et_value, itemView);//初始化表单控件
        tv_name.setText(formFiledBean.getDbFieldTxt());
        et_value.setText("");//默认为空
        et_value.setHint(String.format("请选择%s", formFiledBean.getDbFieldTxt()));
        if (null != jsonObject.get(formFiledBean.getDbFieldName())) {
            String value = jsonObject.get(formFiledBean.getDbFieldName()).getAsString();
            if (!TextUtils.isEmpty(value)) {
                if (dataType == Constants.FORM_FILED_TYPE_DATE) {
                    et_value.setText(value.substring(0, 10));
                }
            }
        }

        int finalDataType1 = dataType;
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.TimePickerDialogInterface() {
            @Override
            public void positiveListener(String mYear, String mMonth, String mDay, String mHour, String mMinute) {
                String date = "";
                if (finalDataType1 == Constants.FORM_FILED_TYPE_DATE) {
                    date = mYear + "-" + mMonth
                            + "-" + mDay;
                } else if (finalDataType1 == Constants.FORM_FILED_TYPE_TIME) {
                    date = mHour + ":" + mMinute;
                } else if (finalDataType1 == Constants.FORM_FILED_TYPE_DATE_TIME) {
                    date = mYear + "-" + mMonth
                            + "-" + mDay + " "
                            + mHour + ":" + mMinute;
                }
                et_value.setText(date);
                jsonObject.addProperty(formFiledBean.getDbFieldName(), date);
            }

            @Override
            public void negativeListener() {
            }
        });


        int finalDataType = dataType;
        et_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalDataType == Constants.FORM_FILED_TYPE_DATE) {
                    String value = jsonObject.get(formFiledBean.getDbFieldName()).getAsString();
                    if (TextUtils.isEmpty(value)) {
                        timePickerDialog.showDatePickerDialog();
                    } else {
                        showDateWithData(timePickerDialog, value.substring(0, 10));
                    }
                } else if (finalDataType == Constants.FORM_FILED_TYPE_TIME) {
                    timePickerDialog.showTimePickerDialog();
                } else if (finalDataType == Constants.FORM_FILED_TYPE_DATE_TIME) {
                    timePickerDialog.showDateAndTimePickerDialog();
                }
            }
        });
    }

    /**
     * 展示日期
     *
     * @param timePickerDialog
     * @param date
     */
    private void showDateWithData(TimePickerDialog timePickerDialog, String date) {
        int year = 0, month = 0, day = 0;
        String[] dateArray = date.split("-");
        if (!TextUtils.isEmpty(dateArray[0])) {
            year = Integer.parseInt(dateArray[0]);
        }
        if (!TextUtils.isEmpty(dateArray[1])) {
            month = Integer.parseInt(dateArray[1]);
        }

        if (!TextUtils.isEmpty(dateArray[2])) {
            day = Integer.parseInt(dateArray[2]);
        }
        if (year != 0 && month != 0 && day != 0) {
            timePickerDialog.showDatePickerDialog(year, month - 1, day);
        }
    }


}
