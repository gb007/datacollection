package com.hollysmart.testcollectionmodule;


import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.Utils;
import com.d.lib.xrv.LRecyclerView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hjq.toast.ToastUtils;
import com.hollysmart.testcollectionmodule.Utils.LogUtils;
import com.hollysmart.testcollectionmodule.adapter.VenueListAdapter;
import com.hollysmart.testcollectionmodule.CaiBaseActivity;
import com.hollysmart.testcollectionmodule.bean.FormData;
import com.hollysmart.testcollectionmodule.bean.LoginInfoBean;
import com.hollysmart.testcollectionmodule.bean.event.EB_Refresh_Venue;
import com.hollysmart.testcollectionmodule.common.Constants;
import com.hollysmart.testcollectionmodule.retrofit.BasicResponse;
import com.hollysmart.testcollectionmodule.retrofit.DefaultObserver;
import com.hollysmart.testcollectionmodule.retrofit.RetrofitHelper;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 场地列表页面
 */
@Route(path = "/formlist/dynamiclist")
public class DynamicListActivity extends CaiBaseActivity implements OnRefreshListener, OnLoadMoreListener {

    private ImageView iv_back;
    private TextView tv_maplsit;
    private SmartRefreshLayout refreshLayout;
    private LRecyclerView lv_venueList;
    private RelativeLayout rl_bottom;
    private LinearLayout lay_fragment_ProdutEmpty;
    private int pageNo = 1;
    private List<JsonObject> venueBeanList;
    private VenueListAdapter venueListAdapter;

    @Autowired
    public String token;//引用模块传过来请求的token




    @Override
    public int layoutResID() {
        return R.layout.form_module_activity_dynamic_list;
    }

    @Override
    public boolean setTranslucent() {
        return true;
    }


    @Override
    public void findView() {

        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_maplsit = (TextView) findViewById(R.id.tv_maplsit);
        refreshLayout = (SmartRefreshLayout) findViewById(R.id.smart_refresh);
        lv_venueList = (LRecyclerView) findViewById(R.id.lv_roadList);
        rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);
        lay_fragment_ProdutEmpty = (LinearLayout) findViewById(R.id.lay_fragment_ProdutEmpty);

        iv_back.setOnClickListener(this);
        tv_maplsit.setOnClickListener(this);
        rl_bottom.setOnClickListener(this);
        findViewById(R.id.ll_search).setOnClickListener(this);

        //添加刷新监听
        refreshLayout.setEnableRefresh(true);
        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setEnableHeaderTranslationContent(true);//是否下拉Header的时候向下平移列表或者内容
        refreshLayout.setEnableFooterTranslationContent(true);//是否上拉Footer的时候向上平移列表或者内容
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setOnLoadMoreListener(this);
        refreshLayout.setOnRefreshListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        lv_venueList.setLayoutManager(layoutManager);

    }

    @Override
    public void init() {
        //ARouter注入
        ARouter.getInstance().inject(this);
        Utils.init(getApplication());
        //初始化ToastUtil,常规在Applicaiton中执行
        ToastUtils.init(this.getApplication());
        EventBus.getDefault().register(this);
        venueBeanList = new ArrayList<>();
        venueListAdapter = new VenueListAdapter(this, venueBeanList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        lv_venueList.setLayoutManager(layoutManager);
        lv_venueList.setAdapter(venueListAdapter);
//        login();
        if(!TextUtils.isEmpty(token)){//设置引用采集模块传递过来的token
            Constants.TOKEN = token;
        }

    }

    /**
     * Get请求 根据表单Id获取表单数据列表
     */
    private void getDataList(String formId, int pageNo) {

        Map<String, String> parasMap = new HashMap<>();
        parasMap.put("column", "id");
        parasMap.put("order", "desc");
        parasMap.put("superQueryMatchType", "and");
        parasMap.put("pageNo", pageNo + "");
        parasMap.put("pageSize", Constants.PAGE_SIZE + "");

        RetrofitHelper.getApiService()
                .getFormData(formId, parasMap)
                .compose(this.<BasicResponse<FormData>>bindToLifecycle())
//                .compose(ProgressUtils.<BasicResponse<FormData>>applyProgressBar(this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<BasicResponse<FormData>>() {
                    @Override
                    public void onSuccess(BasicResponse<FormData> response) {
                        showDialogLoadSuccess();
                        if (null != response) {
                            if (null != response.getResult()) {
                                JsonArray datas = response.getResult().getRecords();
                                if (null != datas && datas.size() > 0) {
                                    LogUtils.d("venues::::", datas.size() + "");
                                    List<JsonObject> jsonList = new ArrayList<>();
                                    for (JsonElement json : datas) {
                                        JsonObject obj = json.getAsJsonObject();
                                        jsonList.add(obj);
                                    }

                                    if (pageNo == 1) {//下拉刷新
                                        venueBeanList.clear();
                                        venueBeanList.addAll(jsonList);
                                        refreshLayout.finishRefresh();
                                        lay_fragment_ProdutEmpty.setVisibility(View.GONE);
                                        lv_venueList.setVisibility(View.VISIBLE);
                                        refreshLayout.finishRefresh();
                                    } else {//上拉加载更多
                                        venueBeanList.addAll(jsonList);
                                        refreshLayout.finishLoadMore();
                                    }

                                    if (datas.size() < Constants.PAGE_SIZE) {//无更多数据（数据已全部加载）
                                        refreshLayout.finishLoadMoreWithNoMoreData();
                                    }
                                    venueListAdapter.notifyDataSetChanged();
                                } else {
                                    if (null == datas) {//后台未返回数据对象
                                        setErrorRequestStatus();
                                    } else {//后台返回数据为空集合
                                        if (pageNo == 1) {//
                                            refreshLayout.finishRefresh();
                                            lay_fragment_ProdutEmpty.setVisibility(View.VISIBLE);
                                            lv_venueList.setVisibility(View.GONE);
                                        } else {
                                            refreshLayout.finishLoadMore();
                                        }
                                        refreshLayout.finishLoadMoreWithNoMoreData();
                                    }
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
                        showDialogLoadFailed();
                        setErrorRequestStatus();
                    }

                    @Override
                    public void onException(ExceptionReason reason) {
                        super.onException(reason);
                        showDialogLoadFailed();
                        setErrorRequestStatus();
                    }
                });


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveEvent(EB_Refresh_Venue model) {
        pageNo = 1;
        showLoadingDialog();
        getDataList(Constants.FORMID_VENUES, pageNo);
    }

    /**
     * 登陆获取Token
     */
    private void login() {
        showLoadingDialog();
        RetrofitHelper.getApiService().login(new HashMap()).compose(this.<BasicResponse<LoginInfoBean>>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<BasicResponse<LoginInfoBean>>() {
                    @Override
                    public void onSuccess(BasicResponse<LoginInfoBean> response) {
                        if (null != response) {
                            if (null != response.getResult()) {
                                Constants.TOKEN = response.getResult().getToken();
                                getDataList(Constants.FORMID_VENUES, pageNo);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        showDialogLoadFailed();
                    }


                    @Override
                    public void onException(ExceptionReason reason) {
                        super.onException(reason);
                        showDialogLoadFailed();
                    }
                });
    }


    /**
     * 请求异常或错误
     */
    void setErrorRequestStatus() {
        if (pageNo == 1) {
            refreshLayout.finishRefresh();
            lay_fragment_ProdutEmpty.setVisibility(View.VISIBLE);
            lv_venueList.setVisibility(View.GONE);
        } else {
            ToastUtils.show("获取数据失败");
            refreshLayout.finishLoadMore();
        }
    }





    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.iv_back) {
            finish();
        } else if (v.getId() == R.id.tv_maplsit) {


        } else if (v.getId() == R.id.rl_bottom) {

//            Intent intent = new Intent(mContext, FormDetailActivity.class);
//            intent.putExtra("isAdd", true);
//            intent.putExtra("isCheck", false);
//            intent.putExtra("formId", Constants.FORMID_VENUES);
//            intent.putExtra("title", "场地信息");
//            startActivityForResult(intent, 4);

            ARouter.getInstance().build("/form/formdetail")
                    .withBoolean("isAdd",true)
                    .withBoolean("isCheck",false)
                    .withString("formId",Constants.FORMID_VENUES)
                    .withString("title","场地信息")
                    .navigation(this,Constants.venueTrasitionCode);


        } else if (v.getId() == R.id.ll_search) {


        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        pageNo++;
        getDataList(Constants.FORMID_VENUES, pageNo);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        pageNo = 1;
        getDataList(Constants.FORMID_VENUES, pageNo);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.venueTrasitionCode && resultCode == Constants.freshFormData){
            pageNo = 1;
            getDataList(Constants.FORMID_VENUES, pageNo);
        }

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }





}