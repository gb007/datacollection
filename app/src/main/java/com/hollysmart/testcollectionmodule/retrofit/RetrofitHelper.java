package com.hollysmart.testcollectionmodule.retrofit;

import com.hollysmart.testcollectionmodule.caiapi.CaiApi;
import com.hollysmart.testcollectionmodule.caiapi.CaiApiService;
import com.hollysmart.testcollectionmodule.common.Constants;

public class RetrofitHelper {
    private static CaiApiService mIdeaApiService;

    public static CaiApiService getApiService(){
        return mIdeaApiService;
    }
    static {
        mIdeaApiService= CaiApi.getApiService(CaiApiService.class, Constants.API_SERVER_URL);
    }
}
