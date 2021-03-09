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
 * @date: Created in 2020/12/9
 * @description:  更新专业分数线里面的不完整专业名称
 * @version:1.0
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class UpdateCompleteSpecialName {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Test
    public void update(){
        String sql = "SELECT sss.id,sss.special_name,ssss.school_name,ssss.province_name,ssss.curriculum,ssss.year FROM `school_special_score` sss left join school_special_score_selector ssss on sss.sms_id=ssss.id where special_name like '%...%'";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        int s1 = list.size();
        int start = 0;
        for (Map<String, Object> map : list) {
            String id = map.get("id").toString();
            String special_name = map.get("special_name").toString().substring(0, 12);
            String school_name = map.get("school_name").toString();
            String province_name = map.get("province_name").toString();
            String curriculum = map.get("curriculum").toString();
            String year = map.get("year").toString();
            String sql1 = "select major_name from special_plan_selector sps left join special_plan sp on sps.id=sp.sps_id  where  sps.school_name='"+school_name+"' and sps.province_name='"+province_name+"' and sps.curriculum='"+curriculum+"' and sps.year = '"+year+"' and  major_name like '%"+special_name+"%'";
            List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql1);
            if (maps.size()!=0){
                start++;
                String major_name = maps.get(0).get("major_name").toString();
                System.out.println(map.get("special_name")+" : "+major_name);
                String sql2 ="update school_special_score set special_name=? where id=?";
                jdbcTemplate.update(sql2,major_name,id);
            } else {
                System.out.println(map.get("special_name")+" : "+ "[]");
            }
        }
        System.out.println(s1+":"+start);
    }
}
