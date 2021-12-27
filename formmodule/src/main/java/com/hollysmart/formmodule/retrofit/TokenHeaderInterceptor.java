package com.hollysmart.formmodule.retrofit;

import com.hollysmart.formmodule.common.Constants;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

//在请求头里添加token的拦截器处理
    public  class TokenHeaderInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
//            String token = SpUtils.getToken(); //SpUtils是SharedPreferences的工具类，自行实现
              String token = Constants.TOKEN;
            if (token.isEmpty()) {
                Request originalRequest = chain.request();
                return chain.proceed(originalRequest);
            }else {
                Request originalRequest = chain.request();
                //key的话以后台给的为准，我这边是叫token
                Request updateRequest = originalRequest.newBuilder().header("X-Access-Token", token).build();
                return chain.proceed(updateRequest);
            }
        }
    }
