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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author: lhhh
 * @date: Created in 2020/12/16
 * @description:
 * @version:1.0
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class BaiduJobDetail {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void jobDetailToSql() {
        Arrays.stream(new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\就业").listFiles()).parallel().forEach(i -> {
            String schoolName = i.getName().split(".txt")[0];

            String content = ReptileUtils.readJsonFile("D:\\Downloads\\高校志愿推荐\\百度高考数据\\就业\\" + i.getName());
            if (!content.equals("{\"msg\":\"没有符合条件的数据\",\"data\":[],\"status\":0}")) {
                Map map = JsonPath.read(content, "$.data");
                if (map.get("industryData") != null) {
                    String sql = "select id from school where name = '" + schoolName + "'";
                    String id = jdbcTemplate.queryForObject(sql, String.class);
                    List<Map<String, String>> list = JsonPath.read(content, "$.data.industryData.list");
                    for (Map<String, String> stringMap : list) {
                        String name1 = stringMap.get("name");
                        String value = stringMap.get("value");
                        String type = "4";
                        String sql1 = "insert into school_job_detail(school_id,name,num,rate,type) values(?,?,?,?,?)";
                        jdbcTemplate.update(sql1,id,name1,value,null,type);
                    }
                }
            }

        });


    }
}
