package com.lhhh.service;

import java.util.Map;

/**
 * @author: lhhh
 * @date: Created in 2020/11/10
 * @description:
 * @version:1.0
 */
public interface RecommendService {
    Map<String,Object> findSchools(Map<String, Object> map) throws Exception;
    Map<String,Object> findSpecials(Map<String, Object> map) throws Exception;
    public String findBatch(Map paramsMap) throws  Exception;
}
