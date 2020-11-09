package com.lhhh.controller;

import com.lhhh.bean.Msg;
import com.lhhh.bean.Result;
import com.lhhh.bean.School;
import com.lhhh.service.SchoolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    /*@GetMapping("/findSchools")
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

}
