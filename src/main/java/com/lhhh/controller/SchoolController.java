package com.lhhh.controller;

import com.lhhh.bean.Msg;
import com.lhhh.bean.Result;
import com.lhhh.bean.School;
import com.lhhh.service.SchoolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author: lhhh
 * @date: Created in 2020/11/7
 * @description:
 * @version:1.0
 */
@Controller
@RequestMapping("/school")
@Slf4j
public class SchoolController {
    @Autowired
    private SchoolService schoolService;

    /*@GetMapping("/findSchoolsAdmission")
    @ResponseBody
    public Result findSchoolsOrderByXyhRank(@RequestParam Integer page,@RequestParam Integer pageCount){
        List<School> schoolList = schoolService.findSchoolsOrderByXyhRank(page,pageCount);
        return new Result(200, Msg.GET_SCHOOL_DATA_SUCCESS,schoolList);
    }*/

    @PostMapping("/findSchools")
    @ResponseBody
    public Result findSchoolsOrderByXyhRank(@RequestParam Map<String, Object> paramsMap){
        log.info("paramsMap: {}",paramsMap);
        List<School> schoolList = schoolService.findSchoolsOrderByXyhRank(paramsMap);
        return new Result(200,Msg.GET_SCHOOL_DATA_SUCCESS, schoolList);
    }

    @PostMapping("/findSchoolsCount")
    @ResponseBody
    public Result findSchoolsCount(@RequestParam Map<String, Object> paramsMap){
        log.info("paramsMap: {}",paramsMap);
        Integer schoolsCount = schoolService.findSchoolsCount(paramsMap);
        return new Result(200,Msg.GET_SCHOOL_DATA_SUCCESS, schoolsCount);
    }

    @GetMapping("/findSchool")
    @ResponseBody
    public Result findSchool(@RequestParam Integer id){
        try {
            Map<String, Object> school = schoolService.findOne(id);
            return new Result(200,Msg.GET_SCHOOL_DATA_SUCCESS, school);
        } catch (Exception e){
            e.printStackTrace();
            return new Result(500,Msg.GET_SCHOOL_DATA_FAIL, null);
        }
    }

    @GetMapping("/findMinScore")
    @ResponseBody
    public Result findMinScore(@RequestParam Map map){
        try {
            Map minScore = schoolService.findMinScore(map);
            return new Result(200,Msg.GET_SCHOOL_MINSCORE_SUCCESS, minScore);
        } catch (Exception e){
            e.printStackTrace();
            return new Result(500,Msg.GET_SCHOOL_MINSCORE_FAIL, null);
        }
    }

    @GetMapping("/findSchoolScore")
    @ResponseBody
    public Result findSchoolScore(@RequestParam Map map){
        try {
            List<Map> schoolScore = schoolService.findSchoolScore(map);
            return new Result(200,Msg.GET_SCHOOL_SCORE_SUCCESS, schoolScore);
        } catch (Exception e){
            e.printStackTrace();
            return new Result(500,Msg.GET_SCHOOL_SCORE_FAIL, null);
        }
    }

}
