package com.hollysmart.formmodule.bean;

import com.google.gson.JsonArray;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表单数据
 */
@Data
@NoArgsConstructor
public class FormData implements Serializable {

    private int total;
    private JsonArray records;
    private List<FormFiledBean> fieldList;
    private boolean isGroupTitle;


}
