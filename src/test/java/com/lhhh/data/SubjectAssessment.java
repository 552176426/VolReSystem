package com.lhhh.data;

import com.jayway.jsonpath.JsonPath;
import com.lhhh.utils.ReptileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author: lhhh
 * @date: Created in 2020/10/31
 * @description:
 * @version:1.0
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class SubjectAssessment {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void SubjectAssessmentToSql(){
        File file = new File("D:\\Downloads\\高校志愿推荐\\学科评估");
        for (File listFile : file.listFiles()) {
            String json = ReptileUtils.readJsonFile(listFile.getAbsolutePath());
            String name = JsonPath.read(json, "$.data.name").toString();
            String code = JsonPath.read(json, "$.data.code").toString();
            List<Map> mapList=JsonPath.read(json, "$.data.list[*]");
            System.out.println(listFile.getName()+"----------------");
            for (Map map : mapList) {
                String schoolname = map.get("schoolname").toString();
                String subranking = map.get("subranking").toString();
                System.out.println(code+":"+name+":"+schoolname+":"+subranking);
            }
        }
    }
}
