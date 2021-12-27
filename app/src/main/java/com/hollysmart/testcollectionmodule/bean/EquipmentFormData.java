package com.hollysmart.testcollectionmodule.bean;

import com.google.gson.JsonArray;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 场地表单数据
 */
@Data
@NoArgsConstructor
public class EquipmentFormData implements Serializable {

    private int total;
    private JsonArray records;
    private List<FormFiledBean> fieldList;
    private boolean isGroupTitle;


}
