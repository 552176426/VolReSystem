package com.lhhh.service.Impl;

import com.lhhh.mapper.SpecialMapper;
import com.lhhh.service.SpecialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: lhhh
 * @date: Created in 2020/11/18
 * @description:
 * @version:1.0
 */
@Service
public class SpecialServiceImpl implements SpecialService {
    @Autowired
    private SpecialMapper specialMapper;

    @Override
    public Map findSpecials() {

        List<Map<String, Object>> specials1 = specialMapper.findSpecials1();
        List<Map<String, Object>> specials2 = specialMapper.findSpecials2();


        Map<String, List<Map<String, Object>>> level2TypesMap1 = specials1.stream()
                .map(i -> {
                    String degree = i.get("degree").toString();
                    if (!(degree == null || degree.length() == 0)) {
                        i.put("degree", degree.substring(0, degree.length() - 2));
                    }
                    return i;
                })
                .collect(Collectors.groupingBy(i -> i.get("level2_name").toString()));
        //{1:[{},{}],2:[{},{}]}
        Map result1 = new HashMap();
        level2TypesMap1.forEach((key, value) -> {
            Map<String, List<Map<String, Object>>> level3TypesMap = value.stream()
                    .collect(Collectors.groupingBy(i -> i.get("level3_name").toString()));
            result1.put(key, level3TypesMap);
        });

        Map<String, List<Map<String, Object>>> level2TypesMap2 = specials2.stream()
                .map(i -> {
                    String degree = i.get("degree").toString();
                    if (!(degree == null || degree.length() == 0)) {
                        i.put("degree", degree.substring(0, degree.length() - 2));
                    }
                    return i;
                })
                .collect(Collectors.groupingBy(i -> i.get("level2_name").toString()));
        //{1:[{},{}],2:[{},{}]}
        Map result2 = new HashMap();
        level2TypesMap2.forEach((key, value) -> {
            Map<String, List<Map<String, Object>>> level3TypesMap = value.stream()
                    .collect(Collectors.groupingBy(i -> i.get("level3_name").toString()));
            result2.put(key, level3TypesMap);
        });

        Map result = new HashMap();
        result.put("1",result1);
        result.put("2",result2);
        return result;
    }
}
