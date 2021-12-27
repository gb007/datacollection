package com.hollysmart.testcollectionmodule.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
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
import com.hollysmart.testcollectionmodule.EquipmentListActivity;
import com.hollysmart.testcollectionmodule.bean.event.EB_Refresh_Venue;
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

public class VenueListAdapter extends CommonAdapter<JsonObject> {


    private Context context;
    private List<JsonObject> venueBeanList;
    private SlideManager manager;

    public VenueListAdapter(Context context, List<JsonObject> venueBeanList) {
        super(context, venueBeanList, R.layout.form_module_item_veneu_list);
        this.context = context;
        this.venueBeanList = venueBeanList;
        manager = new SlideManager();
    }


    public int getItemCount() {
        if (venueBeanList.size() == 0) {
            return 0;
        } else {
            return venueBeanList.size();
        }
    }


    @Override
    public void convert(int position, CommonHolder holder, JsonObject item) {

//        if (ischeck) {
//            holder.getView( R.id.tv_delete ).setVisibility( View.GONE );
//            holder.setText( R.id.tv_check, "查看" );
//        }


//        holder.setText(R.id.tv_count, String.format("器材数量: %d", item.getChild_count()));

        if (null != item.get("time")) {
            String time = item.get("time").getAsString();
            if (!TextUtils.isEmpty(time)) {
                holder.setText(R.id.tv_time, item.get("time").getAsString().substring(0, 10));
            } else {
                holder.setText(R.id.tv_time, "");
            }
        } else {
            holder.setText(R.id.tv_time, "");
        }
        holder.setText(R.id.tv_jingquName, item.get("name").getAsString());
        final SlideLayout slSlide = holder.getView(R.id.sl_slide);

        holder.getView(R.id.sl_slide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, EquipmentListActivity.class);
//                intent.putExtra("name", item.get("name").getAsString());
//                intent.putExtra("dataId", item.get("id").getAsString());
//                Activity activity = (Activity) context;
//                activity.startActivity(intent);
                ARouter.getInstance().build("/formlist/equipmentlist")
                        .withString("name",item.get("name").getAsString())
                        .withString("dataId",item.get("id").getAsString())
                        .navigation();
            }
        });


        holder.getView(R.id.tv_check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slSlide.close();
//                Intent intent = new Intent(mContext, FormDetailActivity.class);
//                Gson gson = new Gson();
//                String venueJson = gson.toJson(item);
//                intent.putExtra("isAdd", false);
//                intent.putExtra("isCheck", false);
//                intent.putExtra("formId", Constants.FORMID_VENUES);
//                intent.putExtra("dataId", item.get("id").getAsString());
//                intent.putExtra("dataJson", venueJson);
//                intent.putExtra("title", "场地信息");
//                Activity activity = (Activity) context;
//                activity.startActivityForResult(intent, 4);

                Activity activity = (Activity) context;
                Gson gson = new Gson();
                String venueJson = gson.toJson(item);
                ARouter.getInstance().build("/form/formdetail")
                        .withBoolean("isAdd",false)
                        .withBoolean("isCheck",false)
                        .withString("formId",Constants.FORMID_VENUES)
                        .withString("dataId",item.get("id").getAsString())
                        .withString("strDataJson",venueJson)
                        .withString("title","场地信息")
                        .navigation(activity,Constants.venueTrasitionCode);

            }
        });

        holder.getView(R.id.tv_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slSlide.close();
//                delVenue(Constants.FORMID_VENUES, item.get("id").getAsString());
                showDelDialog(Constants.FORMID_VENUES, item.get("id").getAsString());
            }
        });


        slSlide.setOnStateChangeListener(new SlideLayout.OnStateChangeListener() {
            @Override
            public void onChange(SlideLayout layout, boolean isOpen) {
//                item.isOpen = isOpen;
                manager.onChange(layout, isOpen);
            }

            @Override
            public boolean closeAll(SlideLayout layout) {
                return manager.closeAll(layout);
            }
        });


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
        new CircleDialog.Builder().setTitle("删除场地")
                .setText("确定要删除此场地吗?")//内容
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
                            EventBus.getDefault().post(new EB_Refresh_Venue());
                            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
