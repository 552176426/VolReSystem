package com.lhhh.service.Impl;

import com.alibaba.fastjson.JSON;
import com.lhhh.mapper.ProvinceScoreMapper;
import com.lhhh.mapper.RecommendMapper;
import com.lhhh.mapper.ScoreParagraphMapper;
import com.lhhh.service.RecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: lhhh
 * @date: Created in 2020/11/10
 * @description:
 * @version:1.0
 */
@Service
@Slf4j
public class RecommendServiceImpl implements RecommendService {
    @Autowired
    public ScoreParagraphMapper scoreParagraphMapper;
    @Autowired
    public RecommendMapper recommendMapper;
    @Autowired
    public ProvinceScoreMapper provinceScoreMapper;


    @Override
    public Map<String, Object> findSchools(Map<String, Object> map) throws Exception {
        Map<String, Object> result = null;
        //{"江西", "河南", "内蒙古", "安徽", "四川", "贵州", "云南", "广西"}
        List<String> proList1 = Arrays.asList(new String[]{"江西", "河南", "内蒙古", "安徽", "四川", "贵州", "云南", "广西"});
        if (proList1.contains(map.get("provinceName"))) {
            result = provinces1(map);
        }
        return result;
    }

    @Override
    public Map<String, Object> findSpecials(Map<String, Object> map) throws Exception {
        return null;
    }

    /**
     * {"江西","河南","内蒙古","安徽","四川","贵州","云南","广西"}
     *
     * @param paramsMap
     * @return
     */
    public Map<String, Object> provinces1(Map<String, Object> paramsMap) {
        //过去三年年份
        List<Integer> yearList = new ArrayList<>();
        int year = Integer.parseInt(paramsMap.get("year").toString());
        yearList.add(year - 3);
        yearList.add(year - 2);
        yearList.add(year - 1);
        log.info("====>yearList:{}", yearList);
        paramsMap.put("yearList", yearList);
        double[] weightYear = {0.25, 0.35, 0.4};
        paramsMap.put("weightYears", weightYear);
        List<String> chooseLocations = null;
        if (paramsMap.get("chooseLocations") != null) {
            chooseLocations = (List<String>) JSON.parse(paramsMap.get("chooseLocations").toString());
        }
        List<String> chooseSchoolLevels = null;
        if (paramsMap.get("chooseSchoolLevels") != null) {
            chooseSchoolLevels = (List<String>) JSON.parse(paramsMap.get("chooseSchoolLevels").toString());
        }
        List<String> chooseSchoolTypes = null;
        if (paramsMap.get("chooseSchoolTypes") != null) {
            chooseSchoolTypes = (List<String>) JSON.parse(paramsMap.get("chooseSchoolTypes").toString());
        }
        List<String> chooseBanxueTypes = null;
        if (paramsMap.get("chooseBanxueTypes") != null) {
            chooseBanxueTypes = (List<String>) JSON.parse(paramsMap.get("chooseBanxueTypes").toString());
        }

        if (chooseSchoolTypes != null && chooseSchoolTypes.size() != 0) {
            chooseSchoolTypes = chooseSchoolTypes.stream().map(s -> s + "类").collect(Collectors.toList());
        }
        if (chooseBanxueTypes != null && chooseBanxueTypes.size() != 0) {
            chooseBanxueTypes = chooseBanxueTypes.stream().map(s -> {
                if (s.equals("公办")) {
                    s = "36000";
                } else if (s.equals("民办")) {
                    s = "36001";
                }
                return s;
            }).collect(Collectors.toList());
        }
        paramsMap.put("chooseSchoolTypes", chooseSchoolTypes);
        paramsMap.put("chooseBanxueTypes", chooseBanxueTypes);
        paramsMap.put("chooseLocations", chooseLocations);
        paramsMap.put("chooseSchoolLevels", chooseSchoolLevels);

        //过去三年录取数据
        List<Map<String, Object>> schoolList = recommendMapper.findSchoolsAdmission(paramsMap);
        //按校名分组
        Map<Object, List<Map<String, Object>>> mapSchoolList = schoolList.stream().collect(Collectors.groupingBy(s -> s.get("school_name")));

        //获取排名信息
        Integer scoreOrder;
        if (StringUtils.isEmpty(paramsMap.get("scoreOrder"))||paramsMap.get("scoreOrder").toString().equals("null")||paramsMap.get("scoreOrder").toString().equals("undefined")) {
            Map<String, Object> findOrderMap = new HashMap<>();
            findOrderMap.put("score", paramsMap.get("score"));
            findOrderMap.put("provinceName", paramsMap.get("provinceName"));
            findOrderMap.put("provinceName", paramsMap.get("provinceName"));
            findOrderMap.put("year", paramsMap.get("year"));
            findOrderMap.put("type", paramsMap.get("curriculum"));
            scoreOrder = scoreParagraphMapper.findNumberByScore(findOrderMap);
            paramsMap.put("scoreOrder", scoreOrder);
        } else {
            scoreOrder = Integer.valueOf(paramsMap.get("scoreOrder").toString());
        }
        //小于5000通过排名推荐，否则通过线差推荐
        if (scoreOrder < 5000) {
            paramsMap.put("method", "sort");
        } else {
            paramsMap.put("method", "score");
        }

        System.out.println(paramsMap);

        return findSchool(mapSchoolList, paramsMap);
    }


    /**
     * @param mapSchoolList
     * @param map
     * @return
     */
    public Map<String, Object> findSchool(Map<Object, List<Map<String, Object>>> mapSchoolList, Map map) {
        Map<String, Object> result = new HashMap<>();
        //分页条件
        int page = Integer.parseInt(map.get("page").toString());
        int pageCount = Integer.parseInt(map.get("pageCount").toString());
        if (page < 0) {
            page = 1;
        }
        if (pageCount < 0) {
            pageCount = 15;
        }

        //类型
        String type = map.get("type").toString();
        //计算线差
        Integer provinceScore = provinceScoreMapper.findProvinceScore(map);
        Integer score = Integer.valueOf(map.get("score").toString());
        Integer stuMinCha = score - provinceScore;
        log.info("====>stuMinCha={}-{}={}", score, provinceScore, stuMinCha);

        List<Integer> yearList = (List) map.get("yearList");//[2017,2018,2019]
        double[] weightYears = (double[]) map.get("weightYears");
        //过滤不包含2019年数据的学校
//        mapSchoolList=mapSchoolList.entrySet().stream().filter(s->s.getValue().get(s.getValue().size()-1).get("year").equals(yearList.get(2))).collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
        /**
         * 大连海事大学=[
         *          {min_cha=54, min_score_order=11623, batch_name=本科一批, year=2017, school_name=大连海事大学, curriculum=理科, min_score=557, province_name=江西},
         *          {min_cha=62, min_score_order=11747, batch_name=本科一批, year=2018, school_name=大连海事大学, curriculum=理科, min_score=589, province_name=江西},
         *          {min_cha=58, min_score_order=13026, batch_name=本科一批, year=2019, school_name=大连海事大学, curriculum=理科, min_score=580, province_name=江西}
         *          ]
         */
        List<Map<String, Object>> schoolList = new ArrayList<>();
        mapSchoolList.entrySet().stream().forEach(entry -> {
            double sumMinCha = 0;
            double sumScoreOrder = 0;
            double weight = 0;

            //线差Map {2017:30,2018:31,2019:28}
            Map<Integer, Integer> minChaYearMap = entry.getValue().stream().collect(Collectors.toMap(s -> Integer.valueOf(s.get("year").toString()), s -> Integer.parseInt(s.get("min_cha").toString())));
            //最低位次Map
            Map<Integer, Integer> minScoreOrderMap = entry.getValue().stream().collect(Collectors.toMap(s -> Integer.valueOf(s.get("year").toString()), s -> Integer.parseInt(s.get("min_score_order").toString())));
//            System.out.println(entry.getKey()+":"+minChaYearMap+":"+minScoreOrderMap);
            for (int i = 0; i < yearList.size(); i++) {
                if (minChaYearMap.containsKey(yearList.get(i))) {
                    sumMinCha += minChaYearMap.get(yearList.get(i)) * weightYears[i];
                    sumScoreOrder += minScoreOrderMap.get(yearList.get(i)) * weightYears[i];
                    weight += weightYears[i];
                }
            }

            int aveMinCha = (int) Math.round(sumMinCha / weight);
            int aveScoreOrder = (int) Math.round(sumScoreOrder / weight);
            List<Map<String, Object>> admissionList = entry.getValue();
            Map<String, Object> schoolMap = new HashMap<>();
            schoolMap.put("school_name", entry.getKey());
            schoolMap.put("admissionList", admissionList);
            schoolMap.put("aveMinCha", aveMinCha);
            schoolMap.put("aveScoreOrder", aveScoreOrder);
            schoolMap.put("school_id",entry.getValue().get(0).get("id"));
            //通过线差
            if (map.get("method").equals("sort")) {
                //冲的学校
                if ((5 < aveMinCha - stuMinCha) && (aveMinCha - stuMinCha <= 10)) {
                    schoolMap.put("type", "冲");
                    //稳的学校
                } else if ((-5 <= aveMinCha - stuMinCha) && (aveMinCha - stuMinCha <= 5)) {
                    schoolMap.put("type", "稳");
                    //保的学校
                } else if ((-30 <= aveMinCha - stuMinCha) && (aveMinCha - stuMinCha < -5)) {
                    schoolMap.put("type", "保");
                }
            } else if (map.get("method").equals("score")) {
                //通过排名
                int scoreOrder = Integer.parseInt(map.get("scoreOrder").toString());
                if ((scoreOrder * 0.9 <= aveScoreOrder) && (aveScoreOrder < scoreOrder)) {
                    schoolMap.put("type", "冲");
                    //稳的学校
                } else if ((scoreOrder <= aveScoreOrder) && (aveScoreOrder <= scoreOrder * 1.2)) {
                    schoolMap.put("type", "稳");
                    //保的学校
                } else if ((scoreOrder * 1.2 < aveScoreOrder) && (aveScoreOrder <= scoreOrder * 2)) {
                    schoolMap.put("type", "保");
                }
            }
            schoolList.add(schoolMap);
        });
        List<Map<String, Object>> list = new ArrayList<>();
        int length = 0;
        if (!type.equals("更多")) {
            list = schoolList.stream()
                    .filter(s -> s.containsKey("type") && s.get("type").equals(type))
                    .sorted(Comparator.comparing(s -> Integer.parseInt(s.get("aveScoreOrder").toString())))
                    .collect(Collectors.toList());
            length = list.size();
            list = list.stream().skip((page - 1) * pageCount).limit(pageCount).collect(Collectors.toList());
        } else {
            list = schoolList.stream()
                    .sorted(Comparator.comparing(s -> Integer.parseInt(s.get("aveScoreOrder").toString())))
                    .collect(Collectors.toList());
            length = list.size();
            list = list.stream().skip((page - 1) * pageCount).limit(pageCount).collect(Collectors.toList());
        }
        //获取这些学校的历年分数
        for (Map<String, Object> stringObjectMap : list) {
            map.put("schoolName", stringObjectMap.get("school_name").toString());
            List<Map> admissionData = recommendMapper.findSchoolAdmission(map);
            stringObjectMap.put("admissionData", admissionData);
        }
        result.put("list", list);
        Map<String, Object> infoMap = new HashMap<>();
        infoMap.put("stuMinCha", stuMinCha);
        infoMap.put("yearList", map.get("yearList"));
        infoMap.put("score", map.get("score"));
        infoMap.put("scoreOrder", map.get("scoreOrder"));
        infoMap.put("provinceName", map.get("provinceName"));
        infoMap.put("curriculum", map.get("curriculum"));
        infoMap.put("batchName", map.get("batchName"));
        infoMap.put("year", map.get("year"));
        infoMap.put("chooseSchoolTypes", map.get("chooseSchoolTypes"));
        infoMap.put("chooseBanxueTypes", map.get("chooseBanxueTypes"));
        infoMap.put("chooseLocations", map.get("chooseLocations"));
        infoMap.put("chooseSchoolLevels", map.get("chooseSchoolLevels"));
        result.put("info", infoMap);
        result.put("length", length);
        return result;
    }


    /**
     * 通过分数计算能上的批次
     *
     * @param paramsMap
     * @return
     */
    public String findBatch(Map paramsMap) {
        String batchName = null;
        List<String> batches = (List) JSON.parse(paramsMap.get("batches").toString());
        System.out.println(batches);
        paramsMap.put("batches", batches);
        List<Integer> scores = provinceScoreMapper.findProvinceScores(paramsMap);
        int score = Integer.parseInt(paramsMap.get("score").toString());
        int index = -1;
        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i) <= score) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            batchName = batches.get(index);
        }
        return batchName;
    }


}
