package com.lhhh.service;

import com.lhhh.bean.School;

import java.util.List;
import java.util.Map;

/**
 * @author: lhhh
 * @date: Created in 2020/11/7
 * @description:
 * @version:1.0
 */
public interface SchoolService {
    List<School> findSchoolsOrderByXyhRank(Map<String,Object> paramsMap);
    Integer findSchoolsCount(Map<String,Object> paramsMap);


}
