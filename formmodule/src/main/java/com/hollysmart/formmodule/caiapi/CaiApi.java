package com.hollysmart.formmodule.caiapi;

import com.hollysmart.formmodule.retrofit.RetrofitUtils;

import retrofit2.Retrofit;

public class CaiApi {
    public static <T> T getApiService(Class<T> cls,String baseUrl) {
        Retrofit retrofit = RetrofitUtils.getRetrofitBuilder(baseUrl).build();
        return retrofit.create(cls);
    }
}
