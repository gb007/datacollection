package com.hollysmart.testcollectionmodule.caiapi.taskpool;

import com.hollysmart.testcollectionmodule.bean.PicBean;

/**
 * Created by cai on 16/6/6.
 */
public interface OnNetRequestListener {
    void onFinish();
    void OnNext();
    void OnResult(boolean isOk, String msg, Object object);
    void UploadPicError();
    void UploadPicSuccess(String formFieldName, PicBean bean);
}
