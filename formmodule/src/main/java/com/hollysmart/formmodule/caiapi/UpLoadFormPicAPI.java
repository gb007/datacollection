package com.hollysmart.formmodule.caiapi;


import android.content.Context;
import android.os.Environment;

import com.hollysmart.formmodule.Utils.CCM_Bitmap;
import com.hollysmart.formmodule.Utils.LogUtils;
import com.hollysmart.formmodule.Utils.QiNiuUploadUtil;
import com.hollysmart.formmodule.Utils.Sha1Util;
import com.hollysmart.formmodule.bean.PicBean;
import com.hollysmart.formmodule.caiapi.taskpool.INetModel;
import com.hollysmart.formmodule.caiapi.taskpool.OnNetRequestListener;
import com.hollysmart.formmodule.common.Constants;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.utils.UrlSafeBase64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 七牛文件上传
 */
public class UpLoadFormPicAPI implements INetModel {

    private final String TAG = "UpLoadFormPicAPI";
    private String picPath;
    private String formFieldName;
    private PicBean bean;
    private OnNetRequestListener onNetRequestListener;

    public UpLoadFormPicAPI(String formFieldName, PicBean bean, OnNetRequestListener onNetRequestListener) {
        this.picPath = bean.getFilePath();
        this.bean = bean;
        this.formFieldName = formFieldName;
        this.onNetRequestListener = onNetRequestListener;
    }

    @Override
    public void request(Context context) {
        try {
            initFile(context);//初始化压缩文件夹
            String outPath = context.getCacheDir().getPath() + "/" + System.currentTimeMillis() + ".jpg";
            LogUtils.d("压缩后的地址：" + outPath);
            //压缩文件
            new CCM_Bitmap().ratioAndGenThumb(picPath, outPath, 1080, 1080, false);
            //初始化七牛文件上传UploadManager
            UploadManager uploadManager = QiNiuUploadUtil.getInstance();
            //生成七牛Token
            String _uploadToken = getQiNiuToken();
            //生成上传文件key（可以理解为七牛文件存储路径）
            String key = Constants.PROJECT_NAME + "_android_" + System.currentTimeMillis() + ".jpg";
            LogUtils.d("七牛key：" + key);
            uploadManager.put(outPath, key, _uploadToken,
                    new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo info, JSONObject res) {
                            //res 包含 hash、key 等信息，具体字段取决于上传策略的设置
                            if (info.isOK()) {//上传成功
                                try {
                                    String showUrl = res.getString("key");
                                    bean.setImageUrl(showUrl);
                                    onNetRequestListener.UploadPicSuccess(formFieldName, bean);
                                    onNetRequestListener.OnNext();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    onNetRequestListener.UploadPicError();
                                }
                            } else {
                                onNetRequestListener.UploadPicError();
                            }
                        }
                    }, null);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            onNetRequestListener.UploadPicError();
        }

    }

    /**
     * 生成七牛Token
     *
     * @return
     */
    private String getQiNiuToken() {
        String _uploadToken = "";
        try {
            JSONObject _json = new JSONObject();
            long _dataline = System.currentTimeMillis() / 1000 + 3600;
            _json.put("deadline", _dataline);// 有效时间为一个小时
            _json.put("scope", Constants.QINIU_SCOPENAME);
            String _encodedPutPolicy = UrlSafeBase64.encodeToString(_json
                    .toString().getBytes());
            byte[] _sign = Sha1Util.HmacSHA1Encrypt(_encodedPutPolicy, Constants.QINIU_SECRETKEY);
            String _encodedSign = UrlSafeBase64.encodeToString(_sign);
            _uploadToken = Constants.QINIU_ACCESSKEY + ':' + _encodedSign + ':'
                    + _encodedPutPolicy;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return _uploadToken;
    }

    /**
     * 初始化压缩文件夹
     *
     * @param context
     */
    private void initFile(Context context) {
        String cacheDir;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            cacheDir = context.getExternalCacheDir().getPath();
            LogUtils.d(TAG, "have SD");
        } else {
            cacheDir = context.getCacheDir().getPath();
            LogUtils.d(TAG, " not have SD");
        }
        LogUtils.d(TAG, cacheDir);
        File file = new File(cacheDir);
        if (!file.exists()) {
            LogUtils.d(TAG, "file no exists");
            file.mkdirs();
        }
    }
}
