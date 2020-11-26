package com.lhhh.mapper;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author: lhhh
 * @date: Created in 2020/11/12
 * @description:
 * @version:1.0
 */
@Repository
public interface RecommendMapper {

    /**
     * @param map
     * @return
     */
    List<Map<String,Object>> findSchoolsAdmission(Map<String, Object> map);
    List<Map<String,Object>> findSchoolAdmission(Map<String, Object> map);
}
