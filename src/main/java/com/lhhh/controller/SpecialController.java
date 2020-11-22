package com.lhhh.controller;

import com.lhhh.bean.Msg;
import com.lhhh.bean.Result;
import com.lhhh.service.SpecialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public Result findSchoolsOrderByXyhRank(){
        Map<String, Map<String, List<Map<String, Object>>>> specialListMap = specialService.findSpecials();
        return new Result(200, Msg.GET__SPECIAL_DATA_SUCCESS, specialListMap);
    }
}
