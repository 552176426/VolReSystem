package com.lhhh.mapper;

import com.lhhh.service.RecommendService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author: lhhh
 * @date: Created in 2020/11/12
 * @description:
 * @version:1.0
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class RecommendServiceTest {
    @Autowired
    private RecommendService recommendService;
    @Autowired
    private RecommendMapper recommendMapper;

    @Test
    public void RecommendService() throws Exception {
        /*HashMap<String, Object> map = new HashMap<>();
        map.put("provinceName","江西");
        map.put("curriculum","理科");
        map.put("batchName","本科一批");
        map.put("year",2020);
        map.put("score",600);
        recommendService.findSchools(map);*/

    }



}
