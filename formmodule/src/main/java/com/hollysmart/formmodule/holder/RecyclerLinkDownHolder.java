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
import com.hollysmart.formmodule.activity.FormDetailActivity;
import com.hollysmart.formmodule.bean.DictTableBean;
import com.hollysmart.formmodule.bean.DictionaryBean;
import com.hollysmart.formmodule.bean.FormFiledBean;
import com.hollysmart.formmodule.bean.LinkDownDictBean;
import com.hollysmart.formmodule.dialog.BsSelectDialog;
import com.hollysmart.formmodule.retrofit.BasicResponse;
import com.hollysmart.formmodule.retrofit.DefaultObserver;
import com.hollysmart.formmodule.retrofit.RetrofitHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * 表单输下拉弹窗单选控件
 */
public class RecyclerLinkDownHolder extends FormItemHolder {

    protected Context context = null;
    private View itemView;
    private TextView tv_bitian;
    private TextView tv_name;
    private TextView tv_value;
    private TextView tv_tishi;
    private LinearLayout ll_value;
    private ImageView iv_arrorw;
    //"取消"字典项
    private DictionaryBean cancelbean;
    //
    private List<FormFiledBean> formFiledBeanList;


    public RecyclerLinkDownHolder(Context context, @NonNull View itemView) {
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

        List<DictionaryBean> dictionaryBeans = new ArrayList<>();//字典
        initFormData(context,formFiledBean,tv_bitian,ll_value,itemView);//初始化表单控件
        tv_value.setHint(String.format("请选择%s", formFiledBean.getDbFieldTxt()));
        tv_name.setText(formFiledBean.getDbFieldTxt());
        //初始化
        tv_value.setText("");
        if (null != jsonObject.get(formFiledBean.getDbFieldName())) {//设置值
            String value = jsonObject.get(formFiledBean.getDbFieldName()).getAsString();
            if (!TextUtils.isEmpty(value)) {
                List<DictionaryBean> dictionaryBeanList = dicMap.get(formFiledBean.getDictKey());
                if (null != dictionaryBeanList && dictionaryBeanList.size() > 0) {
                    tv_value.setText(getDicValue(value, dictionaryBeans));
                    for (DictionaryBean dictionaryBean : dictionaryBeanList) {
                        if (dictionaryBean.getValue().equals(value)) {
                            tv_value.setText(dictionaryBean.getTitle());
                        }
                    }
                }
            }
        }

        List<DictionaryBean> finalDictionaryBeans = dictionaryBeans;
        BsSelectDialog bsSelectDialog = new BsSelectDialog(new BsSelectDialog.BsSelectIF() {//选择框
            @Override
            public void onBsSelect(int type, int index) {

                //点击取消 清除原来选中的数据
                if (index == finalDictionaryBeans.size() - 1) {
                    tv_value.setText("");
                    jsonObject.addProperty(formFiledBean.getDbFieldName(), "");
                    resetChildComponent("", formFiledBean, jsonObject);
                    adapter.notifyDataSetChanged();
                } else {
                    DictionaryBean dictionaryBean = finalDictionaryBeans.get(index);
                    String value = dictionaryBean.getValue();
                    String oldValue = jsonObject.get(formFiledBean.getDbFieldName()).getAsString();
                    if(!value.equals(oldValue)){//两次操作选择相同则子类不重置
                        tv_value.setText(dictionaryBean.getTitle());
                        jsonObject.addProperty(formFiledBean.getDbFieldName(), value);
                        resetChildComponent(value, formFiledBean, jsonObject);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });


        ll_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isChild = formFiledBean.isLinkDownChild();
                if (isChild) {//当前联动组件为子组件
                    if(TextUtils.isEmpty(formFiledBean.getDictParentId())){//parentId为空则不请求
                        dictionaryBeans.clear();//清除上次请求字典数据
                        addCancelDictItem(dictionaryBeans);
                        bsSelectDialog.showPopuWindow_DictListData(context, 0, formFiledBean.getDbFieldTxt(), dictionaryBeans);
                    }else{
                        getDictListByCode(formFiledBean.getDictTableBean(), formFiledBean.getDictParentId(), isChild, bsSelectDialog, formFiledBean.getDbFieldTxt(), dictionaryBeans);
                    }
                } else {//当前联动组件为根组件
                    getDictListByCode(formFiledBean.getDictTableBean(), "", isChild, bsSelectDialog, formFiledBean.getDbFieldTxt(), dictionaryBeans);
                }


            }
        });
    }


    /**
     *获取字典,获取成功后展示字典选项
     *
     * @param dictTableBean
     * @param pid
     * @param isChild
     * @param bsSelectDialog
     * @param dialogTitle
     * @param dictionaryBeans
     */
    private void getDictListByCode(DictTableBean dictTableBean, String pid, boolean isChild, BsSelectDialog bsSelectDialog, String dialogTitle, List<DictionaryBean> dictionaryBeans) {
        Map<String, String> parasMap = new HashMap<>();
        parasMap.put("table", dictTableBean.getTable());
        parasMap.put("txt", dictTableBean.getTxt());
        parasMap.put("key", dictTableBean.getKey());
        parasMap.put("idField", dictTableBean.getIdField());
        parasMap.put("pidField", dictTableBean.getPidField());
        if (isChild) {//组件为联动组建子组件
            parasMap.put("pidValue",pid);
        } else {//组件为联动组建根组件
            parasMap.put("condition", dictTableBean.getCondition());
        }
        FormDetailActivity activity = (FormDetailActivity) context;
        RetrofitHelper.getApiService()
                .getDictByCode(parasMap)
                .compose(activity.<BasicResponse<List<LinkDownDictBean>>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<BasicResponse<List<LinkDownDictBean>>>() {
                    @Override
                    public void onSuccess(BasicResponse<List<LinkDownDictBean>> response) {
                        if (null != response) {
                            if (null != response.getResult()) {
                                List<LinkDownDictBean> datas = response.getResult();
                                if (null != datas && datas.size() > 0) {
                                    dictionaryBeans.clear();//清除上次请求字典数据
                                    for (LinkDownDictBean linkDownDictBean : datas) {
                                        DictionaryBean dictionaryBean = new DictionaryBean();
                                        dictionaryBean.setTitle(linkDownDictBean.getLabel());
                                        dictionaryBean.setValue(linkDownDictBean.getId());
                                        dictionaryBean.setText(linkDownDictBean.getLabel());
                                        dictionaryBeans.add(dictionaryBean);
                                    }
                                }
                            }
                        }
                        addCancelDictItem(dictionaryBeans);
                        bsSelectDialog.showPopuWindow_DictListData(context, 0, dialogTitle, dictionaryBeans);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        addCancelDictItem(dictionaryBeans);
                        bsSelectDialog.showPopuWindow_DictListData(context, 0, dialogTitle, dictionaryBeans);
                    }

                    @Override
                    public void onException(ExceptionReason reason) {
                        super.onException(reason);
                        addCancelDictItem(dictionaryBeans);
                        bsSelectDialog.showPopuWindow_DictListData(context, 0, dialogTitle, dictionaryBeans);
                    }
                });
    }

    /**
     * 添加取消字典项
     *
     * @param dictionaryBeans
     */
    private void addCancelDictItem(List<DictionaryBean> dictionaryBeans) {
        if (!dictionaryBeans.contains(cancelbean)) {//取消项
            dictionaryBeans.add(cancelbean);
        }
    }

    /**
     * 重置子组件选项和值
     *
     * @param pId
     * @param formFiledBean
     * @param jsonObject
     */
    private void resetChildComponent(String pId, FormFiledBean formFiledBean, JsonObject jsonObject) {
        List<String> nameList = formFiledBean.getChildHierarchyName();
        if(null == nameList || nameList.size() < 1)
            return;
        for (int i = 0; i < nameList.size(); i++) {
            jsonObject.addProperty(nameList.get(i), "");
            if(i == 0){
                for(FormFiledBean filedBean :formFiledBeanList){
                    if(nameList.get(i).equals(filedBean.getDbFieldName())){
                        filedBean.setDictParentId(pId);
                    }
                }
            }
        }
    }

    /**
     * 设置所有组件（当父级字典改变时，刷新重置子选项）
     *
     * @param formFiledBeanList
     */
    public void setFormBeanList(List<FormFiledBean> formFiledBeanList) {
        this.formFiledBeanList = formFiledBeanList;
    }


}
