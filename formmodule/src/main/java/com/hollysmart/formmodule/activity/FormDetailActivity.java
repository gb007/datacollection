package com.hollysmart.formmodule.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hjq.toast.ToastUtils;
import com.hollysmart.formmodule.R;
import com.hollysmart.formmodule.Utils.AndroidBug5497Workaround;
import com.hollysmart.formmodule.Utils.KeyBoardUtil;
import com.hollysmart.formmodule.Utils.LogUtils;
import com.hollysmart.formmodule.Utils.RegularExpressionUtils;
import com.hollysmart.formmodule.adapter.FormAdapter;
import com.hollysmart.formmodule.base.CaiBaseActivity;
import com.hollysmart.formmodule.bean.DictTableBean;
import com.hollysmart.formmodule.bean.DictionaryBean;
import com.hollysmart.formmodule.bean.FormDictionaryBean;
import com.hollysmart.formmodule.bean.FormFiledBean;
import com.hollysmart.formmodule.bean.PicBean;
import com.hollysmart.formmodule.caiapi.UpLoadFormPicAPI;
import com.hollysmart.formmodule.caiapi.UploadFormDataAPI;
import com.hollysmart.formmodule.caiapi.taskpool.OnNetRequestListener;
import com.hollysmart.formmodule.caiapi.taskpool.TaskPool;
import com.hollysmart.formmodule.common.Constants;
import com.hollysmart.formmodule.retrofit.BasicResponse;
import com.hollysmart.formmodule.retrofit.DefaultObserver;
import com.hollysmart.formmodule.retrofit.RetrofitHelper;
import com.lwkandroid.imagepicker.ImagePicker;
import com.lwkandroid.imagepicker.data.ImageBean;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.view.listener.OnButtonClickListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import lombok.SneakyThrows;

/**
 * ??????????????????
 */
@Route(path = "/form/formdetail")
public class FormDetailActivity extends CaiBaseActivity {

    private RecyclerView recy_view;
    private TextView tv_title;
    private TextView tv_shure;


    //?????????????????????????????????????????????
    private boolean mShouldScroll;
    //?????????????????????
    private int mToPosition;

    //???????????????
    @Autowired
    public boolean isAdd;
    //???????????????
    @Autowired
    public boolean isCheck;
    //??????Id
    @Autowired
    public String formId;
    //??????Id
    @Autowired
    public String dataId;
    //????????????Id
    @Autowired
    public String fk_fd_Id;
    //????????????
    @Autowired
    public String title;
    //???????????????????????????
    @Autowired
    public String strDataJson;

    public int resultFreshCode = 9999;//????????????????????????????????????????????????code

    //??????filed??????
    private List<FormFiledBean> filedBeanList;
    //????????????
    private Map<String, List<DictionaryBean>> dicMap;
    //??????json??????
    private JsonObject dataJsonObject;
    //????????????json??????
    private JsonObject jsonObject;
    private FormAdapter formAdapter;
    //????????????????????????list
    private List<RegularExpressionUtils.Regular> regularList;

    @Override
    public int layoutResID() {
        return R.layout.form_module_activity_form_detail;
    }

    @Override
    public boolean setTranslucent() {
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void findView() {
        recy_view = findViewById(R.id.recy_view);
        tv_title = findViewById(R.id.tv_title);
        tv_shure = findViewById(R.id.tv_shure);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //?????????????????????
        recy_view.setLayoutManager(layoutManager);
        tv_shure.setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        //??????Android?????????????????????activity???????????????statusbar???????????????Bug
        AndroidBug5497Workaround.assistActivity(this);
    }

    @Override
    public void init() {
        //ARouter??????
        ARouter.getInstance().inject(this);
        regularList = RegularExpressionUtils.getInstatce().getRegularList();//??????????????????

//        isAdd = getIntent().getBooleanExtra("isAdd", false);
//        isCheck = getIntent().getBooleanExtra("isCheck", true);
//        formId = getIntent().getStringExtra("formId");
//        title = getIntent().getStringExtra("title");

//        if (!isAdd) {//???????????????
//            dataId = getIntent().getStringExtra("dataId");//??????Id
//            String str_venueJson = getIntent().getStringExtra("dataJson");
//            Gson gson = new Gson();
//            jsonObject = gson.fromJson(str_venueJson, JsonObject.class);
//        } else {//??????
//            fk_fd_Id = getIntent().getStringExtra("fk_fd_Id");//??????Id
//        }

        if (!isAdd) {
            Gson gson = new Gson();
            jsonObject = gson.fromJson(strDataJson, JsonObject.class);
        }


        if (isCheck) {//???????????????
            tv_shure.setVisibility(View.GONE);
        } else {
            tv_shure.setVisibility(View.VISIBLE);
        }

        dataJsonObject = new JsonObject();
        filedBeanList = new ArrayList<>();
        dicMap = new HashMap<>();
        formAdapter = new FormAdapter(filedBeanList, dicMap, dataJsonObject, isCheck);
        recy_view.setAdapter(formAdapter);
        recy_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mShouldScroll && RecyclerView.SCROLL_STATE_IDLE == newState) {
                    mShouldScroll = false;
                    smoothMoveToPosition(recy_view, mToPosition);
                }
                KeyBoardUtil.hiddenKeyBoard(FormDetailActivity.this);//????????????????????????????????????
            }
        });


        tv_title.setText(title);
        showLoadingDialog();
        getFormFiledList();

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.iv_back) {
            showBackDialog();
        } else if (v.getId() == R.id.tv_shure) {
            //???????????????????????????????????????????????????
            if (!checkedFormData()) {
                return;
            }
            //??????????????????????????????
            submitForm();
        }
    }

    /**
     * ??????????????????
     */
    private void getFormFiledList() {
        RetrofitHelper.getApiService()
                .getForm(formId)
                .compose(this.<BasicResponse<List<FormFiledBean>>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<BasicResponse<List<FormFiledBean>>>() {
                    @Override
                    public void onSuccess(BasicResponse<List<FormFiledBean>> response) {
                        if (null != response) {
                            if (null != response.getResult()) {
                                List<FormFiledBean> datas = response.getResult();
                                if (null != datas && datas.size() > 0) {
                                    setLinkDownChildConponent(datas);
                                    filedBeanList.addAll(datas);
                                    setJsonData(filedBeanList);
                                    setImages();
                                    getFormDicListData();
                                } else {
                                    setErrorRequestStatus();
                                }

                            } else {
                                setErrorRequestStatus();
                            }
                        } else {
                            setErrorRequestStatus();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        setErrorRequestStatus();
                    }

                    @Override
                    public void onException(ExceptionReason reason) {
                        super.onException(reason);
                        setErrorRequestStatus();
                    }
                });

    }

    /**
     * ????????????
     */
    private void getFormDicListData() {
        RetrofitHelper.getApiService().getDicListData(formId)
                .compose(this.<BasicResponse<FormDictionaryBean>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .safeSubscribe(new DefaultObserver<BasicResponse<FormDictionaryBean>>() {
                    @Override
                    public void onSuccess(BasicResponse<FormDictionaryBean> response) {
                        if (null != response) {
                            if (null != response.getResult()) {
                                Map<String, List<DictionaryBean>> datas = response.getResult().getDictOptions();
                                if (null != datas && datas.size() > 0) {
                                    showDialogLoadSuccess();
                                    dicMap.putAll(datas);
                                    formAdapter.notifyDataSetChanged();
                                    LogUtils.d("datas:::", datas.toString());
                                } else {
                                    setErrorRequestStatus();
                                }
                            } else {
                                setErrorRequestStatus();
                            }
                        } else {
                            setErrorRequestStatus();
                        }
                    }
                });
    }


    /**
     * ?????????????????????
     */
    void setErrorRequestStatus() {
        showDialogLoadFailed();
    }


    /**
     * ??????form?????????
     */
    private void submitForm() {

        TaskPool taskPool = new TaskPool();
        OnNetRequestListener listener = new OnNetRequestListener() {
            @Override
            public void UploadPicSuccess(String formFieldName, PicBean bean) {
                for (FormFiledBean formFiledBean : filedBeanList) {
                    if (formFiledBean.getDbFieldName().equals(formFieldName)) {
                        List<PicBean> picList = formFiledBean.getPic();
                        for (PicBean picBean : picList) {
                            if (picBean.getPicId().equals(bean.getPicId())) {
                                picBean.setImageUrl(bean.getImageUrl());
                            }
                        }
                    }
                }
            }

            @Override
            public void OnNext() {
                taskPool.execute(FormDetailActivity.this, this);
            }

            @Override
            public void onFinish() {
                //??????????????????
                uploadFormData();
            }

            @Override
            public void OnResult(boolean isOk, String msg, Object object) {
            }

            @Override
            public void UploadPicError() {
                taskPool.clear();
                showDialogLoadFailed();
            }
        };

        //????????????
        for (FormFiledBean formFiledBean : filedBeanList) {
            if (formFiledBean.getFieldShowType().equals("image")) {
                List<PicBean> picList = formFiledBean.getPic();
                if (null != picList && picList.size() > 0) {
                    for (PicBean jdPicInfo : picList) {
                        if (!TextUtils.isEmpty(jdPicInfo.getFilePath()) && jdPicInfo.getIsAddFlag() != 1 && (TextUtils.isEmpty(jdPicInfo.getImageUrl()))) {
                            taskPool.addTask(new UpLoadFormPicAPI(formFiledBean.getDbFieldName(), jdPicInfo, listener));
                        }
                    }
                }
            }
        }
        if (isAdd) {//??????  ?????????????????????????????????????????????????????????????????????
            showLoadingDialog("???????????????????????????,?????????...", "????????????", "????????????", true);
        } else {//??????   ?????????????????????????????????????????????????????????????????????
            showLoadingDialog("???????????????????????????,?????????...", "????????????", "????????????", true);
        }
        taskPool.execute(FormDetailActivity.this, listener);
    }


    /**
     * ????????????????????????????????????????????????
     *
     * @return ??????????????????
     */
    private boolean checkedFormData() {
        boolean valid = true;

        for (FormFiledBean formFiledBean : filedBeanList) {//??????????????????????????????
            formFiledBean.setShowTips(false);
        }

        for (FormFiledBean formFiledBean : filedBeanList) {
            if (formFiledBean.getIsShowForm() == 1) {
                //????????????
                checkFormMustInPut(formFiledBean);
                if (formFiledBean.isShowTips()) {//??????????????????
                    smoothMoveToPosition(recy_view, formFiledBean.getPosition());
                    formAdapter.notifyDataSetChanged();
                    return false;
                }
                //??????????????????
                boolean checkResult = checkFormRule(formFiledBean);
                if (!checkResult) {//??????????????????
                    formFiledBean.setShowTips(true);
                    smoothMoveToPosition(recy_view, formFiledBean.getPosition());
                    formAdapter.notifyDataSetChanged();
                    return false;
                } else {
                    formFiledBean.setShowTips(false);
                }
            }
        }
        formAdapter.notifyDataSetChanged();
        return valid;
    }


    /**
     * ??????????????????????????????
     */
    private void checkFormMustInPut(FormFiledBean checkedFormBean) {
        checkedFormBean.setShowTips(false);
        if (checkedFormBean.getFieldMustInput().equals("1")) {//1?????? 0?????????
            if (TextUtils.isEmpty(dataJsonObject.get(checkedFormBean.getDbFieldName()).getAsString())) {
                if ((checkedFormBean.getFieldShowType().equals("image")) && (checkedFormBean.getPic() != null && checkedFormBean.getPic().size() > 0)) {
                    checkedFormBean.setShowTips(false);
                } else {
                    ToastUtils.show("*" + checkedFormBean.getDbFieldTxt() + "  " + this.getString(R.string.form_model_not_null));
                    checkedFormBean.setShowTips(true);
                }
            } else {
                checkedFormBean.setShowTips(false);
            }
        }
    }

    /**
     * ?????????????????????????????????
     */
    private boolean checkFormRule(FormFiledBean checkedFormBean) {
        boolean checkresult = true;
        String validType = "";
        if (null != checkedFormBean.getFieldValidType()) {
            validType = checkedFormBean.getFieldValidType().toString();
            for (RegularExpressionUtils.Regular regular : regularList) {
                if (regular.getKey().equals(validType)) {
                    String par = regular.getExpression();
                    Pattern p = Pattern.compile(par);
                    String value = dataJsonObject.get(checkedFormBean.getDbFieldName()).getAsString();
                    if (!TextUtils.isEmpty(value)) {
                        Matcher m = p.matcher(value);
                        if (!m.matches()) {
                            ToastUtils.show(regular.getTip());
                            checkresult = false;
                        }
                    }
                }
            }
        }
        return checkresult;
    }


    /**
     * ????????????
     */
    private void uploadFormData() {

        OnNetRequestListener listener = new OnNetRequestListener() {
            @Override
            public void UploadPicSuccess(String formFieldName, PicBean bean) {
            }

            @Override
            public void OnNext() {
            }

            @Override
            public void onFinish() {
            }

            @Override
            public void OnResult(boolean isOk, String msg, Object object) {
                if (isOk) {//????????????????????????????????????
                    showDialogLoadSuccess();
                    returnAndRefreList();
                } else {
                    showDialogLoadFailed();
                }
            }

            @Override
            public void UploadPicError() {
            }
        };
        UploadFormDataAPI uploadFormDataAPI = new UploadFormDataAPI(isAdd, formId, dataId, filedBeanList, dataJsonObject, listener);
        uploadFormDataAPI.request(this);

    }


    /**
     * ????????????JsonObject????????????key???value
     *
     * @param oldObject
     * @param newObject
     */
    private void resetJsonObjectValue(JsonObject oldObject, JsonObject newObject) {
        Set<Map.Entry<String, JsonElement>> entries = oldObject.entrySet();
        for (Map.Entry<String, JsonElement> entry : entries) {
            System.out.println("??????:" + entry.getKey());  //????????????
            String value = newObject.get(entry.getKey()).getAsString();
            if (null != value) {
                oldObject.addProperty(entry.getKey(), value);
            }
        }
    }


    /**
     * ???????????????????????????
     *
     * @param filedBeanList
     */
    private void setLinkDownChildConponent(List<FormFiledBean> filedBeanList) {
        Gson gson = new Gson();
        for (FormFiledBean filedBean : filedBeanList) {
            if (filedBean.getFieldShowType().equals("link_down")) {//????????????
                String strDictTable = filedBean.getDictTable();
                if (!TextUtils.isEmpty(strDictTable)) {
                    DictTableBean dictTableBean = gson.fromJson(strDictTable, DictTableBean.class);
                    if (null != dictTableBean) {
                        //???????????????????????????????????????????????????
                        filedBean.setDictTableBean(dictTableBean);
                        //????????????????????????????????????list???Key
                        filedBean.setDictKey(filedBean.getDbFieldName());
                        //?????????????????????id
                        String tLinkField = dictTableBean.getLinkField();
                        if (!TextUtils.isEmpty(tLinkField)) {
                            String[] fileds = tLinkField.split(",");
                            //?????????????????????????????????????????????????????????
                            List<String> rootHierarchyName = setChildHierarchyName(fileds, 0);
                            filedBean.setChildHierarchyName(rootHierarchyName);
                            for (int i = 0; i < fileds.length; i++) {//?????????????????????id
                                String fieldName = fileds[i];
                                for (FormFiledBean bean : filedBeanList) {//????????????????????????????????????
                                    if (bean.getDbFieldName().equals(fieldName)) {
                                        //??????????????????????????????
                                        bean.setLinkDownChild(true);
                                        //??????????????????????????????????????????
                                        bean.setDictTableBean(dictTableBean);
                                        //????????????????????????????????????list???Key
                                        bean.setDictKey(filedBean.getDbFieldName());
                                        if (i != fileds.length - 1) { //?????????????????????????????????????????????????????????;????????????????????????
                                            List<String> childHierarchyName = setChildHierarchyName(fileds, i + 1);
                                            bean.setChildHierarchyName(childHierarchyName);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * ???????????????????????????????????????
     *
     * @param fileds ??????????????????
     * @param index  ??????????????????
     * @return
     */
    private List<String> setChildHierarchyName(String[] fileds, int index) {
        List<String> childsName = new ArrayList<>();
        for (int i = index; i < fileds.length; i++) {
            childsName.add(fileds[i]);
        }
        return childsName;
    }


    /**
     * ???????????????????????????JsonData??????????????????
     * ???????????????????????????????????????????????????
     *
     * @param filedBeanList
     */
    private void setJsonData(List<FormFiledBean> filedBeanList) {

        if (isAdd) {//??????
            for (FormFiledBean formFiledBean : filedBeanList) {
                String dbFieldName = formFiledBean.getDbFieldName();
                if (dbFieldName.equals("fd_fk") && !TextUtils.isEmpty(fk_fd_Id)) {//???????????????????????????
                    dataJsonObject.addProperty(dbFieldName, fk_fd_Id);
                } else {
                    if (dbFieldName.equals("location_lat")) {//????????????????????????????????????
                        String mapCenterPoint = formFiledBean.getMapCenterPoint();
                        if (TextUtils.isEmpty(mapCenterPoint)) {//????????????
                            dataJsonObject.addProperty("location_lon", "");
                            dataJsonObject.addProperty("location_lat", "");
                        } else {
                            String[] gps = mapCenterPoint.split(",");
                            if (gps.length > 0) {
                                dataJsonObject.addProperty("location_lon", gps[0]);
                                dataJsonObject.addProperty("location_lat", gps[1]);
                            } else {
                                dataJsonObject.addProperty("location_lon", "");
                                dataJsonObject.addProperty("location_lat", "");
                            }
                        }
                    } else {//???????????????????????????
                        String dbFieldDefaultValue = formFiledBean.getFieldDefaultValue();
                        if (TextUtils.isEmpty(dbFieldDefaultValue)) {
                            dataJsonObject.addProperty(dbFieldName, "");
                        } else {
                            dataJsonObject.addProperty(dbFieldName, dbFieldDefaultValue);
                        }
                    }
                }
            }
        } else {//???????????????
            for (FormFiledBean formFiledBean : filedBeanList) {
                String dbFieldName = formFiledBean.getDbFieldName();
                if (null != jsonObject.get(dbFieldName)) {//?????????????????????
                    String value = jsonObject.get(dbFieldName).getAsString();
                    if (TextUtils.isEmpty(value)) {
                        dataJsonObject.addProperty(dbFieldName, "");
                    } else {
                        dataJsonObject.addProperty(dbFieldName, value);
                    }
                } else {//??????????????????
                    if (dbFieldName.equals("location_lat")) {//??????????????????????????????
                        String mapCenterPoint = formFiledBean.getMapCenterPoint();
                        if (TextUtils.isEmpty(mapCenterPoint)) {//????????????
                            dataJsonObject.addProperty("location_lon", "");
                            dataJsonObject.addProperty("location_lat", "");
                        } else {
                            String[] gps = mapCenterPoint.split(",");
                            if (gps.length > 0) {
                                dataJsonObject.addProperty("location_lon", gps[0]);
                                dataJsonObject.addProperty("location_lat", gps[1]);
                            } else {
                                dataJsonObject.addProperty("location_lon", "");
                                dataJsonObject.addProperty("location_lat", "");
                            }
                        }
                    } else {//???????????????????????????
                        String dbFieldDefaultValue = formFiledBean.getFieldDefaultValue();
                        if (TextUtils.isEmpty(dbFieldDefaultValue)) {
                            dataJsonObject.addProperty(dbFieldName, "");
                        } else {
                            dataJsonObject.addProperty(dbFieldName, dbFieldDefaultValue);
                        }
                    }

                }
            }
        }
    }

    /**
     * ????????????
     */
    private void setImages() {
        for (FormFiledBean formFiledBean : filedBeanList) {
            if (formFiledBean.getFieldShowType().equals("image")) {
                List<PicBean> pics = new ArrayList<>();
                if (null != dataJsonObject.get(formFiledBean.getDbFieldName())) {//????????????????????????
                    String images = dataJsonObject.get(formFiledBean.getDbFieldName()).getAsString();
                    if (!TextUtils.isEmpty(images)) {
                        String[] imgs = images.split(",");
                        if (imgs != null && imgs.length > 0) {
                            for (int i = 0; i < imgs.length; i++) {
                                String id = UUID.randomUUID().toString();
                                PicBean picBean = new PicBean();
                                picBean.setPicId(id);
                                picBean.setFilename(imgs[i]);
                                picBean.setImageUrl(imgs[i]);
                                picBean.setIsAddFlag(0);
                                pics.add(picBean);
                            }
                        }
                    }
                }
                formFiledBean.setPic(pics);
            }
        }
    }


    /**
     * ?????????????????????
     */
    private void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        // ?????????????????????
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // ????????????????????????
        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
        if (position < firstItem) {
            // ???????????????:??????????????????????????????????????????
            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            // ???????????????:??????????????????????????????????????????
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                mRecyclerView.smoothScrollBy(0, top);
            }
        } else {
            // ???????????????:????????????????????????????????????
            mRecyclerView.smoothScrollToPosition(position);
            mToPosition = position;
            mShouldScroll = true;
        }
    }


    @SneakyThrows
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_CODE_MAP:
                //??????
                if (resultCode == Constants.RESULT_CODE_MAP) {
                    String str_dataJson = data.getStringExtra("dataJson");
                    String mark_lat = data.getStringExtra("mark_lat");
                    String mark_lon = data.getStringExtra("mark_lon");
                    if (TextUtils.isEmpty(mark_lat) || TextUtils.isEmpty(mark_lon))
                        return;
                    Gson gson = new Gson();
                    JsonObject data_jsonObject = gson.fromJson(str_dataJson, JsonObject.class);
                    data_jsonObject.addProperty("location_lat", mark_lat);
                    data_jsonObject.addProperty("location_lon", mark_lon);
                    resetJsonObjectValue(dataJsonObject, data_jsonObject);
                    formAdapter.notifyDataSetChanged();
                    getAddr(mark_lat, mark_lon, data_jsonObject);
                }
                break;

            //?????????
            case Constants.REQUEST_CODE_ADD_IMAGE:
                if (resultCode == Constants.RESULT_CODE_ADD_IMAGE) {

                    String str_dataJson = data.getStringExtra("dataJson");
                    Gson gson = new Gson();
                    JsonObject data_jsonObject = gson.fromJson(str_dataJson, JsonObject.class);
                    String dbFieldName = data.getStringExtra("dbFieldName");
                    List<ImageBean> picPaths = data.getParcelableArrayListExtra(ImagePicker.INTENT_RESULT_DATA);
                    for (FormFiledBean formFiledBean : filedBeanList) {
                        if (formFiledBean.getDbFieldName().equals(dbFieldName)) {
                            List<PicBean> pics = formFiledBean.getPic();
                            for (ImageBean item : picPaths) {
                                String id = UUID.randomUUID().toString();
                                PicBean picBean = new PicBean();
                                picBean.setPicId(id);
                                picBean.setFilename(item.getImagePath());
                                picBean.setFilePath(item.getImagePath());
                                picBean.setImageUrl(null);
                                picBean.setIsAddFlag(0);
                                picBean.setIsDownLoad("false");
                                pics.add(picBean);
                            }
                            formFiledBean.setPic(pics);
                        }
                    }

                    resetJsonObjectValue(dataJsonObject, data_jsonObject);
                    formAdapter.notifyDataSetChanged();

                }
                break;
            case Constants.REQUEST_CODE_CUT_IMAGE:
//                setPicToView(data, takePhotoFormBean);
                break;


        }
    }


    /**
     * ??????????????????????????????
     *
     * @param latitude
     * @param longitude
     * @param jsonObject
     * @throws AMapException
     */
    private void getAddr(String latitude, String longitude, JsonObject jsonObject) throws AMapException {
        GeocodeSearch geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                jsonObject.addProperty("address", regeocodeResult.getRegeocodeAddress().getFormatAddress());
                resetJsonObjectValue(dataJsonObject, jsonObject);
                formAdapter.notifyDataSetChanged();
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });
        LatLonPoint latLonPoint = new LatLonPoint(Double.parseDouble(latitude), Double.parseDouble(longitude));
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 10, GeocodeSearch.AMAP);
        geocodeSearch.getFromLocationAsyn(query);

    }


    /**
     * ???????????????????????????????????????
     */
    private void returnAndRefreList() {
        new Handler().postDelayed(new Runnable() {//??????1s?????????????????????????????????????????????
            public void run() {
                //execute the task
                //TODO
//                if (formId.equals(Constants.FORMID_VENUES)) {//??????????????????
//                    EventBus.getDefault().post(new EB_Refresh_Venue());
//                } else if (formId.equals(Constants.FORMID_EQUIPMENT)) {//??????????????????
//                    EventBus.getDefault().post(new EB_Refresh_Equipment());
//                }

                setResult(resultFreshCode);
                finish();
            }
        }, 1000);
    }


    /**
     * ??????????????????
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {//????????????????????????
            showBackDialog();
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * ??????????????????alert??????
     */
    private void showBackDialog() {

        new CircleDialog.Builder().setTitle("????????????????????????")
                .setText("???????????????????????????????????????")//??????
                .setPositive("????????????", new OnButtonClickListener() {
                    @Override
                    public boolean onClick(View v) {
                        finish();
                        return true;
                    }
                })
                .setNegative("????????????", new OnButtonClickListener() {
                    @Override
                    public boolean onClick(View v) {
                        return true;
                    }
                }).show(getSupportFragmentManager());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}