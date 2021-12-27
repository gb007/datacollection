package com.hollysmart.testcollectionmodule.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 多级联动字典
 */

@Data
@NoArgsConstructor
public class LinkDownDictBean implements Serializable {


    @SerializedName("label")
    private String label;
    @SerializedName("store")
    private String store;
    @SerializedName("id")
    private String id;
    @SerializedName("pid")
    private String pid;
}
