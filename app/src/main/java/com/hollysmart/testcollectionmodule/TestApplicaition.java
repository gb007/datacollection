package com.hollysmart.testcollectionmodule;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hollysmart.formmodule.base.FormModuleInit;

public class TestApplicaition extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //ARouter初始化
        ARouter.init(this);
        //FormModule初始化
        FormModuleInit.init(this);
    }
}
