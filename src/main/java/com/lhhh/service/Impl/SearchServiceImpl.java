package com.lhhh.service.Impl;

import com.github.pagehelper.PageHelper;
import com.lhhh.mapper.SchoolMapper;
import com.lhhh.mapper.SearchMapper;
import com.lhhh.mapper.SpecialMapper;
import com.lhhh.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: lhhh
 * @date: Created in 2021/1/5
 * @description:
 * @version:1.0
 */
@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private SearchMapper searchMapper;
    @Autowired
    private SchoolMapper schoolMapper;
    @Autowired
    private SpecialMapper specialMapper;

    @Override
    public List<Map<String, Object>> searchSchool(String str) throws IOException {
        StringReader sr=new StringReader(str);
        IKSegmenter ik=new IKSegmenter(sr, true);
        Lexeme lex=null;
        Map<Map,Integer> map = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        while((lex=ik.next())!=null){
            String lexemeText = lex.getLexemeText();
            sb.append(lexemeText+",");
        }
        String strs = sb.toString();
        for (String s : strs.split(",")) {
            List<Map<String, Object>> schools = searchMapper.searchSchool(s);
            for (Map<String, Object> school : schools) {
                if (map.containsKey(school)){
                    Integer count = map.get(school);
                    map.put(school,++count);
                } else {
                    map.put(school,1);
                }
            }
        }
        LinkedHashMap<Map, Integer> linkedHashMap = map.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(10)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (key, value) -> key,LinkedHashMap::new));
        List<Map<String, Object>> schoolList = new ArrayList<>();
        linkedHashMap.forEach((key,value)-> {
            Map<String,Object> schoolMap = new HashMap<>();
            schoolMap.put("id",key.get("id"));
            schoolMap.put("name",key.get("name"));
            schoolMap.put("count",value);
            schoolMap.put("badge_url",key.get("badge_url"));
            schoolList.add(schoolMap);
        });
        return schoolList;
    }

    @Override
    public Map<String, List<Map<String, Object>>> searchAll(String str) throws IOException {
        Map<String, List<Map<String, Object>>> maps = new HashMap<>();
        //学校
        List<Map<String, Object>> schoolsList = searchSchool(str);
        maps.put("schools",schoolsList);


        StringReader sr=new StringReader(str);
        IKSegmenter ik=new IKSegmenter(sr, true);
        Lexeme lex=null;
        Map<Map,Integer> map = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        while((lex=ik.next())!=null){
            String lexemeText = lex.getLexemeText();
            sb.append(lexemeText+",");
        }
        String strs = sb.toString();
        for (String s : strs.split(",")) {
            List<Map<String, Object>> specials = searchMapper.searchSpecial(s);
            for (Map<String, Object> special : specials) {
                if (map.containsKey(special)){
                    Integer count = map.get(special);
                    map.put(special,++count);
                } else {
                    map.put(special,1);
                }
            }
        }
        LinkedHashMap<Map, Integer> linkedHashMap = map.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(10)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (key, value) -> key,LinkedHashMap::new));
        List<Map<String, Object>> specialList = new ArrayList<>();
        linkedHashMap.forEach((key,value)-> {
            Map<String,Object> specialMap = new HashMap<>();
            specialMap.put("special_id",key.get("special_id"));
            specialMap.put("name",key.get("name"));
            specialMap.put("count",value);
            specialMap.put("level1",key.get("level1"));
            specialList.add(specialMap);
        });
        maps.put("specials",specialList);
        return maps;
    }

    @Override
    public Map<String, List<Map>> searchRank(Map map) {
        int pageSize = Integer.parseInt(map.get("pageSize").toString());
        Map<String, List<Map>> resultMap = new HashMap<>();
        PageHelper.startPage(1,pageSize);
        List<Map> schoolRankByViewWeek = schoolMapper.findSchoolRankByViewWeek();
        PageHelper.startPage(1,pageSize);
        List<Map> schoolRankByCreateDate = schoolMapper.findSchoolRankByCreateDate();
        PageHelper.startPage(1,pageSize);
        List<Map> specialRankBySalary = specialMapper.findSpecialRankBySalary();
        PageHelper.startPage(1,pageSize);
        List<Map> specialRankByViewWeek = specialMapper.findSpecialRankByViewWeek();
        resultMap.put("schoolVW",schoolRankByViewWeek);
        resultMap.put("schoolCD",schoolRankByCreateDate);
        resultMap.put("specialSal",specialRankBySalary);
        resultMap.put("specialVW",specialRankByViewWeek);
        return resultMap;
    }

    @Override
    public List<Map> searchRankByType(Map map) {
        int type = Integer.parseInt(map.get("type").toString());
        int page = Integer.parseInt(map.get("page").toString());
        int pageSize = Integer.parseInt(map.get("pageSize").toString());

        if (type==1){
            PageHelper.startPage(page,pageSize);
            return specialMapper.findSpecialRankBySalaryAndLevel1(map.get("choose").toString());
        } else if (type==2){
            PageHelper.startPage(page,pageSize);
            String orderType = map.get("orderType").toString();
            if (orderType.equals("view_total")){
                return specialMapper.findSpecialRankByViewTotalAndLevel1(map.get("choose").toString());
            } else if (orderType.equals("view_month")){
                return specialMapper.findSpecialRankByViewMonthAndLevel1(map.get("choose").toString());
            } else {
                return specialMapper.findSpecialRankByViewWeekAndLevel1(map.get("choose").toString());
            }
        }else if (type==3){
            PageHelper.startPage(page,pageSize);
            String orderType = map.get("orderType").toString();
            if (orderType.equals("view_total")){
                return schoolMapper.findSchoolRankByViewTotal();
            } else if (orderType.equals("view_month")){
                return schoolMapper.findSchoolRankByViewMonth();
            } else {
                return schoolMapper.findSchoolRankByViewWeek();
            }
        } else {
            PageHelper.startPage(page,pageSize);
            return schoolMapper.findSchoolRankByCreateDate();
        }

    }
}
