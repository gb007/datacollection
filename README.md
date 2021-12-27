# datacollection使用

## 1.在需要引用此类库模块的build.gradle中引入Arouter以及此类库

````

dependencies {
    //A-Router
    implementation'com.alibaba:arouter-api:1.3.1'
    annotationProcessor'com.alibaba:arouter-compiler:1.1.4'
	implementation 'com.github.gb007:datacollection:Tag'
	}

````

## 2.在工程的根目录build.gradle中

````

buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
    dependencies {
        classpath "com.android.tools.build:gradle:7.0.3"

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

````

### 2.1 如果引用类库比较慢可在工程的build.gradle中配置阿里云镜像节点

````

buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
        maven {
            url 'https://maven.aliyun.com/repository/google/'
        }
        maven {
            url 'https://maven.aliyun.com/repository/jcenter/'
        }
    }
    dependencies {
        classpath "com.android.tools.build:gradle:7.0.3"

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

````

## 3.初始化配置信息

#### 3.1.1 Manifest中初始化配置权限以及高德地图key信息

````

    <!-- 权限申请 -->
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" /> <!-- 允许程序读取位置信息 -->
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" /><!-- 允许程序获取位置信息 -->
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" /> <!-- 允许程序读取外部内存卡 -->
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" /> <!-- 允许程序访问WiFi状态信息 -->
    <uses-permission android:name="android.permission.VIBRATE" /> <!-- 允许程序调用震动 -->
    <uses-permission android:name="android.permission.CAMERA" /> <!-- 允许程序调用摄像头 -->
    <uses-permission android:name="android.permission.INTERNET" /> <!--允许程序使用互联网  -->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" /> <!--允许程序设置内置sd卡的写权限  -->
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" /> <!-- 允许程序获取网络状态 -->
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" /> <!-- 允许程序访问WiFi网络信息 -->
    <uses-permission android:name="android.permission.READ_PHONE_STATE" /> <!-- 允许程序读写手机状态和身份-->
    
     <!-- 高德地图key -->
        <meta-data
            android:name="com.amap.api.v2.apikey"
            android:value="1f094b4047dac2af1e1ebc36eab45e7f" />
    
````

#### 3.1.2 Manifest中application中设置usesCleartextTraffic为true

````

        <application
        android:name=".TestApplication"
        android:usesCleartextTraffic="true"
        android:theme="@style/Theme.TestGitib">

````

### 3.2 Application中初始化Arouter以及FormModule

````

    @Override
    public void onCreate() {
        super.onCreate();

        //ARouter初始化
        ARouter.init(this);
        //FormModule初始化
        FormModuleInit.init(this);

    }
    
````

### 3.3 跳转表单详情页面之前，一定要初始化FormModule的配置信息

````

    //表单模块服务器地址
    public String API_SERVER_URL = "http://10.2.9.150:8885";
    //请求Token
    public static String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NDA1ODgyODUsInVzZXJuYW1lIjoiamVlY2cifQ.iGGoow0LLp3_kdeaYSgUfOgY3RyWstCEgAbfey-srYw";
    //文件上传七牛地址
    public String PIC_IP = "https://****.*****.com.cn/";
    //项目名（七牛上传文件，文件名前缀拼接项目名）
    public String PROJECT_NAME = "changyuansports";
    public String SDCARD_ROOT = "smart_sport";
    //七牛文件上传AK
    public String QINIU_ACCESSKEY = "28fPX_YqJodo-*************";
    //七牛文件上传SK
    public String QINIU_SECRETKEY = "iZZJ7B1WFJ-***********-Mj";
    //七牛文件上传scope
    public String QINIU_SCOPENAME = "atspace";
    
    private void initFormModule() {
        //表单模块服务器地址
        FormModuleInit.initFormServerUrl(API_SERVER_URL);
        //请求Token
        FormModuleInit.initToken(TOKEN);
        //文件上传至七牛服务器，七牛服务器的相关配置洗信息
        FormModuleInit.initQiNiu(PIC_IP, PROJECT_NAME, SDCARD_ROOT, QINIU_ACCESSKEY, QINIU_SECRETKEY, QINIU_SCOPENAME);
    }



````

#### 3.4.1 新建表单 跳转表单详情页面

 ````
 
                    ARouter.getInstance().build("/form/formdetail")
                    .withBoolean("isAdd",true)//是否是新增 
                    .withBoolean("isCheck",false)//查看或编辑 true代表查看false代表编辑
                    .withString("formId",FORMID_VENUES)//表单id
                    .withString("title","场地信息")//页面标题
                    .withString("fk_fd_Id",dataId)//新增数据的表单有外键时，需传此字段，否则不传
                    .navigation();
                    
   ````                 

#### 3.4.2 表单详情查看编辑 跳转表单详情页面

````

                ARouter.getInstance().build("/form/formdetail")
                        .withBoolean("isAdd",false)//是否是新增 
                        .withBoolean("isCheck",false)//查看或编辑 true代表查看false代表编辑
                        .withString("formId",Constants.FORMID_EQUIPMENT)//表单id
                        .withString("dataId",item.get("id").getAsString())//数据id
                        .withString("strDataJson",venueJson)//表单详细数据(Json形式的string)
                        .withString("title","器材信息")//页面标题
                        .navigation(activity,Constants.equipTrasitionCode);
                        
````