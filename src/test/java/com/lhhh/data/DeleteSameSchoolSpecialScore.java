package com.lhhh.data;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

/**
 * @author: lhhh
 * @date: Created in 2020/11/14
 * @description:
 * @version:1.0
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class DeleteSameSchoolSpecialScore {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void delete(){
        String sql = "select max(id) id\n" +
                "from school_special_score \n" +
                "GROUP BY sms_id,special_id,special_name,batch_name,min,max,average,min_order\n" +
                "having count(*)>1";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        System.out.println(maps.size());
        for (Map<String, Object> map : maps) {
            int id = Integer.parseInt(map.get("id").toString());
            System.out.println(id);
            String sql1 = "delete from school_special_score where id = ?";
            jdbcTemplate.update(sql1,id);
        }
    }
}
