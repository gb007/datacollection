package com.hollysmart.formmodule.bean;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图片信息
 */


@Data
@NoArgsConstructor
public class PicBean implements Serializable {

    private int id;

    private String filename;

    private boolean status;

    private String filePath;

    private String imageUrl;

    private int isAddFlag = 1;//

    private boolean isJsx;

    private String isDownLoad;

    private String picId;
}
