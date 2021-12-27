package com.hollysmart.formmodule.caiapi;



import com.hollysmart.formmodule.bean.FormData;
import com.hollysmart.formmodule.bean.FormDictionaryBean;
import com.hollysmart.formmodule.bean.FormFiledBean;
import com.hollysmart.formmodule.bean.LinkDownDictBean;
import com.hollysmart.formmodule.bean.LoginInfoBean;
import com.hollysmart.formmodule.retrofit.BasicResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface CaiApiService {

    /**
     * 获取表单
     *
     * @param formID
     * @return
     */
    @GET("/online/cgform/field/listByHeadId")
    Observable<BasicResponse<List<FormFiledBean>>> getForm(@Query("headId") String formID);

    /**
     * 获取场所列表数据
     *
     * @param formId
     * @param options
     * @return
     */
    @GET("/online/cgform/api/getData/{formId}")
    Observable<BasicResponse<FormData>> getFormData(@Path("formId") String formId, @QueryMap Map<String, String> options);


    /**
     * 新增采集数据
     *
     * @param formId
     * @return
     */
    @Headers("Content-Type: application/json")
    @POST("/online/cgform/api/form/{formId}")
    Observable<BasicResponse> postFormData(@Path("formId") String formId, @Body RequestBody data);


    /**
     * 修改采集数据
     *
     * @param formId //     * @param dataId
     * @return
     */
    @Headers("Content-Type: application/json")
    @PUT("/online/cgform/api/form/{formId}")
    Observable<BasicResponse> putFormData(@Path("formId") String formId, @Body RequestBody data);


    /**
     * 删除采集数据
     *
     * @param formId
     * @param dataId
     * @return
     */
    @DELETE("/online/cgform/api/form/{formId}/{dataId}")
    Observable<BasicResponse> delFormData(@Path("formId") String formId, @Path("dataId") String dataId);


    /**
     * 获取表单的所有字典
     *
     * @param formId
     * @return
     */
    @GET("/online/cgform/api/getColumns/{formId}")
    Observable<BasicResponse<FormDictionaryBean>> getDicListData(@Path("formId") String formId);


    /**
     * 根据code获取字典列表
     *
     * @param options
     * @return
     */
    @GET("/online/cgform/api/querySelectOptions")
    Observable<BasicResponse<List<LinkDownDictBean>>> getDictByCode(@QueryMap Map<String, String> options);


    /**
     * 获取数据详情
     *
     * @param formId
     * @param dataId
     * @return
     */
    @GET("/online/cgform/api/form/{formId}/{dataId}")
    Observable<BasicResponse<FormDictionaryBean>> getFormDataDetail(@Path("formId") String formId, @Path("dataId") String dataId);


    /**
     * 登陆
     *
     * @param map
     * @return
     */
    @POST("/sys/ycLogin")
    Observable<BasicResponse<LoginInfoBean>> login(@Body HashMap map);


}