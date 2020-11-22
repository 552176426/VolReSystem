package com.lhhh.controller;

import com.lhhh.bean.Msg;
import com.lhhh.bean.Result;
import com.lhhh.bean.ScoreParagraph;
import com.lhhh.service.ScoreParagraphService;
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
 * @date: Created in 2020/11/10
 * @description:
 * @version:1.0
 */
@Controller
@RequestMapping("/scoreParagraph")
@Slf4j
public class ScoreParagraphController {
    @Autowired
    private ScoreParagraphService scoreParagraphService;

    @GetMapping("/types")
    @ResponseBody
    public Result findTypesByProvinceNameAndYear(@RequestParam("chooseProvince")String provinceName,
                                                 @RequestParam("chooseYear")Integer year){
        List<String> types = scoreParagraphService.findTypesByProvinceNameAndYear(provinceName, year);
        return new Result(200, Msg.GET_SCORE_PARAGRAPH_DATA_SUCCESS, types);
    }

    @GetMapping("/years")
    @ResponseBody
    public Result findTypesByProvinceNameAndYear(@RequestParam("chooseProvince")String provinceName){
        List<Integer> years = scoreParagraphService.findYearsByProvinceName(provinceName);
        return new Result(200, Msg.GET_SCORE_PARAGRAPH_DATA_SUCCESS, years);
    }

    @GetMapping("/scoreParagraphs")
    @ResponseBody
    public Result findScoreParagraphs(@RequestParam Map<String,Object> chooseMap){
        log.info("====>chooseMap:{}",chooseMap);
        List<ScoreParagraph> scoreParagraphs = scoreParagraphService.findScoreParagraphs(chooseMap);
        return new Result(200,Msg.GET_SCORE_PARAGRAPH_DATA_SUCCESS,scoreParagraphs);
    }


}
