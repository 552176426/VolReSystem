package com.lhhh.mapper;

import com.lhhh.bean.ProvinceScore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author: lhhh
 * @date: Created in 2020/10/24
 * @description:
 * @version:1.0
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class ProvinceScoreMapperTest {

    @Autowired
    private ProvinceScoreMapper provinceScoreMapper;

    @Test
    public void findProvinceScoreByProId(){
        List<ProvinceScore> scoreParagraphList = provinceScoreMapper.findProvinceScoreByProvinceName("北京");
        System.out.println(scoreParagraphList);
    }
}
