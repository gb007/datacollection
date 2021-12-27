package com.hollysmart.testcollectionmodule.caiapi;

import com.hollysmart.testcollectionmodule.retrofit.RetrofitUtils;

import retrofit2.Retrofit;

public class CaiApi {
    public static <T> T getApiService(Class<T> cls,String baseUrl) {
        Retrofit retrofit = RetrofitUtils.getRetrofitBuilder(baseUrl).build();
        return retrofit.create(cls);
    }
}
