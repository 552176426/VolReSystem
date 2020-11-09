package com.lhhh.controller;

import com.lhhh.bean.Msg;
import com.lhhh.bean.ProvinceScore;
import com.lhhh.bean.Result;
import com.lhhh.mapper.ProvinceScoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author: lhhh
 * @date: Created in 2020/11/1
 * @description:
 * @version:1.0
 */
@Controller
@RequestMapping("/test")
public class TestController {
    @Autowired
    private ProvinceScoreMapper provinceScoreMapper;

    @GetMapping("/all")
    @ResponseBody
    public Result test(){
        List<ProvinceScore> provinceScoreList = provinceScoreMapper.findProvinceScoreList();
        return new Result(200, Msg.SUCCESS,provinceScoreList);
    }
}
