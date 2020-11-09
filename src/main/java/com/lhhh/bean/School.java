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
     * 唯一id
     */
    private Integer id;
    /**
     * 学校名称
     */
    private String name;
    /**
     * 地址
     */
    private String address;
    /**
     * 学校代码
     */
    private String codeEnroll;
    /**
     * 隶属于
     */
    private String belong;
    /**
     * 省份
     */
    private String province;
    /**
     * 城市
     */
    private String city;
    /**
     * 区
     */
    private String county;
    /**
     * 38001：一流大学
     * 38002：一流学科
     * 38003：其他
     */
    private int dualClass;
    /**
     * 1:是  2：否
     */
    private int f985;
    /**
     * 1:是  2：否
     */
    private int f211;

    /**
     * 36000：公办
     * 36001：民办
     * 36002：中外合作
     * 0：其他
     */
    private int nature;

    /**
     * 院校类型(理工，农林...)
     */
    private String type;
    /**
     * 月人气
     */
    private int viewMonth;
    /**
     * 周人气
     */
    private int viewWeek;
    /**
     * 总人气
     */
    private int viewTotal;

    /**
     * 教育部直属
     */
    private int department;
    /**
     * 中央部委
     */
    private int central;
    /**
     * 强基计划
     */
    private int admissions;
    /**
     * 学校介绍
     */
    private String content;
    /**
     * 综合指数
     */
    private double comVote;
    /**
     * 学习指数
     */
    private double stuVote;
    /**
     * 生活指数
     */
    private double lifeVote;
    /**
     * 就业指数
     */
    private double jobVote;
    /**
     * 旧称
     */
    private String oldName;
    /**
     * 简称
     */
    private String shortName;
    /**
     * 面积
     */
    private double area;

    /**
     * 创办年份
     */
    private int createDate;

    /**
     * 联系方式
     */
    private String phone;
    /**
     * 邮编
     */
    private String postcode;

    /**
     * 邮箱
     */
    private String email;
    /**
     * 网址1
     */
    private String site;
    /**
     * 网址2
     */
    private String schoolSite;

    /**
     * 研招网址
     */
    private String yjszs;

    /**
     * 院士数量
     */
    private int numAcademician;
    /**
     * 博士点数量
     */
    private int numDoctor;
    /**
     * 重点实验室数量
     */
    private int numLab;
    /**
     * 图书数量
     */
    private int numLibrary;
    /**
     * 硕士点数量
     */
    private int numMaster;
    /**
     * 重点学科数量
     */
    private int numSubject;

    /**
     * 中国教育在线排名
     */
    private int eolRank;
    /**
     * 软科排名
     */
    private int ruankeRank;
    /**
     * 武书连排名
     */
    private int wslRank;
    /**
     * 校友会排名
     */
    private int xyhRank;
    /**
     * qs排名
     */
    private int qsRank;
    /**
     * 6000:普通本科
     * 6001：专科(高职)
     * 6002：独立学院
     * 6003：中外合作办学
     * 0：其他
     */
    private int schoolType;
    /**
     * 校徽图
     */
    private String badgeUrl;

    private String dataCode;
    private int recruitment;
    private int searchNum;



}
