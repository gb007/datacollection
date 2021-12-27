package com.hollysmart.formmodule.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;

import com.hollysmart.formmodule.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cai on 2017/8/3. 日期时间选择器
 */

public class TimePickerDialog {

    private Context mContext;
    private AlertDialog.Builder mAlertDialog;
    private int mHour, mMinute;
    private TimePickerDialogInterface timePickerDialogInterface;
    private TimePicker mTimePicker;
    private DatePicker mDatePicker;
    private int mTag = 0;
    private int mYear, mDay, mMonth;

    public TimePickerDialog(Context context) {
        super();
        mContext = context;
        timePickerDialogInterface = (TimePickerDialogInterface) context;
    }
    public TimePickerDialog(Context context, TimePickerDialogInterface timePickerDialogInterface) {
        super();
        mContext = context;
        this.timePickerDialogInterface = timePickerDialogInterface;
    }

    /**
     * 初始化DatePicker
     *
     * @return
     */
    private View initDatePicker() {
        View inflate = LayoutInflater.from(mContext).inflate( R.layout.form_module_datepicker_layout, null);
        mDatePicker = (DatePicker) inflate.findViewById(R.id.datePicker);
        resizePikcer(mDatePicker);
        return inflate;
    }

    /**
     * 初始化TimePicker
     *
     * @return
     */
    private View initTimePicker() {
        View inflate = LayoutInflater.from(mContext).inflate(
                R.layout.form_module_timepicker_layout, null);
        mTimePicker = (TimePicker) inflate.findViewById(R.id.timePicker);
        mTimePicker.setIs24HourView(true);
        resizePikcer(mTimePicker);
        return inflate;
    }

    private View initDateAndTimePicker() {
        View inflate = LayoutInflater.from(mContext).inflate(
                R.layout.form_module_dateandtimepicker_layout, null);
        mTimePicker = (TimePicker) inflate
                .findViewById(R.id.dateAndTimePicker_timePicker);
        mDatePicker = (DatePicker) inflate
                .findViewById(R.id.dateAndTimePicker_datePicker);
        mTimePicker.setIs24HourView(true);
        resizePikcer(mTimePicker);
        resizePikcer(mDatePicker);
        return inflate;
    }

    /**
     * 创建dialog
     *
     * @param view
     */
    private void initDialog(View view) {
        mAlertDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        if (mTag == 0) {
                            getTimePickerValue();
                        } else if (mTag == 1) {
                            getDatePickerValue();
                        } else if (mTag == 2) {
                            getDatePickerValue();
                            getTimePickerValue();
                        }

                        if (textView != null){
                            textView.setText(getYear() + "-" + getMonth() + "-" + getDay());
                        }

                        int iYear = getYear();
                        int iMonth = getMonth();
                        int iDay = getDay();
                        int iHour = getHour();
                        int iMinute = getMinute();

                        String strYear = "";
                        String strMonth = "";
                        String strDay = "";
                        String strHour = "";
                        String strMinute = "";
                        strYear = iYear + "";
                        strMonth = iMonth + "";
                        strDay = iDay + "";
                        strHour = iHour + "";
                        strMinute = iMinute + "";

                        if(iMonth < 10){
                            strMonth = "0" + iMonth;
                        }

                        if(iDay < 10){
                            strDay = "0" + iDay;
                        }
                        if(iHour < 10){
                            strHour = "0" + iHour;
                        }
                        if(iMinute < 10){
                            strMinute = "0" + iMinute;
                        }
                        timePickerDialogInterface.positiveListener(strYear,strMonth,strDay,strHour,strMinute);
//                      timePickerDialogInterface.positiveListener(getYear(),getMonth(),getDay(),getHour(),getMinute());

                    }
                });
        mAlertDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        timePickerDialogInterface.negativeListener();
                        dialog.dismiss();
                    }
                });
        mAlertDialog.setView(view);
    }

    /**
     * 显示时间选择器
     */
    public void showTimePickerDialog() {
        mTag=0;
        View view = initTimePicker();
        mAlertDialog = new AlertDialog.Builder(mContext);
        mAlertDialog.setTitle("选择时间");
        initDialog(view);
        mAlertDialog.show();

    }

    /**
     * 显示日期选择器
     */
    public void showDatePickerDialog() {
        mTag=1;
        View view = initDatePicker();
        mAlertDialog = new AlertDialog.Builder(mContext);
        mAlertDialog.setTitle("选择时间");
        initDialog(view);
        mAlertDialog.show();
    }

    /**
     * 显示日期选择器  带初始值
     * @param year
     * @param month
     * @param day
     */
    public void showDatePickerDialog(int year, int month, int day) {
        mTag=1;
        View view = initDatePicker();
        mAlertDialog = new AlertDialog.Builder(mContext);
        mAlertDialog.setTitle("选择时间");
        initDialog(view);
        mDatePicker.init(year, month, day, null);
        mAlertDialog.show();
    }

    /**
     * 显示日期选择器  带初始值
     * @param date   2018-01-01
     */

    public void showDatePickerDialog(String date) {
        mTag=1;
        View view = initDatePicker();
        mAlertDialog = new AlertDialog.Builder(mContext);
        mAlertDialog.setTitle("选择时间");
        initDialog(view);
        int year;
        int month;
        int day;
        try {
            String[] dateS = date.split("-");
            year = Integer.parseInt(dateS[0]);
            month = Integer.parseInt(dateS[1]) - 1;
            day = Integer.parseInt(dateS[2]);
            mDatePicker.init(year, month, day, null);
            mAlertDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 显示日期选择器  带初始值
     * @param year
     * @param month
     * @param day
     * @param textView
     */
    private TextView textView;
    public void showDatePickerDialog(int year, int month, int day, TextView textView) {
        mTag=1;
        this.textView = textView;
        View view = initDatePicker();
        mAlertDialog = new AlertDialog.Builder(mContext);
        mAlertDialog.setTitle("选择时间");
        initDialog(view);
        mDatePicker.init(year, month, day, null);
        mAlertDialog.show();
    }


    /**
     * 显示日期选择器
     */
    public void showDateAndTimePickerDialog() {
        mTag=2;
        View view = initDateAndTimePicker();
        mAlertDialog = new AlertDialog.Builder(mContext);
        mAlertDialog.setTitle("选择时间");
        initDialog(view);
        mAlertDialog.show();
    }

    /*
    * 调整numberpicker大小
    */
    private void resizeNumberPicker(NumberPicker np) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(120,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 0, 10, 0);
        np.setLayoutParams(params);
    }

    /**
     * 调整FrameLayout大小
     *
     * @param tp
     */
    private void resizePikcer(FrameLayout tp) {
        List<NumberPicker> npList = findNumberPicker(tp);
        for (NumberPicker np : npList) {
            resizeNumberPicker(np);
        }
    }

    /**
     * 得到viewGroup里面的numberpicker组件
     *
     * @param viewGroup
     * @return
     */
    private List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
        List<NumberPicker> npList = new ArrayList<NumberPicker>();
        View child = null;
        if (null != viewGroup) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                child = viewGroup.getChildAt(i);
                if (child instanceof NumberPicker) {
                    npList.add((NumberPicker) child);
                } else if (child instanceof LinearLayout) {
                    List<NumberPicker> result = findNumberPicker((ViewGroup) child);
                    if (result.size() > 0) {
                        return result;
                    }
                }
            }
        }
        return npList;
    }

    public int getYear() {
        return mYear;
    }

    public int getDay() {
        return mDay;
    }

    public int getMonth() {
        //返回的时间是0-11
        return mMonth+1;
    }

    public int getMinute() {
        return mMinute;
    }

    public int getHour() {
        return mHour;
    }

    /**
     * 获取日期选择的值
     */
    private void getDatePickerValue() {
        mYear = mDatePicker.getYear();
        mMonth = mDatePicker.getMonth();
        mDay= mDatePicker.getDayOfMonth();
    }

    /**
     * 获取时间选择的值
     */
    private void getTimePickerValue() {
        // api23这两个方法过时
        mHour = mTimePicker.getCurrentHour();// timePicker.getHour();
        mMinute = mTimePicker.getCurrentMinute();// timePicker.getMinute();
    }



    public interface TimePickerDialogInterface {
        public void positiveListener(String mYear, String mMonth, String mDay,String mHour, String mMinute);
        public void negativeListener();
    }

}
