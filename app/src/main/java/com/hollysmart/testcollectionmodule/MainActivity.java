package com.hollysmart.testcollectionmodule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hollysmart.formmodule.base.FormModuleInit;

public class MainActivity extends AppCompatActivity {

    public String API_SERVER_URL = "http://10.2.9.150:8885";
    public static String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NDAyMjY4NDYsInVzZXJuYW1lIjoiamVlY2cifQ.kxcH9hXv1JinQazQNaYtwIQWfQZ0wcWu7e3A7vaJ1oU";
    //文件上传七牛地址
    public String PIC_IP = "https://qiniu.hollysmart.com.cn/";
    //项目名（七牛上传文件，文件名前缀拼接项目名）
    public String PROJECT_NAME = "changyuansports";
    public String SDCARD_ROOT = "smart_sport";
    //七牛文件上传AK
    public String QINIU_ACCESSKEY = "28fPX_YqJodo-uikTzYZWDv0koVKcqvx1erzEHks";
    //七牛文件上传SK
    public String QINIU_SECRETKEY = "iZZJ7B1WFJ-Ulsry6xASIX6nL2itJAC7CkWQd-Mj";
    //七牛文件上传scope
    public String QINIU_SCOPENAME = "atspace";


    /// 场馆表单id
    public final String FORMID_VENUES = "8acf27faa97b49769698eaecf916dc4a";



    private Button btn_venue;
    private Button btn_equip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_venue = (Button) findViewById(R.id.btn_venue);
        btn_equip = (Button) findViewById(R.id.btn_equip);
        initFormModule();
        btn_venue.setOnClickListener((view) -> {

//            ARouter.getInstance().build("/form/formdetail")
//                    .withBoolean("isAdd",true)
//                    .withBoolean("isCheck",false)
//                    .withString("formId",FORMID_VENUES)
//                    .withString("title","场地信息")
//                    .navigation();

            ARouter.getInstance().build("/formlist/dynamiclist").withString("token",TOKEN).navigation();


        });
        btn_equip.setOnClickListener((view) -> {
//            ARouter.getInstance().build("/form/formdetail").navigation();
        });

    }


    private void initFormModule() {
        FormModuleInit.initFormServerUrl(API_SERVER_URL);
        FormModuleInit.initToken(TOKEN);
        FormModuleInit.initQiNiu(PIC_IP, PROJECT_NAME, SDCARD_ROOT, QINIU_ACCESSKEY, QINIU_SECRETKEY, QINIU_SCOPENAME);
    }

}