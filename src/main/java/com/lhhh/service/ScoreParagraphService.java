package com.lhhh.service;

import com.lhhh.bean.ScoreParagraph;

import java.util.List;
import java.util.Map;

/**
 * @author: lhhh
 * @date: Created in 2020/10/24
 * @description:
 * @version:1.0
 */
public interface ScoreParagraphService {
    List<String> findTypesByProvinceNameAndYear(String provinceName,Integer year);
    List<Integer> findYearsByProvinceName(String provinceName);
    List<ScoreParagraph> findScoreParagraphs(Map<String,Object> chooseMap);
}
