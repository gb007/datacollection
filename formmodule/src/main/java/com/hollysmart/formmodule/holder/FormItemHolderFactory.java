package com.hollysmart.formmodule.holder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hollysmart.formmodule.R;
import com.hollysmart.formmodule.common.Constants;

public class FormItemHolderFactory {

    public static FormItemHolder getFormItemHolder(ViewGroup parent, int viewType) {

        if (Constants.FORM_FILED_TYPE_TEXT == viewType) {//文本框

            View itemTextView = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_module_item_biaoge_danhang, parent, false);
            FormItemHolder textHolder = new RecyclerTextHolder(parent.getContext(), itemTextView);
            return textHolder;

        } else if (Constants.FORM_FILED_TYPE_PASSWORD == viewType) {//密码输入框

            View itemPasswordView = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_module_item_biaoge_password, parent, false);
            FormItemHolder passwordHolder = new RecyclerPassordHolder(parent.getContext(), itemPasswordView);
            return passwordHolder;


        } else if (Constants.FORM_FILED_TYPE_LIST == viewType) {//下拉单选

            View itemDownListView = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_module_item_biaoge_danhang_list, parent, false);
            FormItemHolder downListHolder = new RecyclerDownListHolder(parent.getContext(), itemDownListView);
            return downListHolder;

        } else if (Constants.FORM_FILED_TYPE_RADIO == viewType) {//下拉单选


            View itemRadioView = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_module_item_biaoge_danhang_list, parent, false);
            FormItemHolder radioHolder = new RecyclerDownListHolder(parent.getContext(), itemRadioView);
            return radioHolder;


        } else if (Constants.FORM_FILED_TYPE_CHECK_BOX  == viewType) {//多选


            View itemCheckBoxView = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_module_item_biaoge_danhang_list, parent, false);
            FormItemHolder checkBoxHolder = new RecyclerCheckBoxHolder(parent.getContext(), itemCheckBoxView);
            return checkBoxHolder;

        } else if (Constants.FORM_FILED_TYPE_SWITACH == viewType) {//开关

            View itemSwitchView = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_module_item_biaoge_switch_content, parent, false);
            FormItemHolder switchHolder = new RecyclerSwtichHolder(parent.getContext(), itemSwitchView);
            return switchHolder;


        } else if (Constants.FORM_FILED_TYPE_DATE == viewType) {//日期选择


            View itemDateView = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_module_item_biaoge_danhang_xuanze, parent, false);
            FormItemHolder dateHolder = new RecyclerDateTimeHolder(parent.getContext(), itemDateView);
            return dateHolder;

        } else if (Constants.FORM_FILED_TYPE_DATE_TIME == viewType) {//日期时间选择


            View itemDateTimeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_module_item_biaoge_danhang_xuanze, parent, false);
            FormItemHolder datetimeHolder = new RecyclerDateTimeHolder(parent.getContext(), itemDateTimeView);
            return datetimeHolder;

        } else if (Constants.FORM_FILED_TYPE_TIME == viewType) {///时间选择


            View itemTimeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_module_item_biaoge_danhang_xuanze, parent, false);
            FormItemHolder timeHolder = new RecyclerDateTimeHolder(parent.getContext(), itemTimeView);
            return timeHolder;

        } else if (Constants.FORM_FILED_TYPE_IMAGE == viewType) {//图片选择

            View itemPicView = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_module_item_biaoge_image_content, parent, false);
            FormItemHolder picHolder = new RecyclerPicHolder(parent.getContext(), itemPicView);
            return picHolder;


        }else if (Constants.FORM_FILED_TYPE_TEXTAREA == viewType) {//文本域

            View itemTextAreaView = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_module_item_biaoge_text_area, parent, false);
            FormItemHolder textAreaHolder = new RecyclerTextAreaHolder(parent.getContext(), itemTextAreaView);
            return textAreaHolder;

        }else if (Constants.FORM_FILED_TYPE_LIST_MULTI == viewType) {


            View itemTextView = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_module_item_biaoge_danhang, parent, false);
            FormItemHolder textHolder = new RecyclerTextHolder(parent.getContext(), itemTextView);
            return textHolder;

        }else if (Constants.FORM_FILED_TYPE_DOT_MAP == viewType) {//地图

            View itemMpView = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_module_item_biaoge_danhang_xuanze, parent, false);
            FormItemHolder mapHolder = new RecyclerMapHolder(parent.getContext(), itemMpView);
            return mapHolder;


        }else if (Constants.FORM_FILED_TYPE_INPUT_DISABLED == viewType) {//不可编辑输入框

            View itemInputDisableView = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_module_item_biaoge_danhang, parent, false);
            FormItemHolder inputDisableHolder = new RecyclerInputDisableHolder(parent.getContext(), itemInputDisableView);
            return inputDisableHolder;

        }else if (Constants.FORM_FILED_TYPE_LINK_DOWN == viewType) {//多级联动

            View itemLinkDownView = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_module_item_biaoge_danhang_list, parent, false);
            FormItemHolder linkDownHolder = new RecyclerLinkDownHolder(parent.getContext(), itemLinkDownView);
            return linkDownHolder;

        }

        return null;
    }


}
