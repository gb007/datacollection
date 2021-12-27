package com.hollysmart.formmodule.dialog;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.hollysmart.formmodule.bean.DictionaryBean;
import com.hollysmart.formmodule.interfaces.SelectIF;

import java.util.List;

/**
 * Created by cai on 2017/9/20
 */

public class BsSelectDialog {

    private BsSelectIF bsSelectIF;
    private PositiveIF positiveIF;
    private MulitiCheckBoxIF mulitiCheckBoxIF;
    public BsSelectDialog(BsSelectIF bsSelectIF) {
        this.bsSelectIF = bsSelectIF;
    }
    public BsSelectDialog(MulitiCheckBoxIF mulitiCheckBoxIF) {
        this.mulitiCheckBoxIF = mulitiCheckBoxIF;
    }

    public void showPopuWindow(Context mContext, final int type, String title, List<SelectIF> beanList) {
        String strs[] = new String[beanList.size()];
        for (int i = 0; i < beanList.size(); i++) {
            strs[i] = beanList.get(i).showInfo();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);

        builder.setItems(strs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bsSelectIF.onBsSelect(type, which);
            }
        }).show();
    }

    public interface BsSelectIF {
        void onBsSelect(int type, int index);
    }

    public interface MulitiCheckBoxIF {
        void checkBoxResult(String checkResult);
    }
    public interface PositiveIF {
        void queDing();
    }


    public void showPopuWindow_DictListData(Context mContext, final int type, String title, List<DictionaryBean> beanList) {
        String strs[] = new String[beanList.size()];
        for (int i = 0; i < beanList.size(); i++) {
            strs[i] = beanList.get(i).getTitle();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);

        builder.setItems(strs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bsSelectIF.onBsSelect(type, which);
            }
        }).show();
    }

    // 多选
    public void showMUltiCheckBoxDialog(Context mContext, final int type, String title, List<DictionaryBean> beanList) {
        String strs[] = new String[beanList.size()];
        boolean[] checkedItems = new boolean[beanList.size()];
        for (int i = 0; i < beanList.size(); i++) {
            strs[i] = beanList.get(i).getTitle();
            checkedItems[i] = false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);

        builder.setMultiChoiceItems(strs, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

            }
        });

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < checkedItems.length; i++) {
                    //判断一下是选中的
                    if (checkedItems[i]) {
                        //把选中的水果取出来     数据在哪里存着就去哪里取
                        String str_result = strs[i];
                        sb.append(str_result + ",");

                    }
                }
                mulitiCheckBoxIF.checkBoxResult(sb.toString());
                dialog.dismiss();
            }
        });
        //[3]展示对话框  和toast一样 一定要记得show出来
        builder.show();
    }


}























