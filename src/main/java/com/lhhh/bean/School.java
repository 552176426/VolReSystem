package com.lhhh.bean;

import lombok.Data;

/**
 * @author: lhhh
 * @date: Created in 2020/9/23
 * @description:
 * @version:1.0
 */
@Data
public class School {
    /**
     * 地址
     */
    private String address;
    /**
     * 面积
     */
    private double area;
    /**
     * 隶属于
     */
    private String belong;
    /**
     * 唯一id
     */
    private Integer schoolId;
    /**
     * 学校名称
     */
    private String name;
    /**
     * 学校标志码
     */
    private String schoolCode;
    /**
     * 主管部门
     */
    private String CompetentAuthority;
    /**
     * 所在地
     */
    private String location;
    /**
     * 省份id
     */
    private int provinceId;
    /**
     * 办学层次
     */
    private String schLevel;
    /**
     * 热度
     */
    private Long Popularity;
    /**
     * 备注
     */
    private String remark;
    /**
     * 图片url
     */
    private String imgUrl;



}
