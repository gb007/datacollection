package com.hollysmart.testcollectionmodule.retrofit;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BasicResponse<T> {
    private boolean success;
    private int code;
    private String message;
    private T result;

//    private int status;
//    private String timestamp;
//    private String trace;
//    private String error;
//    private String exception;
//    private String path;


}
