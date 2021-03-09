package com.lhhh.controller;

import com.lhhh.bean.Msg;
import com.lhhh.bean.Result;
import com.lhhh.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author: lhhh
 * @date: Created in 2021/1/5
 * @description:
 * @version:1.0
 */
@Controller
@RequestMapping("/search")
@Slf4j
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/school")
    @ResponseBody
    public Result searchSchool(@RequestParam String str) throws IOException {
        List<Map<String, Object>> schoolList = searchService.searchSchool(str);
        return new Result(200, Msg.GET_SEARCH_SUCCESS, schoolList);
    }

    @GetMapping("/all")
    @ResponseBody
    public Result searchAll(@RequestParam String str) {
        try {
            Map<String, List<Map<String, Object>>> maps = searchService.searchAll(str);
            return new Result(200,Msg.GET_SEARCH_SUCCESS,maps);
        } catch (Exception e){
            e.printStackTrace();
            return new Result(500,Msg.GET_SEARCH_FAIL,null);
        }
    }

    @GetMapping("/rank")
    @ResponseBody
    public Result searchRank(@RequestParam Map map) {
        log.info("=============>map:{}",map);
        try {
            Map<String, List<Map>> resultMap = searchService.searchRank(map);
            return new Result(200,Msg.GET_SEARCH_SUCCESS,resultMap);
        } catch (Exception e){
            e.printStackTrace();
            return new Result(500,Msg.GET_SEARCH_FAIL,null);
        }
    }

    @GetMapping("/rankByType")
    @ResponseBody
    public Result searchRankByType(@RequestParam Map map) {
        log.info("=============>map:{}",map);
        try {
            List<Map> resultMap = searchService.searchRankByType(map);
            return new Result(200,Msg.GET_SEARCH_SUCCESS,resultMap);
        } catch (Exception e){
            e.printStackTrace();
            return new Result(500,Msg.GET_SEARCH_FAIL,null);
        }
    }
}
