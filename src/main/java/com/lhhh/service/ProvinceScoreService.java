package com.lhhh.service;

import com.lhhh.bean.ProvinceScore;

import java.util.List;

/**
 * @author: lhhh
 * @date: Created in 2020/11/4
 * @description:
 * @version:1.0
 */
public interface ProvinceScoreService {
    List<ProvinceScore> findProvinceScoreByProvinceNameAndYear(String provinceName,Integer year);
    List<String> getAllProvince();
    public static final int a = 3;
}
