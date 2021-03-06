package com.hollysmart.testcollectionmodule.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class FormFiledBean implements Serializable {

    @SerializedName("id")
    private String id;
    @SerializedName("cgformHeadId")
    private String cgformHeadId;
    @SerializedName("dbFieldName")
    private String dbFieldName;
    @SerializedName("dbFieldTxt")
    private String dbFieldTxt;
    @SerializedName("dbFieldNameOld")
    private Object dbFieldNameOld;
    @SerializedName("dbIsKey")
    private Integer dbIsKey;
    @SerializedName("dbIsNull")
    private Integer dbIsNull;
    @SerializedName("dbType")
    private String dbType;
    @SerializedName("dbLength")
    private Integer dbLength;
    @SerializedName("dbPointLength")
    private Integer dbPointLength;
    @SerializedName("dbDefaultVal")
    private String dbDefaultVal;
    @SerializedName("dictField")
    private String dictField;
    @SerializedName("dictTable")
    private String dictTable;
    @SerializedName("dictText")
    private String dictText;
    @SerializedName("fieldShowType")
    private String fieldShowType;
    @SerializedName("fieldHref")
    private String fieldHref;
    @SerializedName("fieldLength")
    private Integer fieldLength;
    @SerializedName("fieldValidType")
    private Object fieldValidType;
    @SerializedName("fieldMustInput")
    private String fieldMustInput;
    @SerializedName("fieldExtendJson")
    private String fieldExtendJson;
    @SerializedName("fieldDefaultValue")
    private String fieldDefaultValue;
    @SerializedName("isQuery")
    private Integer isQuery;
    @SerializedName("isShowForm")
    private Integer isShowForm;
    @SerializedName("isShowList")
    private Integer isShowList;
    @SerializedName("isReadOnly")
    private Integer isReadOnly;
    @SerializedName("queryMode")
    private String queryMode;
    @SerializedName("mainTable")
    private String mainTable;
    @SerializedName("mainField")
    private String mainField;
    @SerializedName("orderNum")
    private Integer orderNum;
    @SerializedName("updateBy")
    private String updateBy;
    @SerializedName("updateTime")
    private String updateTime;
    @SerializedName("createTime")
    private String createTime;
    @SerializedName("createBy")
    private String createBy;
    @SerializedName("converter")
    private String converter;
    @SerializedName("queryConfigFlag")
    private String queryConfigFlag;
    @SerializedName("queryDefVal")
    private String queryDefVal;
    @SerializedName("queryDictText")
    private String queryDictText;
    @SerializedName("queryDictField")
    private String queryDictField;
    @SerializedName("queryDictTable")
    private String queryDictTable;
    @SerializedName("queryShowType")
    private Object queryShowType;
    @SerializedName("queryValidType")
    private Object queryValidType;
    @SerializedName("queryMustInput")
    private Object queryMustInput;
    @SerializedName("sortFlag")
    private String sortFlag;
    @SerializedName("orderSeq")
    private Integer orderSeq;
    @SerializedName("orderRole")
    private String orderRole;
    @SerializedName("groupTitle")
    private String groupTitle;
    @SerializedName("groupSeq")
    private Integer groupSeq;
    @SerializedName("mapLongName")
    private String mapLongName;
    @SerializedName("mapCenterPoint")
    private String mapCenterPoint;
    @SerializedName("mapApiKey")
    private String mapApiKey;
    @SerializedName("queryIsHide")
    private Object queryIsHide;
    //??????
    private List<PicBean> pic;
    //????????????????????????????????????
    private boolean showTips = false;
    //??????????????????????????????????????????
    private int position;
    //???????????????????????????
    private boolean isLinkDownChild = false;
    //??????????????????????????????
    private DictTableBean dictTableBean;
    //???????????????????????????????????????
    private List<String> childHierarchyName;
    //???????????????????????????id
    private String dictParentId;
    //??????????????????Key
    private String dictKey;

}
