package com.lhhh.data;

import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * @author: lhhh
 * @date: Created in 2020/10/10
 * @description:
 * @version:1.0
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class ProvinceScoreData {
    private static int[] provinceIds = {11, 12, 13, 14, 15, 21,22, 23, 31, 32, 33, 34, 35, 36, 37,41, 42, 43, 44, 45, 46, 50, 51, 52, 53, 54, 61, 62, 63, 64, 65};
    private static int[] years = {2020,2019,2018,2017,2016,2015,2014};

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void provinceScoreToSql(){
        for (int provinceId : provinceIds) {
            String url1 = "D:\\Downloads\\高校志愿推荐\\provinceScore\\";
            url1 += provinceId;
            for (int year : years) {
                String url2 = url1;
                url2 += "\\"+year+"\\score.txt";
                System.out.println(url2);
                File file = new File(url2);
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new FileReader(file));
                    StringBuffer sb = new StringBuffer();
                    String s;
                    while ((s=br.readLine())!=null){
                        sb.append(s);
                    }
                    String json = sb.toString();
                    System.out.println(json);
                    List<Map<Object, Object>> items = JsonPath.read(json, "$.data.item[*]");
                    for (Map<Object, Object> item : items) {
                        String batchName = item.get("local_batch_name").toString();
                        String provinceName = item.get("local_province_name").toString();
                        String type = item.get("local_type_name").toString();
                        int score = Integer.valueOf(item.get("average").toString());
                        String sql = "insert into province_score(province_name,batch_name,year,score,type) values(?,?,?,?,?)";
                        jdbcTemplate.update(sql,provinceName,batchName,year,score,type);
                        System.out.println(":省"+provinceName+":批次"+batchName+":年份"+year+":分数"+score+":"+type);
                    }
                    br.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void test(){


    }
}
