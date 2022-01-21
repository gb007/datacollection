package com.hollysmart.formmodule.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 地图上采集点集合
 */


@NoArgsConstructor
@Data
public class Postions implements Serializable {


    @SerializedName("postions")
    private List<Point> postions;

    @NoArgsConstructor
    @Data
    public static class Point {
        @SerializedName("fd_lng")
        private String fdLng;
        @SerializedName("fd_lnt")
        private String fdLnt;
    }
}
