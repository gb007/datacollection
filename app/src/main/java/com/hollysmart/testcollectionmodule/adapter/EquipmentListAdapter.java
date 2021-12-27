package com.hollysmart.testcollectionmodule.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.d.lib.slidelayout.SlideLayout;
import com.d.lib.slidelayout.SlideManager;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.CommonHolder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hollysmart.testcollectionmodule.R;
import com.hollysmart.testcollectionmodule.bean.event.EB_Refresh_Equipment;
import com.hollysmart.testcollectionmodule.common.Constants;
import com.hollysmart.testcollectionmodule.retrofit.BasicResponse;
import com.hollysmart.testcollectionmodule.retrofit.DefaultObserver;
import com.hollysmart.testcollectionmodule.retrofit.RetrofitHelper;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.view.listener.OnButtonClickListener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class EquipmentListAdapter extends CommonAdapter<JsonObject> {


    private Context context;
    private List<JsonObject> equipmentBeanList;
    private SlideManager manager;

    public EquipmentListAdapter(Context context, List<JsonObject> equipmentBeanList) {
        super(context, equipmentBeanList, R.layout.form_module_item_equipment_list);
        this.context = context;
        this.equipmentBeanList = equipmentBeanList;
        manager = new SlideManager();
    }


    public int getItemCount() {
        if (equipmentBeanList.size() == 0) {
            return 0;
        } else {
            return equipmentBeanList.size();
        }
    }


    @Override
    public void convert(int position, CommonHolder holder, JsonObject item) {


        holder.setText(R.id.tv_number, String.format("%s", item.get("number").getAsString()));
//        holder.setText( R.id.tv_time, item.getTime().substring(0,10));
        holder.setText( R.id.tv_name, item.get("name").getAsString());
        final SlideLayout slSlide = holder.getView( R.id.sl_slide );

        holder.getView( R.id.sl_slide ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mContext, FormDetailActivity.class);
//                Gson gson = new Gson();
//                String venueJson = gson.toJson(item);
//                intent.putExtra("isAdd",false);
//                intent.putExtra("isCheck", false);
//                intent.putExtra("formId", Constants.FORMID_EQUIPMENT);
//                intent.putExtra("dataId", item.get("id").getAsString());
//                intent.putExtra("dataJson", venueJson);
//                intent.putExtra("title", "器材信息");
//                Activity activity = (Activity) context;
//                activity.startActivityForResult(intent, 4);


                Activity activity = (Activity) context;
                Gson gson = new Gson();
                String venueJson = gson.toJson(item);
                ARouter.getInstance().build("/form/formdetail")
                        .withBoolean("isAdd",false)
                        .withBoolean("isCheck",false)
                        .withString("formId",Constants.FORMID_EQUIPMENT)
                        .withString("dataId",item.get("id").getAsString())
                        .withString("strDataJson",venueJson)
                        .withString("title","器材信息")
                        .navigation(activity,Constants.equipTrasitionCode);


            }
        } );


        holder.getView( R.id.tv_check ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slSlide.close();
//                Intent intent = new Intent(mContext, FormDetailActivity.class);
//                Gson gson = new Gson();
//                String venueJson = gson.toJson(item);
//                intent.putExtra("isAdd",false);
//                intent.putExtra("isCheck", false);
//                intent.putExtra("formId", Constants.FORMID_EQUIPMENT);
//                intent.putExtra("dataId", item.get("id").getAsString());
//                intent.putExtra("dataJson", venueJson);
//                intent.putExtra("title", "器材信息");
//                Activity activity = (Activity) context;
//                activity.startActivityForResult(intent, 4);


                Activity activity = (Activity) context;
                Gson gson = new Gson();
                String venueJson = gson.toJson(item);
                ARouter.getInstance().build("/form/formdetail")
                        .withBoolean("isAdd",false)
                        .withBoolean("isCheck",false)
                        .withString("formId",Constants.FORMID_EQUIPMENT)
                        .withString("dataId",item.get("id").getAsString())
                        .withString("strDataJson",venueJson)
                        .withString("title","器材信息")
                        .navigation(activity,Constants.equipTrasitionCode);

            }
        } );

        holder.getView( R.id.tv_delete ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slSlide.close();
                showDelDialog(Constants.FORMID_EQUIPMENT,item.get("id").getAsString());
            }
        } );

        slSlide.setOnStateChangeListener( new SlideLayout.OnStateChangeListener() {
            @Override
            public void onChange(SlideLayout layout, boolean isOpen) {
//                item.isOpen = isOpen;
                manager.onChange( layout, isOpen );
            }

            @Override
            public boolean closeAll(SlideLayout layout) {
                return manager.closeAll( layout );
            }
        } );

    }

    public long getItemId(int arg0) {
        return 0;
    }

    /**
     * 弹删除提示框
     * @param formId
     * @param dataId
     */
    private  void showDelDialog(String formId, String dataId) {
        FragmentActivity activity = (FragmentActivity) context;
        new CircleDialog.Builder().setTitle("删除器材")
                .setText("确定要删除此器材吗?")//内容
                .setPositive("删除", new OnButtonClickListener() {
                    @Override
                    public boolean onClick(View v) {
                        postDel(formId, dataId);
                        return true;
                    }
                })
                .setNegative("取消", new OnButtonClickListener() {
                    @Override
                    public boolean onClick(View v) {
                        return true;
                    }
                }).show(activity.getSupportFragmentManager());
    }

    private void postDel(String formId, String dataId) {
        RetrofitHelper.getApiService().delFormData(formId, dataId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .safeSubscribe(new DefaultObserver<BasicResponse>() {
                    @Override
                    public void onSuccess(BasicResponse response) {

                        if (response.getCode() == 200) {
                            EventBus.getDefault().post(new EB_Refresh_Equipment());
                            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
