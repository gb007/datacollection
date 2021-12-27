package com.hollysmart.testcollectionmodule;

import android.content.Intent;
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
import com.d.lib.xrv.LRecyclerView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hjq.toast.ToastUtils;
import com.hollysmart.testcollectionmodule.Utils.LogUtils;
import com.hollysmart.testcollectionmodule.adapter.EquipmentListAdapter;
import com.hollysmart.testcollectionmodule.CaiBaseActivity;
import com.hollysmart.testcollectionmodule.bean.EquipmentFormData;
import com.hollysmart.testcollectionmodule.bean.event.EB_Refresh_Equipment;
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
 * 器材列表页面
 */
@Route(path = "/formlist/equipmentlist")
public class EquipmentListActivity extends CaiBaseActivity implements OnRefreshListener, OnLoadMoreListener {

    private ImageView ib_back;
    private TextView tv_title;
    private SmartRefreshLayout refreshLayout;
    private LRecyclerView lv_equipmentList;
    private RelativeLayout rl_bottom;
    private LinearLayout lay_fragment_ProdutEmpty;
    private int pageNo = 1;
    private List<JsonObject> equipmentBeanList;
    private EquipmentListAdapter equipmentListAdapter;

    //场馆Id
    @Autowired
    public String dataId;

    //场馆名称
    @Autowired
    public String name;

    @Override
    public int layoutResID() {
        return R.layout.form_module_activity_equipment_list;
    }

    @Override
    public boolean setTranslucent() {
        return true;
    }

    @Override
    public void findView() {
        ib_back = (ImageView) findViewById(R.id.ib_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        refreshLayout = (SmartRefreshLayout) findViewById(R.id.smart_refresh);
        lv_equipmentList = (LRecyclerView) findViewById(R.id.lv_treeList);
        rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);
        lay_fragment_ProdutEmpty = (LinearLayout) findViewById(R.id.lay_fragment_ProdutEmpty);

        ib_back.setOnClickListener(this);
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
        lv_equipmentList.setLayoutManager(layoutManager);
    }

    @Override
    public void init() {
        ARouter.getInstance().inject(this);
        EventBus.getDefault().register(this);
//        String venueName = getIntent().getStringExtra("name");
//        dataId = getIntent().getStringExtra("dataId");
        tv_title.setText(name + "器材采集");

        equipmentBeanList = new ArrayList<>();
        equipmentListAdapter = new EquipmentListAdapter(this, equipmentBeanList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        lv_equipmentList.setLayoutManager(layoutManager);
        lv_equipmentList.setAdapter(equipmentListAdapter);
        showLoadingDialog();
        getDataList(Constants.FORMID_EQUIPMENT, pageNo);
    }

    /**
     * Get请求 根据表单Id获取表单数据列表
     */
    public void getDataList(String formId, int pageNo) {

        Map<String, String> parasMap = new HashMap<>();
        parasMap.put("fd_fk", dataId);
        parasMap.put("column", "id");
        parasMap.put("order", "desc");
        parasMap.put("superQueryMatchType", "and");
        parasMap.put("pageNo", pageNo + "");
        parasMap.put("pageSize", Constants.PAGE_SIZE + "");

        RetrofitHelper.getApiService()
                .getEquipmentFormData(formId, parasMap)
                .compose(this.<BasicResponse<EquipmentFormData>>bindToLifecycle())
//                .compose(ProgressUtils.<BasicResponse<FormData>>applyProgressBar(this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<BasicResponse<EquipmentFormData>>() {
                    @Override
                    public void onSuccess(BasicResponse<EquipmentFormData> response) {
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
                                        equipmentBeanList.clear();
                                        equipmentBeanList.addAll(jsonList);
                                        refreshLayout.finishRefresh();
                                        lay_fragment_ProdutEmpty.setVisibility(View.GONE);
                                        lv_equipmentList.setVisibility(View.VISIBLE);
                                        refreshLayout.finishRefresh();
                                    } else {//上拉加载更多
                                        equipmentBeanList.addAll(jsonList);
                                        refreshLayout.finishLoadMore();
                                    }

                                    if (datas.size() < Constants.PAGE_SIZE) {//无更多数据（数据已全部加载）
                                        refreshLayout.finishLoadMoreWithNoMoreData();
                                    }
                                    equipmentListAdapter.notifyDataSetChanged();
                                } else {
                                    if (null == datas) {//后台未返回数据对象
                                        setErrorRequestStatus();
                                    } else {//后台返回数据为空集合
                                        if (pageNo == 1) {//
                                            refreshLayout.finishRefresh();
                                            lay_fragment_ProdutEmpty.setVisibility(View.VISIBLE);
                                            lv_equipmentList.setVisibility(View.GONE);
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
    public void receiveEvent(EB_Refresh_Equipment model) {
        pageNo = 1;
        showLoadingDialog();
        getDataList(Constants.FORMID_EQUIPMENT, pageNo);
    }

    /**
     * 请求异常或错误
     */
    void setErrorRequestStatus() {
        if (pageNo == 1) {
            refreshLayout.finishRefresh();
            lay_fragment_ProdutEmpty.setVisibility(View.VISIBLE);
            lv_equipmentList.setVisibility(View.GONE);
        } else {
            ToastUtils.show("获取数据失败");
            refreshLayout.finishLoadMore();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ib_back) {
            finish();
        } else if (v.getId() == R.id.rl_bottom) {

//            Intent intent = new Intent(mContext, FormDetailActivity.class);
//            intent.putExtra("isAdd", true);
//            intent.putExtra("isCheck", false);
//            intent.putExtra("title", "器材信息");
//            intent.putExtra("formId", Constants.FORMID_EQUIPMENT);
//            intent.putExtra("fk_fd_Id", dataId);
//            startActivityForResult(intent, 4);

            ARouter.getInstance().build("/form/formdetail")
                    .withBoolean("isAdd",true)
                    .withBoolean("isCheck",false)
                    .withString("title","器材信息")
                    .withString("formId",Constants.FORMID_EQUIPMENT)
                    .withString("fk_fd_Id",dataId)
                    .navigation(this,Constants.equipTrasitionCode);



        } else if (v.getId() == R.id.ll_search) {

        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        pageNo = 1;
        getDataList(Constants.FORMID_EQUIPMENT, pageNo);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        pageNo++;
        getDataList(Constants.FORMID_EQUIPMENT, pageNo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.equipTrasitionCode && resultCode == Constants.freshFormData){
            pageNo = 1;
            getDataList(Constants.FORMID_EQUIPMENT, pageNo);
        }

    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}