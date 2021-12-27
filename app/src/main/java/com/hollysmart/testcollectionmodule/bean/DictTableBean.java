package com.hollysmart.testcollectionmodule.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DictTableBean implements Serializable {

    @SerializedName("table")
    private String table;
    @SerializedName("txt")
    private String txt;
    @SerializedName("key")
    private String key;
    @SerializedName("linkField")
    private String linkField;
    @SerializedName("idField")
    private String idField;
    @SerializedName("pidField")
    private String pidField;
    @SerializedName("condition")
    private String condition;
}
