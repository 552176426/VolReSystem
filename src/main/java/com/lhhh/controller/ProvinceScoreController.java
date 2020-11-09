package com.lhhh.controller;

import com.lhhh.bean.Msg;
import com.lhhh.bean.ProvinceScore;
import com.lhhh.bean.Result;
import com.lhhh.service.ProvinceScoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author: lhhh
 * @date: Created in 2020/11/4
 * @description:
 * @version:1.0
 */
@Controller
@RequestMapping("/provinceScore")
@Slf4j
public class ProvinceScoreController {
    @Autowired
    private ProvinceScoreService provinceScoreService;

    @GetMapping("/all")
    @ResponseBody
    public Result findProvinceScoreByProvinceNameAndYear(@RequestParam("provinceName") String provinceName,
                                                         @RequestParam("year") Integer year){
        log.info("provinceName: {},year: {}",provinceName,year);
        List<ProvinceScore> ProvinceScoreList = provinceScoreService.findProvinceScoreByProvinceNameAndYear(provinceName, year);
        //分组
        Map<String, List<ProvinceScore>> map = ProvinceScoreList.stream().collect(Collectors.groupingBy(provinceScore -> provinceScore.getType()));
        List<Map> mapList = new ArrayList<>();
        for (Map.Entry<String, List<ProvinceScore>> stringListEntry : map.entrySet()) {
            HashMap<String, Object> map1 = new HashMap<>();
            String key = stringListEntry.getKey();
            List<ProvinceScore> pS = stringListEntry.getValue();
            map1.put("type",key);
            map1.put("provinceScores",pS);
            mapList.add(map1);
        }
        return new Result(200, Msg.GET_PROVINCE_SCORE_SUCCESS,mapList);
    }
    @GetMapping("/allProvince")
    @ResponseBody
    public Result getAllProvince(){
        List<String> provinceList = provinceScoreService.getAllProvince();
        return new Result(200, Msg.GET_All_YEAR_SUCCESS,provinceList);
    }

}
