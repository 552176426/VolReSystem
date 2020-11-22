package com.lhhh.controller;

import com.lhhh.bean.Msg;
import com.lhhh.bean.Result;
import com.lhhh.service.RecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * @author: lhhh
 * @date: Created in 2020/11/10
 * @description:
 * @version:1.0
 */

@Controller
@RequestMapping("/recommend")
@Slf4j
public class RecommendController {
    @Autowired
    private RecommendService recommendService;

    @GetMapping("/findSchools")
    @ResponseBody
    public Result findSchools(@RequestParam Map<String,Object> paramsMap){

        log.info("====>paramsMap:{}",paramsMap);
        try {
            Map<String,Object> result = recommendService.findSchools(paramsMap);
            return new Result(200, Msg.GET_RECOMMEND_SCHOOL_DATA_SUCCESS,result);
        } catch (Exception e){
            e.printStackTrace();
            return new Result(500,Msg.GET_RECOMMEND_SCHOOL_DATA_FAIL,null);
        }
    }

    @GetMapping("/findSpecials")
    @ResponseBody
    public Result findSpecials(@RequestParam Map<String,Object> paramsMap){
        log.info("====>paramsMap:{}",paramsMap);
        try {
            Map<String,Object> result = recommendService.findSpecials(paramsMap);
            return new Result(200, Msg.GET_RECOMMEND_SPECIAL_DATA_SUCCESS,result);
        } catch (Exception e){
            e.printStackTrace();
            return new Result(500,Msg.GET_RECOMMEND_SPECIAL_DATA_FAIL,null);
        }
    }
}
