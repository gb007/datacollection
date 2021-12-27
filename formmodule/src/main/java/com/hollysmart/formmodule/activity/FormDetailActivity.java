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
 * 表单详情页面
 */
@Route(path = "/form/formdetail")
public class FormDetailActivity extends CaiBaseActivity {

    private RecyclerView recy_view;
    private TextView tv_title;
    private TextView tv_shure;


    //目标项是否在最后一个可见项之后
    private boolean mShouldScroll;
    //记录目标项位置
    private int mToPosition;

    //新增或编辑
    @Autowired
    public boolean isAdd;
    //查看或编辑
    @Autowired
    public boolean isCheck;
    //表单Id
    @Autowired
    public String formId;
    //数据Id
    @Autowired
    public String dataId;
    //关联外键Id
    @Autowired
    public String fk_fd_Id;
    //表单标题
    @Autowired
    public String title;
    //表单数据展示字段值
    @Autowired
    public String strDataJson;

    public int resultFreshCode = 9999;//新增或修改数据成功后返回刷新页面code

    //表单filed控件
    private List<FormFiledBean> filedBeanList;
    //数据字典
    private Map<String, List<DictionaryBean>> dicMap;
    //表单json对象
    private JsonObject dataJsonObject;
    //数据列表json对象
    private JsonObject jsonObject;
    private FormAdapter formAdapter;
    //表单数据正则验证list
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
        //设置布局管理器
        recy_view.setLayoutManager(layoutManager);
        tv_shure.setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        //解决Android键盘弹出把整个activity页面（包含statusbar）顶出屏幕Bug
        AndroidBug5497Workaround.assistActivity(this);
    }

    @Override
    public void init() {
        //ARouter注入
        ARouter.getInstance().inject(this);
        regularList = RegularExpressionUtils.getInstatce().getRegularList();//获取正则列表

//        isAdd = getIntent().getBooleanExtra("isAdd", false);
//        isCheck = getIntent().getBooleanExtra("isCheck", true);
//        formId = getIntent().getStringExtra("formId");
//        title = getIntent().getStringExtra("title");

//        if (!isAdd) {//编辑或新增
//            dataId = getIntent().getStringExtra("dataId");//数据Id
//            String str_venueJson = getIntent().getStringExtra("dataJson");
//            Gson gson = new Gson();
//            jsonObject = gson.fromJson(str_venueJson, JsonObject.class);
//        } else {//新建
//            fk_fd_Id = getIntent().getStringExtra("fk_fd_Id");//外键Id
//        }

        if (!isAdd) {
            Gson gson = new Gson();
            jsonObject = gson.fromJson(strDataJson, JsonObject.class);
        }


        if (isCheck) {//查看或编辑
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
                KeyBoardUtil.hiddenKeyBoard(FormDetailActivity.this);//滑动时键盘显示则隐藏键盘
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
            //验证数据必填和对数据内容正则验证　
            if (!checkedFormData()) {
                return;
            }
            //提交表单图片以及数据
            submitForm();
        }
    }

    /**
     * 获取表单结构
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
     * 获取字典
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
     * 请求异常或错误
     */
    void setErrorRequestStatus() {
        showDialogLoadFailed();
    }


    /**
     * 上传form表单；
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
                //上传表单数据
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

        //上传图片
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
        if (isAdd) {//新增  设置加载框展示文本，以及新增成功，失败展示文本
            showLoadingDialog("正在同步新增的资源,请稍等...", "新增成功", "新增失败", true);
        } else {//修改   设置加载框展示文本，以及修改成功，失败展示文本
            showLoadingDialog("正在同步修改的资源,请稍等...", "修改成功", "修改失败", true);
        }
        taskPool.execute(FormDetailActivity.this, listener);
    }


    /**
     * 验证数据必填和对数据内容正则验证
     *
     * @return 返回验证结果
     */
    private boolean checkedFormData() {
        boolean valid = true;

        for (FormFiledBean formFiledBean : filedBeanList) {//初始化提示设为不显示
            formFiledBean.setShowTips(false);
        }

        for (FormFiledBean formFiledBean : filedBeanList) {
            if (formFiledBean.getIsShowForm() == 1) {
                //验证必填
                checkFormMustInPut(formFiledBean);
                if (formFiledBean.isShowTips()) {//显示必填提示
                    smoothMoveToPosition(recy_view, formFiledBean.getPosition());
                    formAdapter.notifyDataSetChanged();
                    return false;
                }
                //内容正则验证
                boolean checkResult = checkFormRule(formFiledBean);
                if (!checkResult) {//内容正则验证
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
     * 检查表单是否是必填项
     */
    private void checkFormMustInPut(FormFiledBean checkedFormBean) {
        checkedFormBean.setShowTips(false);
        if (checkedFormBean.getFieldMustInput().equals("1")) {//1必填 0非必填
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
     * 检查表单输入项是否合法
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
     * 上传数据
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
                if (isOk) {//数据上传成功，返回上一页
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
     * 重新设置JsonObject对象重的key的value
     *
     * @param oldObject
     * @param newObject
     */
    private void resetJsonObjectValue(JsonObject oldObject, JsonObject newObject) {
        Set<Map.Entry<String, JsonElement>> entries = oldObject.entrySet();
        for (Map.Entry<String, JsonElement> entry : entries) {
            System.out.println("键名:" + entry.getKey());  //输出键名
            String value = newObject.get(entry.getKey()).getAsString();
            if (null != value) {
                oldObject.addProperty(entry.getKey(), value);
            }
        }
    }


    /**
     * 设置联动组件的子项
     *
     * @param filedBeanList
     */
    private void setLinkDownChildConponent(List<FormFiledBean> filedBeanList) {
        Gson gson = new Gson();
        for (FormFiledBean filedBean : filedBeanList) {
            if (filedBean.getFieldShowType().equals("link_down")) {//联动组件
                String strDictTable = filedBean.getDictTable();
                if (!TextUtils.isEmpty(strDictTable)) {
                    DictTableBean dictTableBean = gson.fromJson(strDictTable, DictTableBean.class);
                    if (null != dictTableBean) {
                        //设置联动组件跟组件字典的联动关系类
                        filedBean.setDictTableBean(dictTableBean);
                        //设置联动组件显示时的字典list的Key
                        filedBean.setDictKey(filedBean.getDbFieldName());
                        //联动组件子项的id
                        String tLinkField = dictTableBean.getLinkField();
                        if (!TextUtils.isEmpty(tLinkField)) {
                            String[] fileds = tLinkField.split(",");
                            //设置联动组件根组件层级关系，子组件名称
                            List<String> rootHierarchyName = setChildHierarchyName(fileds, 0);
                            filedBean.setChildHierarchyName(rootHierarchyName);
                            for (int i = 0; i < fileds.length; i++) {//联动组件子项的id
                                String fieldName = fileds[i];
                                for (FormFiledBean bean : filedBeanList) {//遍历表单组件找出组件子项
                                    if (bean.getDbFieldName().equals(fieldName)) {
                                        //标记为联动组件子控件
                                        bean.setLinkDownChild(true);
                                        //设置联动组件字典的联动关系类
                                        bean.setDictTableBean(dictTableBean);
                                        //设置联动组件显示时的字典list的Key
                                        bean.setDictKey(filedBean.getDbFieldName());
                                        if (i != fileds.length - 1) { //设置联动组件子组件层级关系，子组件名称;最后一项无子组件
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
     * 设置组件子组件层级每层名称
     *
     * @param fileds 层级关系数组
     * @param index  当前层级索引
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
     * 若是新增则产生一个JsonData的空数据对象
     * 若不是新增则把数据重设到表单字段中
     *
     * @param filedBeanList
     */
    private void setJsonData(List<FormFiledBean> filedBeanList) {

        if (isAdd) {//新增
            for (FormFiledBean formFiledBean : filedBeanList) {
                String dbFieldName = formFiledBean.getDbFieldName();
                if (dbFieldName.equals("fd_fk") && !TextUtils.isEmpty(fk_fd_Id)) {//有关联表，设置外键
                    dataJsonObject.addProperty(dbFieldName, fk_fd_Id);
                } else {
                    if (dbFieldName.equals("location_lat")) {//纬度字段存放经纬度默认值
                        String mapCenterPoint = formFiledBean.getMapCenterPoint();
                        if (TextUtils.isEmpty(mapCenterPoint)) {//无默认值
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
                    } else {//其他字段设置默认值
                        String dbFieldDefaultValue = formFiledBean.getFieldDefaultValue();
                        if (TextUtils.isEmpty(dbFieldDefaultValue)) {
                            dataJsonObject.addProperty(dbFieldName, "");
                        } else {
                            dataJsonObject.addProperty(dbFieldName, dbFieldDefaultValue);
                        }
                    }
                }
            }
        } else {//展示或修改
            for (FormFiledBean formFiledBean : filedBeanList) {
                String dbFieldName = formFiledBean.getDbFieldName();
                if (null != jsonObject.get(dbFieldName)) {//原数据存在字段
                    String value = jsonObject.get(dbFieldName).getAsString();
                    if (TextUtils.isEmpty(value)) {
                        dataJsonObject.addProperty(dbFieldName, "");
                    } else {
                        dataJsonObject.addProperty(dbFieldName, value);
                    }
                } else {//表单更新字段
                    if (dbFieldName.equals("location_lat")) {//纬度存放经纬度默认值
                        String mapCenterPoint = formFiledBean.getMapCenterPoint();
                        if (TextUtils.isEmpty(mapCenterPoint)) {//无默认值
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
                    } else {//其他字段设置默认值
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
     * 设置图片
     */
    private void setImages() {
        for (FormFiledBean formFiledBean : filedBeanList) {
            if (formFiledBean.getFieldShowType().equals("image")) {
                List<PicBean> pics = new ArrayList<>();
                if (null != dataJsonObject.get(formFiledBean.getDbFieldName())) {//防止更新新增字段
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
     * 滑动到指定位置
     */
    private void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        // 第一个可见位置
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
        if (position < firstItem) {
            // 第一种可能:跳转位置在第一个可见位置之前
            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            // 第二种可能:跳转位置在第一个可见位置之后
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                mRecyclerView.smoothScrollBy(0, top);
            }
        } else {
            // 第三种可能:跳转位置在最后可见项之后
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
                //地图
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

            //图片；
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
     * 经纬度转化为物理地址
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
     * 刷新列表数据返回上一级列表
     */
    private void returnAndRefreList() {
        new Handler().postDelayed(new Runnable() {//延迟1s执行，等页面动画执行完成再返回
            public void run() {
                //execute the task
                //TODO
//                if (formId.equals(Constants.FORMID_VENUES)) {//刷新场所列表
//                    EventBus.getDefault().post(new EB_Refresh_Venue());
//                } else if (formId.equals(Constants.FORMID_EQUIPMENT)) {//刷新器材列表
//                    EventBus.getDefault().post(new EB_Refresh_Equipment());
//                }

                setResult(resultFreshCode);
                finish();
            }
        }, 1000);
    }


    /**
     * 键盘事件监听
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {//键盘返回事件监听
            showBackDialog();
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 退出页面弹出alert弹窗
     */
    private void showBackDialog() {

        new CircleDialog.Builder().setTitle("未保存数据将丢失")
                .setText("建议您编辑之后保存该条数据")//内容
                .setPositive("继续退出", new OnButtonClickListener() {
                    @Override
                    public boolean onClick(View v) {
                        finish();
                        return true;
                    }
                })
                .setNegative("再次编辑", new OnButtonClickListener() {
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