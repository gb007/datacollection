package com.hollysmart.formmodule.caiapi;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hollysmart.formmodule.bean.FormFiledBean;
import com.hollysmart.formmodule.bean.PicBean;
import com.hollysmart.formmodule.caiapi.taskpool.INetModel;
import com.hollysmart.formmodule.caiapi.taskpool.OnNetRequestListener;
import com.hollysmart.formmodule.retrofit.BasicResponse;
import com.hollysmart.formmodule.retrofit.DefaultObserver;
import com.hollysmart.formmodule.retrofit.RetrofitHelper;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 表单数据上传（新增或更新）
 */
public class UploadFormDataAPI implements INetModel {

    private String formId;
    private String dataId;
    //表单filed控件
    private List<FormFiledBean> filedBeanList;
    private JsonObject dataJson;
    private boolean isAdd;
    private OnNetRequestListener onNetRequestListener;

    public UploadFormDataAPI(boolean isAdd, String formId, String dataId, List<FormFiledBean> filedBeanList, JsonObject dataJson, OnNetRequestListener onNetRequestListener) {
        this.formId = formId;
        this.dataId = dataId;
        this.filedBeanList = filedBeanList;
        this.dataJson = dataJson;
        this.isAdd = isAdd;
        this.onNetRequestListener = onNetRequestListener;
    }

    @Override
    public void request(Context context) {

        JsonObject uploadData = setUploadJsonData();

        if (isAdd) {
            addFormData(uploadData);
        } else {
            updateFormData(uploadData);
        }
    }

    /**
     * 新增采集数据
     */
    private void addFormData(JsonObject dataJson) {

        Gson gson = new Gson();
        String str_dataJson = gson.toJson(dataJson);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), str_dataJson);
        RetrofitHelper.getApiService()
                .postFormData(formId, requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<BasicResponse>() {
                    @Override
                    public void onSuccess(BasicResponse response) {
                        if (null != response) {
                            if (response.isSuccess()) {//成功
                                onNetRequestListener.OnResult(true, null, null);

                            } else {
                                onNetRequestListener.OnResult(false, response.getMessage(), null);
                            }
                        } else {
                            onNetRequestListener.OnResult(false, null, null);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        onNetRequestListener.OnResult(false, null, null);
                    }

                    @Override
                    public void onException(ExceptionReason reason) {
                        super.onException(reason);
                        onNetRequestListener.OnResult(false, null, null);
                    }
                });
    }


    /**
     * 更新采集数据
     *
     * @param dataJson
     */
//,dataId
    private void updateFormData(JsonObject dataJson) {

        Gson gson = new Gson();
        String str_dataJson = gson.toJson(dataJson);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), str_dataJson);
        RetrofitHelper.getApiService()
                .putFormData(formId, requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<BasicResponse>() {
                    @Override
                    public void onSuccess(BasicResponse response) {
                        if (null != response) {
                            if (response.isSuccess()) {
                                onNetRequestListener.OnResult(true, null, null);

                            } else {
                                onNetRequestListener.OnResult(false, response.getMessage(), null);
                            }

                        } else {
                            onNetRequestListener.OnResult(false, null, null);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        onNetRequestListener.OnResult(false, null, null);
                    }

                    @Override
                    public void onException(ExceptionReason reason) {
                        super.onException(reason);
                        onNetRequestListener.OnResult(false, null, null);
                    }
                });

    }


    /**
     * 设置上传成功后图片地址到图片地址字段
     * 过滤出表单上传所需字段
     *
     * @return
     */
    private JsonObject setUploadJsonData() {
        JsonObject uploadDataJsonObject = new JsonObject();
        for (FormFiledBean formFiledBean : filedBeanList) {
            if (!isAdd || !formFiledBean.getDbFieldName().equals("id")) {//1展示的字段 0隐藏的字段
                if (formFiledBean.getFieldShowType().equals("image")) {//图片类型，设置图片的七牛地址
                    String imgsUrl = "";
                    List<PicBean> picList = formFiledBean.getPic();
                    if (null != picList && picList.size() > 0) {
                        for (PicBean picInfo : picList) {
                            if (!TextUtils.isEmpty(picInfo.getImageUrl())) {
                                imgsUrl += (picInfo.getImageUrl() + ",");
                            }
                        }
                        if (imgsUrl.length() > 1) {
                            imgsUrl = imgsUrl.substring(0, imgsUrl.length() - 1);
                        }
                    }
                    if (null != dataJson.get(formFiledBean.getDbFieldName()).getAsString()) {
                        uploadDataJsonObject.addProperty(formFiledBean.getDbFieldName(), imgsUrl);
                    }
                } else {//其他类型，设置字段值
                    if (null != dataJson.get(formFiledBean.getDbFieldName()).getAsString()) {
                        String value = dataJson.get(formFiledBean.getDbFieldName()).getAsString();
                        if (TextUtils.isEmpty(value)) {
                            uploadDataJsonObject.addProperty(formFiledBean.getDbFieldName(), "");
                        } else {
                            uploadDataJsonObject.addProperty(formFiledBean.getDbFieldName(), value);
                        }
                    }
                }
            }

        }
        return uploadDataJsonObject;
    }
}
