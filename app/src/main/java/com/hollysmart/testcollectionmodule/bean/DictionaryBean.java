package com.hollysmart.testcollectionmodule.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DictionaryBean implements Serializable {

    @SerializedName("value")
    private String value;
    @SerializedName("text")
    private String text;
    @SerializedName("title")
    private String title;
}
