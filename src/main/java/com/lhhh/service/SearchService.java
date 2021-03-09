package com.lhhh.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author: lhhh
 * @date: Created in 2021/1/5
 * @description:
 * @version:1.0
 */
public interface SearchService {
    List<Map<String, Object>> searchSchool(String str) throws IOException;
    Map<String,List<Map<String, Object>>> searchAll(String str) throws IOException;
    Map<String,List<Map>> searchRank(Map map);
    List<Map> searchRankByType(Map map);
}
