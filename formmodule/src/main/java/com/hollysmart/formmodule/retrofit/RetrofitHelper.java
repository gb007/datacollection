package com.hollysmart.formmodule.retrofit;

import com.hollysmart.formmodule.caiapi.CaiApi;
import com.hollysmart.formmodule.caiapi.CaiApiService;
import com.hollysmart.formmodule.common.Constants;

public class RetrofitHelper {
    private static CaiApiService mIdeaApiService;

    public static CaiApiService getApiService(){
        return mIdeaApiService;
    }
    static {
        mIdeaApiService= CaiApi.getApiService(CaiApiService.class, Constants.API_SERVER_URL);
    }
}
