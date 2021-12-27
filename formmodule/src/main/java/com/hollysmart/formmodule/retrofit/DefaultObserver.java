package com.hollysmart.formmodule.retrofit;

import android.net.ParseException;

import com.google.gson.JsonParseException;
import com.hjq.toast.ToastUtils;
import com.hollysmart.formmodule.R;
import com.hollysmart.formmodule.Utils.LogUtils;

import org.json.JSONException;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

public abstract class DefaultObserver<T> implements Observer<T> {
    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T response) {
        onSuccess(response);
        onFinish();
    }

    @Override
    public void onError(Throwable e) {
        LogUtils.d("Retrofit", e.getMessage());
        if (e instanceof HttpException) {     //   HTTP错误
            if (((HttpException) e).code() == 500) {//Tokeng过期
                onException(ExceptionReason.TOKEN_EXPIRE);
            } else {
                onException(ExceptionReason.BAD_NETWORK);
            }
        } else if (e instanceof ConnectException
                || e instanceof UnknownHostException) {   //   连接错误
            onException(ExceptionReason.CONNECT_ERROR);
        } else if (e instanceof InterruptedIOException) {   //  连接超时
            onException(ExceptionReason.CONNECT_TIMEOUT);
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {   //  解析错误
            onException(ExceptionReason.PARSE_ERROR);
        } else {
            onException(ExceptionReason.UNKNOWN_ERROR);
        }
        onFinish();

//        else if(e instanceof ServerResponseException){
//            onFail(e.getMessage());
//        }
    }

    @Override
    public void onComplete() {
    }

    /**
     * 请求成功
     *
     * @param response 服务器返回的数据
     */
    abstract public void onSuccess(T response);

    /**
     * 服务器返回数据，但响应码不为200
     */
    public void onFail(String message) {
        ToastUtils.show(message);
    }

    public void onFinish() {
    }

    /**
     * 请求异常
     *
     * @param reason
     */
    public void onException(ExceptionReason reason) {
        switch (reason) {
            case CONNECT_ERROR:
                ToastUtils.show(R.string.form_model_connect_error);
                break;

            case CONNECT_TIMEOUT:
                ToastUtils.show(R.string.form_model_connect_timeout);
                break;

            case BAD_NETWORK:
                ToastUtils.show(R.string.form_model_bad_network);
                break;

            case PARSE_ERROR:
                ToastUtils.show(R.string.form_model_parse_error);
                break;

            case TOKEN_EXPIRE:
                ToastUtils.show(R.string.form_model_token_expire);
                break;

            case UNKNOWN_ERROR:
            default:
                ToastUtils.show(R.string.form_model_unknown_error);
                break;
        }
    }

    /**
     * 请求网络失败原因
     */
    public enum ExceptionReason {
        /**
         * 解析数据失败
         */
        PARSE_ERROR,
        /**
         * 网络问题
         */
        BAD_NETWORK,
        /**
         * 连接错误
         */
        CONNECT_ERROR,
        /**
         * 连接超时
         */
        CONNECT_TIMEOUT,
        /**
         * Token失效
         */
        TOKEN_EXPIRE,
        /**
         * 未知错误
         */
        UNKNOWN_ERROR,
    }
}
