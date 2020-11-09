package com.lhhh.service.Impl;

import com.github.pagehelper.PageHelper;
import com.lhhh.bean.School;
import com.lhhh.mapper.SchoolMapper;
import com.lhhh.service.SchoolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
    public List<School> findSchoolsOrderByXyhRank(Map<String,Object> paramsMap) {
        int page = Integer.valueOf(paramsMap.get("page").toString());
        int pageCount = Integer.valueOf(paramsMap.get("pageCount").toString());
        List<String> chooseLocations = null;
        List<String> chooseSchoolLevels = null;
        List<String> chooseSchoolTypes = null;
        List<String> chooseBanxueTypes = null;
        if (!paramsMap.get("chooseLocations").toString().equals("")){
            chooseLocations = Arrays.asList(paramsMap.get("chooseLocations").toString().split(","));
        }
        if (!paramsMap.get("chooseSchoolLevels").toString().equals("")){
            chooseSchoolLevels = Arrays.asList(paramsMap.get("chooseSchoolLevels").toString().split(","));
        }
        if (!paramsMap.get("chooseSchoolTypes").toString().equals("")){
            chooseSchoolTypes = Arrays.asList(paramsMap.get("chooseSchoolTypes").toString().split(","));
            chooseSchoolTypes = chooseSchoolTypes.stream().map(s->s+"类").collect(Collectors.toList());
        }
        if (!paramsMap.get("chooseBanxueTypes").toString().equals("")){
            chooseBanxueTypes = Arrays.asList(paramsMap.get("chooseBanxueTypes").toString().split(","));
            chooseBanxueTypes = chooseBanxueTypes.stream().map(s->{
                if (s.equals("普通本科")){
                    s="6000";
                } else if (s.equals("专科(高职)")){
                    s="6001";
                }else if (s.equals("独立学院")){
                    s="6002";
                }else if (s.equals("中外合作办学")){
                    s="6003";
                }
                return s;
            }).collect(Collectors.toList());
        }
        PageHelper.startPage(page,pageCount);
        log.info("chooseLocations:{},chooseSchoolTypes:{},chooseBanxueTypes:{},chooseSchoolLevels:{}",chooseLocations,chooseSchoolTypes,chooseBanxueTypes,chooseSchoolLevels);
        return schoolMapper.findSchoolsOrderByXyhRank(chooseLocations,chooseSchoolTypes,chooseBanxueTypes,chooseSchoolLevels);
    }

    @Override
    public Integer findSchoolsCount(Map<String, Object> paramsMap) {
        List<String> chooseLocations = null;
        List<String> chooseSchoolLevels = null;
        List<String> chooseSchoolTypes = null;
        List<String> chooseBanxueTypes = null;
        if (!paramsMap.get("chooseLocations").toString().equals("")){
            chooseLocations = Arrays.asList(paramsMap.get("chooseLocations").toString().split(","));
        }
        if (!paramsMap.get("chooseSchoolLevels").toString().equals("")){
            chooseSchoolLevels = Arrays.asList(paramsMap.get("chooseSchoolLevels").toString().split(","));
        }
        if (!paramsMap.get("chooseSchoolTypes").toString().equals("")){
            chooseSchoolTypes = Arrays.asList(paramsMap.get("chooseSchoolTypes").toString().split(","));
            chooseSchoolTypes = chooseSchoolTypes.stream().map(s->s+"类").collect(Collectors.toList());
        }
        if (!paramsMap.get("chooseBanxueTypes").toString().equals("")){
            chooseBanxueTypes = Arrays.asList(paramsMap.get("chooseBanxueTypes").toString().split(","));
            chooseBanxueTypes = chooseBanxueTypes.stream().map(s->{
                if (s.equals("普通本科")){
                    s="6000";
                } else if (s.equals("专科(高职)")){
                    s="6001";
                }else if (s.equals("独立学院")){
                    s="6002";
                }else if (s.equals("中外合作办学")){
                    s="6003";
                }
                return s;
            }).collect(Collectors.toList());
        }
        log.info("chooseLocations:{},chooseSchoolTypes:{},chooseBanxueTypes:{},chooseSchoolLevels:{}",chooseLocations,chooseSchoolTypes,chooseBanxueTypes,chooseSchoolLevels);
        return schoolMapper.findSchoolsCount(chooseLocations,chooseSchoolTypes,chooseBanxueTypes,chooseSchoolLevels);
    }
}
