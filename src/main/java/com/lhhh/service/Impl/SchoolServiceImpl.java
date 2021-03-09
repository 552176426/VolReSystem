package com.lhhh.service.Impl;

import com.github.pagehelper.PageHelper;
import com.lhhh.bean.School;
import com.lhhh.mapper.SchoolMapper;
import com.lhhh.service.SchoolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: lhhh
 * @date: Created in 2020/11/7
 * @description:
 * @version:1.0
 */
@Service
@Slf4j
public class SchoolServiceImpl implements SchoolService {
    @Autowired
    private SchoolMapper schoolMapper;

    @Override
    public List<School> findSchoolsOrderByXyhRank(Map<String, Object> paramsMap) {
        int page = Integer.valueOf(paramsMap.get("page").toString());
        int pageCount = Integer.valueOf(paramsMap.get("pageCount").toString());
        List<String> chooseLocations = null;
        List<String> chooseSchoolLevels = null;
        List<String> chooseSchoolTypes = null;
        List<String> chooseBanxueTypes = null;
        if (!paramsMap.get("chooseLocations").toString().equals("")) {
            chooseLocations = Arrays.asList(paramsMap.get("chooseLocations").toString().split(","));
        }
        if (!paramsMap.get("chooseSchoolLevels").toString().equals("")) {
            chooseSchoolLevels = Arrays.asList(paramsMap.get("chooseSchoolLevels").toString().split(","));
        }
        if (!paramsMap.get("chooseSchoolTypes").toString().equals("")) {
            chooseSchoolTypes = Arrays.asList(paramsMap.get("chooseSchoolTypes").toString().split(","));
            chooseSchoolTypes = chooseSchoolTypes.stream().map(s -> s + "类").collect(Collectors.toList());
        }
        if (!paramsMap.get("chooseBanxueTypes").toString().equals("")) {
            chooseBanxueTypes = Arrays.asList(paramsMap.get("chooseBanxueTypes").toString().split(","));
            chooseBanxueTypes = chooseBanxueTypes.stream().map(s -> {
                if (s.equals("普通本科")) {
                    s = "6000";
                } else if (s.equals("专科(高职)")) {
                    s = "6001";
                } else if (s.equals("独立学院")) {
                    s = "6002";
                } else if (s.equals("中外合作办学")) {
                    s = "6003";
                }
                return s;
            }).collect(Collectors.toList());
        }
        PageHelper.startPage(page, pageCount);
        log.info("chooseLocations:{},chooseSchoolTypes:{},chooseBanxueTypes:{},chooseSchoolLevels:{}", chooseLocations, chooseSchoolTypes, chooseBanxueTypes, chooseSchoolLevels);
        return schoolMapper.findSchoolsOrderByXyhRank(chooseLocations, chooseSchoolTypes, chooseBanxueTypes, chooseSchoolLevels);
    }

    @Override
    public Integer findSchoolsCount(Map<String, Object> paramsMap) {
        List<String> chooseLocations = null;
        List<String> chooseSchoolLevels = null;
        List<String> chooseSchoolTypes = null;
        List<String> chooseBanxueTypes = null;
        if (!paramsMap.get("chooseLocations").toString().equals("")) {
            chooseLocations = Arrays.asList(paramsMap.get("chooseLocations").toString().split(","));
        }
        if (!paramsMap.get("chooseSchoolLevels").toString().equals("")) {
            chooseSchoolLevels = Arrays.asList(paramsMap.get("chooseSchoolLevels").toString().split(","));
        }
        if (!paramsMap.get("chooseSchoolTypes").toString().equals("")) {
            chooseSchoolTypes = Arrays.asList(paramsMap.get("chooseSchoolTypes").toString().split(","));
            chooseSchoolTypes = chooseSchoolTypes.stream().map(s -> s + "类").collect(Collectors.toList());
        }
        if (!paramsMap.get("chooseBanxueTypes").toString().equals("")) {
            chooseBanxueTypes = Arrays.asList(paramsMap.get("chooseBanxueTypes").toString().split(","));
            chooseBanxueTypes = chooseBanxueTypes.stream().map(s -> {
                if (s.equals("普通本科")) {
                    s = "6000";
                } else if (s.equals("专科(高职)")) {
                    s = "6001";
                } else if (s.equals("独立学院")) {
                    s = "6002";
                } else if (s.equals("中外合作办学")) {
                    s = "6003";
                }
                return s;
            }).collect(Collectors.toList());
        }
        log.info("chooseLocations:{},chooseSchoolTypes:{},chooseBanxueTypes:{},chooseSchoolLevels:{}", chooseLocations, chooseSchoolTypes, chooseBanxueTypes, chooseSchoolLevels);
        return schoolMapper.findSchoolsCount(chooseLocations, chooseSchoolTypes, chooseBanxueTypes, chooseSchoolLevels);
    }

    @Override
    public Map<String, Object> findOne(Integer id) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> school = schoolMapper.findOne(id);
        school.entrySet().stream().map(i -> {
            if (i.getValue() == null) {
                String key = i.getKey();
                if (key.equals("wsl_rank") || key.equals("ruanke_rank") || key.equals("xyh_rank") || key.equals("qs_rank") || key.equals("eol_rank")) {
                    i.setValue("--");
                } else {
                    i.setValue("");
                }
            }
            return i;
        }).collect(Collectors.toList());

        String nature = school.get("nature").toString();
        if (nature.equals("36000")) {
            school.put("nature", "公办");
        } else if (nature.equals("36001")) {
            school.put("nature", "民办");
        } else if (nature.equals("36002")) {
            school.put("nature", "中外合作");
        } else {
            school.put("nature", "其他");
        }


        //一流学科
        List<Map<String, Object>> dualClassList = schoolMapper.findDualClass(id);

        //学校图
        List<Map<String, String>> images = schoolMapper.findSchoolImages(id);
        result.put("images", images);

        //学校专业
        List<Map<String, String>> specialList = schoolMapper.findSchoolSpecial(id);
        List<Map<String, String>> nation_feature = specialList.stream().filter(i -> {
            return i.get("nation_feature").equals("1");
        }).collect(Collectors.toList());


        //就业
        List<Map<String, String>> jobDetail = schoolMapper.findSchoolJobDetail(id);
        List<Map<String, String>> type1 = jobDetail.stream().filter(i -> {
            return i.get("type").equals("1");
        }).sorted(Comparator.comparing((Map<String, String> i)->Double.parseDouble(i.get("num"))).reversed()).limit(10)
                .collect(Collectors.toList());

        List<Map<String, String>> type2 = jobDetail.stream().filter(i -> {
            return i.get("type").equals("2");
        }).sorted(Comparator.comparing((Map<String, String> i)->Double.parseDouble(i.get("rate"))).reversed()).limit(5)
                .collect(Collectors.toList());

        List<Map<String, String>> type3 = jobDetail.stream().filter(i -> {
            return i.get("type").equals("3");
        }).sorted(Comparator.comparing((Map<String, String> i)->Double.parseDouble(i.get("num"))).reversed()).limit(5)
                .collect(Collectors.toList());

        List<Map<String, String>> type4 = jobDetail.stream().filter(i -> {
            return i.get("type").equals("4");
        }).sorted(Comparator.comparing((Map<String, String> i)->Double.parseDouble(i.get("num"))).reversed()).limit(5)
                .collect(Collectors.toList());
        Map<String,Object> jobDetailMap = new HashMap<>();
        jobDetailMap.put("type1",type1);
        jobDetailMap.put("type2",type2);
        jobDetailMap.put("type3",type3);
        jobDetailMap.put("type4",type4);

        //就业前景最好专业
        List<Map<String, String>> bestJobSpecial = schoolMapper.findBestJobSpecial(id);


        result.put("specialList", specialList);
        result.put("bestJobSpecial", bestJobSpecial);
        result.put("jobDetailMap", jobDetailMap);
        result.put("nation_feature", nation_feature);
        result.put("baseInfo", school);
        result.put("dualClassList", dualClassList);
        return result;
    }

    @Override
    public Map findMinScore(Map map) {
        return schoolMapper.findMinScore(map);
    }

    @Override
    public List<Map> findSchoolScore(Map map) {
        return schoolMapper.findSchoolScore(map);
    }
}
