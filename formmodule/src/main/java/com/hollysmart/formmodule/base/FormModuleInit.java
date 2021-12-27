package com.hollysmart.formmodule.base;

import android.app.Application;
import com.blankj.utilcode.util.Utils;
import com.hjq.toast.ToastUtils;
import com.hollysmart.formmodule.common.Constants;

/**
 * 采集模块初始化
 *
 * 注意：采集模块初始化类中的方法都要初始化，按照相应的参数传值
 *
 *
 * @author gbin
 */
public class FormModuleInit {


    /**
     * 初始化
     * @param app
     */
    public static void init(Application app){
        Utils.init(app);
        ToastUtils.init(app);
    }


    /**
     * @param API_SERVER_URL 采集模块服务器地址
     */
    public static void initFormServerUrl(String API_SERVER_URL) {
        Constants.API_SERVER_URL = API_SERVER_URL;
    }


    /**
     * @param TOKEN 采集模块初始化请求头Token
     */
    public static void initToken(String TOKEN) {
        Constants.TOKEN = TOKEN;
    }


    /**
     * @param PIC_IP          文件上传七牛地址
     * @param PROJECT_NAME    项目名（七牛上传文件，文件名前缀拼接项目名）
     * @param SDCARD_ROOT     拍摄照片根目录名称
     * @param QINIU_ACCESSKEY 七牛文件上传AK
     * @param QINIU_SECRETKEY 七牛文件上传SK
     * @param QINIU_SCOPENAME 七牛文件上传scope
     */
    public static void initQiNiu(String PIC_IP, String PROJECT_NAME, String SDCARD_ROOT, String QINIU_ACCESSKEY, String QINIU_SECRETKEY, String QINIU_SCOPENAME) {

        Constants.PIC_IP = PIC_IP;
        Constants.PROJECT_NAME = PROJECT_NAME;
        Constants.SDCARD_ROOT = SDCARD_ROOT;
        Constants.QINIU_ACCESSKEY = QINIU_ACCESSKEY;
        Constants.QINIU_SECRETKEY = QINIU_SECRETKEY;
        Constants.QINIU_SCOPENAME = QINIU_SCOPENAME;

    }


}
