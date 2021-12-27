package com.hollysmart.formmodule.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hollysmart.formmodule.R;
import com.hollysmart.formmodule.Utils.LogUtils;
import com.hollysmart.formmodule.activity.AddPicActivity;
import com.hollysmart.formmodule.activity.BigPicActivity;
import com.hollysmart.formmodule.bean.PicBean;
import com.hollysmart.formmodule.common.Constants;
import com.hollysmart.formmodule.view.linearlayoutforlistview.LinearLayoutBaseAdapter;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ItemPicAdater extends LinearLayoutBaseAdapter {
    private Context context;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private int MAXNUM = 9;

    List<PicBean> pics;
    private boolean isCheck = false; //是否是查看，true查看，不能编辑；
    private JsonObject jsonObject;
    private String dbFieldName;

    public ItemPicAdater(Context context, JsonObject jsonObject, String dbFieldName, List<PicBean> pics, boolean isCheck) {
        super(context, pics);
        this.context = context;
        this.pics = pics;
        this.isCheck = isCheck;
        this.jsonObject = jsonObject;
        this.dbFieldName = dbFieldName;

        if (this.pics == null || this.pics.size() == 0) {
            this.pics = new ArrayList<>();
        }

    }

    public void setMaxSize(int MAXNUM) {
        this.MAXNUM = MAXNUM;
    }

    @Override
    public int getCount() {
        if (isCheck) {
            return pics.size();
        } else if (pics.size() == MAXNUM) {
            return pics.size();
        } else {
            return pics.size() + 1;
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public View getView(final int position) {
        View convertView = View.inflate(context, R.layout.form_module_item_pic_list, null);
        ImageView imageView = convertView.findViewById(R.id.photo);
        ImageView iv_del = convertView.findViewById(R.id.iv_del);
        if (isCheck) {
            iv_del.setVisibility(View.GONE);
        } else {
            iv_del.setVisibility(View.VISIBLE);
        }

        //判断  最后一个Item是加号
        if (!isCheck && pics.size() != MAXNUM && position == pics.size()) {
            iv_del.setVisibility(View.GONE);
            Glide.with(context)
                    .load(R.drawable.form_module_upload)
                    .centerCrop().into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Activity activity = (Activity) context;
                        activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_CONTACTS);
                    } else {
                        Gson gson = new Gson();
                        String dataJson = gson.toJson(jsonObject);

                        Activity activity = (Activity) context;
                        Intent intent = new Intent(context, AddPicActivity.class);
                        intent.putExtra("num", MAXNUM - pics.size());
                        intent.putExtra("dataJson", dataJson);
                        intent.putExtra("dbFieldName", dbFieldName);
                        activity.startActivityForResult(intent, 1);
                    }
                }
            });
        } else {
            final PicBean jdPicInfo = pics.get(position);
            if (!TextUtils.isEmpty(jdPicInfo.getImageUrl())) {
                Glide.with(context)
                        .load(Constants.PIC_IP + jdPicInfo.getImageUrl())
                        .centerCrop().into(imageView);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, BigPicActivity.class);
                        intent.putExtra("pics", (Serializable) pics);
                        intent.putExtra("index", position);
                        context.startActivity(intent);
                    }
                });

            } else {
                Glide.with(context)
                        .load(new File(jdPicInfo.getFilePath()))
                        .centerCrop().into(imageView);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LogUtils.d("点击了大图");
                        Intent intent = new Intent(context, BigPicActivity.class);
                        intent.putExtra("pics", (Serializable) pics);
                        intent.putExtra("index", position);
                        context.startActivity(intent);
                    }
                });

            }
        }
        iv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("确定要删除该图片吗？");
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pics.remove(position);
                        resetJsonObjectImages(pics, jsonObject, dbFieldName);
                        notifyDataSetChanged();
                    }
                });
                builder.setPositiveButton("取消", null);
                builder.create();
                builder.show();


            }
        });
        return convertView;
    }


    void resetJsonObjectImages(List<PicBean> pics, JsonObject jsonObject, String dbFieldName) {

        String images = "";
        if (pics.size() >= 1) {
            for (PicBean picBean : pics) {
                String imageUrl = picBean.getImageUrl();
                String imagePath = picBean.getFilePath();
                String iamgeName = picBean.getFilename();
                if(!TextUtils.isEmpty(imageUrl)){//网络
                    images += (images + imageUrl + ",");
                }
                if(!TextUtils.isEmpty(imagePath)){//本地图片
                    if(!TextUtils.isEmpty(iamgeName)){//本地图片使用名字，名字为前缀+地址；前缀用来展示图片时判断是本地还是网络
                        images += (images + iamgeName + ",");
                    }
                }
            }
            if(images.length() > 1){
                images = images.substring(0,images.length() - 1);
            }
            jsonObject.addProperty(dbFieldName,images);
        } else {
            jsonObject.addProperty(dbFieldName,"");
        }
    }
}
