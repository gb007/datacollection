package com.hollysmart.formmodule.common;

import android.os.Environment;

public class Constants {

    public static final long DEFAULT_TIMEOUT = 60000;      //默认的超时时间
    public static final long EXITTIME = 200; //默认两次点击事件间隔


    //  public static String API_SERVER_URL = "http://10.2.9.150:8885";
    public static String API_SERVER_URL = "";


    /// 场馆表单id
    public static final String FORMID_VENUES = "8acf27faa97b49769698eaecf916dc4a";
    /// 器材表单id
    public static final String FORMID_EQUIPMENT = "dc39e9d987b74d6d918c3fb9da068626";
    //采集模块请求接口Token
//    eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2Mzc4MTk0NjEsInVzZXJuYW1lIjoiamVlY2cifQ.0hXrQYMuIQQHlpFwTqIuyrQyMj3TxSiUcoFRTxj3fQI
    public static String TOKEN = "";

    //图片选择生成图片名称（增加前缀，用于区分本地选择和网络获取）
    public static final String LOCAL_CHECKED_IMAGE_FLAG = "local_checked_image_android_";
    //Activity加载方式
    public final static String ANIM_TYPE = "animType";
    public final static int ANIM_TYPE_SHANG = 1; //上进 上出
    public final static int ANIM_TYPE_XIA = 2;   //下进 下出
    public final static int ANIM_TYPE_LEFT = 3;  //左进 左出
    public final static int ANIM_TYPE_RIGHT = 4; //右进 右出
    public final static int ANIM_TYPE_SUOFANG = 5; //放大进 缩小出
    public final static int ANIM_TYPE_LONG_LEFT = 6; //左进 左出 350毫秒
    public final static int ANIM_TYPE_LONG_RIGHT = 7; //右进 右出 350毫秒
    public final static int ANIM_TYPE_ALPHA = 8; //右进 右出 350毫秒
    //list数据请求一次条数
    public final static int PAGE_SIZE = 20; //页容量，不传默认20，非必填
    //表单控件类型
    public final static int FORM_FILED_TYPE_TEXT = 1;//单行文本
    public final static int FORM_FILED_TYPE_PASSWORD = 2;//单行密码输入框
    public final static int FORM_FILED_TYPE_LIST = 3;//下拉单选
    public final static int FORM_FILED_TYPE_RADIO = 4;//下拉单选
    public final static int FORM_FILED_TYPE_CHECK_BOX = 5;//单行选择
    public final static int FORM_FILED_TYPE_SWITACH = 6;//单行开关切换
    public final static int FORM_FILED_TYPE_DATE = 7;//日期选择
    public final static int FORM_FILED_TYPE_DATE_TIME = 8;//日期时间选择
    public final static int FORM_FILED_TYPE_TIME = 9;//时间选择
    public final static int FORM_FILED_TYPE_IMAGE = 10;//图片选择
    public final static int FORM_FILED_TYPE_TEXTAREA = 11;//文本域
    public final static int FORM_FILED_TYPE_LIST_MULTI = 12;//多选
    public final static int FORM_FILED_TYPE_DOT_MAP = 13;//地图定位
    public final static int FORM_FILED_TYPE_INPUT_DISABLED = 14;//不可编辑输入框
    public final static int FORM_FILED_TYPE_LINK_DOWN = 15;//多级联动

    //Activity REQUEST_CODE
    public static final int REQUEST_CODE_IMAGE = 100;//选取图片
    public static final int REQUEST_CODE_MAP = 6;//地图选取坐标
    public static final int REQUEST_CODE_ADD_IMAGE = 1;//选取图片
    public static final int REQUEST_CODE_CUT_IMAGE = 3;//裁剪图片
    //Activity RESULT_CODE
    public static final int RESULT_CODE_MAP = 2;//地图选取坐标
    public static final int RESULT_CODE_ADD_IMAGE = 2;//选取图片
    //地图默认位置
    public static final double DEFAULT_LONGITUDE = 35.201548;//经度////35.201548
    public static final double DEFAULT_LATITUDE = 114.668936;//纬度///114.668936

    //文件上传七牛地址
//  public static   String PIC_IP = "https://qiniu.hollysmart.com.cn/";
    public static   String PIC_IP = "";


    //项目名（七牛上传文件，文件名前缀拼接项目名）
//    public static   String PROJECT_NAME = "changyuansports";
    public static   String PROJECT_NAME = "";
//    public static   String SDCARD_ROOT = "smart_sport";
    public static   String SDCARD_ROOT = "";

    //七牛拍照文件中间路径
    public static final String SDCARD_PIC = "pic";
    public static final String SDCARD_DIR = Environment.getExternalStorageDirectory().toString() + "/" + SDCARD_ROOT + "/";
    public static String SDCARD_FILE(String FILENAME) {
        return SDCARD_DIR + FILENAME + "/";
    }


    //七牛文件上传AK
//    public static   String QINIU_ACCESSKEY = "28fPX_YqJodo-uikTzYZWDv0koVKcqvx1erzEHks";
    public static   String QINIU_ACCESSKEY = "";
    //七牛文件上传SK
//    public static   String QINIU_SECRETKEY = "iZZJ7B1WFJ-Ulsry6xASIX6nL2itJAC7CkWQd-Mj";
    public static   String QINIU_SECRETKEY = "";
    //七牛文件上传scope
//    public static   String QINIU_SCOPENAME = "atspace";
    public static   String QINIU_SCOPENAME = "";


}
