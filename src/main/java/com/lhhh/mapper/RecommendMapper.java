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
     * map: provinceName(招生省份)、curriculum(文理科)、batchName(批次)、yearList(年份)
     * @return
     */
    List<Map<String,Object>> findSchoolsAdmission(Map<String, Object> map);
}
