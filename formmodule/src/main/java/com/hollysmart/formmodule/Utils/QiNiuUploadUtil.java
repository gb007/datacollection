package com.hollysmart.formmodule.Utils;

import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UploadManager;

public class QiNiuUploadUtil {

    private static UploadManager uploadManager;

    public static UploadManager getInstance(){
        if(null == uploadManager){
            Configuration config = new Configuration.Builder()
                    .connectTimeout(90)              // 链接超时。默认90秒
                    .useHttps(true)                  // 是否使用https上传域名
                    .useConcurrentResumeUpload(true) // 使用并发上传，使用并发上传时，除最后一块大小不定外，其余每个块大小固定为4M，
                    .concurrentTaskCount(3)          // 并发上传线程数量为3
                    .responseTimeout(90)             // 服务器响应超时。默认90秒
                    .build();
            // 重用uploadManager。一般地，只需要创建一个uploadManager对象
             uploadManager = new UploadManager(config);
        }
        return uploadManager;
    }

}
