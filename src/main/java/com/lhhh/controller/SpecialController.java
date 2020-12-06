package com.lhhh.controller;

import com.lhhh.bean.Msg;
import com.lhhh.bean.Result;
import com.lhhh.service.SpecialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author: lhhh
 * @date: Created in 2020/11/18
 * @description:
 * @version:1.0
 */
@Controller
@RequestMapping("/special")
@Slf4j
public class SpecialController {
    @Autowired
    private SpecialService specialService;

    @GetMapping("/findSpecials")
    @ResponseBody
    public Result findSpecials(){
        Map<String, Map<String, List<Map<String, Object>>>> specialListMap = specialService.findSpecials();
        return new Result(200, Msg.GET__SPECIAL_DATA_SUCCESS, specialListMap);
    }

    @GetMapping("/findOne")
    @ResponseBody
    public Result findSpecial(@RequestParam Integer id){
        try {
            Map<String, Object> specialMap = specialService.findSpecialOne(id);
            return new Result(200, Msg.GET__SPECIAL_DETAIL_DATA_SUCCESS, specialMap);
        } catch (Exception e){
            e.printStackTrace();
            return new Result(500, Msg.GET__SPECIAL_DETAIL_DATA_FAIL, null);
        }
    }

    @GetMapping("/findSchool")
    @ResponseBody
    public Result findSchool(@RequestParam Map map){
        try {
            List<Map<String, Object>> schoolList = specialService.findSchoolBySpId(map);
            return new Result(200, Msg.GET__SPECIAL_SCHOOL_DATA_SUCCESS, schoolList);
        } catch (Exception e){
            e.printStackTrace();
            return new Result(500, Msg.GET__SPECIAL_SCHOOL_DATA_FAIL, null);
        }
    }



}
