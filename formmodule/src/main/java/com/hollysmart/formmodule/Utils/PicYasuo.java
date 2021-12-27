package com.hollysmart.formmodule.Utils;

import android.content.Context;

import com.hollysmart.formmodule.bean.PicBean;
import com.hollysmart.formmodule.caiapi.taskpool.INetModel;
import com.hollysmart.formmodule.caiapi.taskpool.OnNetRequestListener;
import com.hollysmart.formmodule.common.Constants;

import java.io.File;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class PicYasuo implements INetModel {

    private PicBean info;
    private String filePath;
    private Context context;
    private OnNetRequestListener onNetRequestListener;

    public PicYasuo(PicBean info, Context context, OnNetRequestListener onNetRequestListener ) {
        this.info = info;
        this.context = context;
        this.onNetRequestListener = onNetRequestListener;
    }


    @Override
    public void request(Context context) {
        String str[] = info.getFilePath().split("/");
        String picName = str[str.length-1];


        Luban.with(context)
                .load(info.getFilePath())
                .ignoreBy(100)
                .setTargetDir(Constants.SDCARD_FILE(Constants.SDCARD_PIC))
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                    }

                    @Override
                    public void onSuccess(File file) {
                        // TODO 压缩成功后调用，返回压缩后的图片文件
                        info.setFilePath(file.getPath());
                        onNetRequestListener.OnNext();
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO 当压缩过程出现问题时调用
                        onNetRequestListener.OnNext();
                    }
                }).launch();

    }


}
