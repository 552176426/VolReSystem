package com.lhhh.service;

import java.util.List;
import java.util.Map;

/**
 * @author: lhhh
 * @date: Created in 2020/11/18
 * @description:
 * @version:1.0
 */
public interface SpecialService {
    Map findSpecials();
    Map findSpecialOne(Integer id);
    List<Map<String, Object>> findSchoolBySpId(Map map);
}
