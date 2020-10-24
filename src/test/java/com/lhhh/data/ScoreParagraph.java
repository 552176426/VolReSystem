package com.lhhh.data;

import com.lhhh.utils.OfficeUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.List;

/**
 * @author: lhhh
 * @date: Created in 2020/10/15
 * @description:
 * @version:1.0
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class ScoreParagraph {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void scoreParagraphToSql(){
        File file = new File("D:\\Downloads\\高校志愿推荐\\2020一分一段表");
        File[] files = file.listFiles();
        for (File file1 : files) {
            File[] files1 = file1.listFiles();
            for (File file2 : files1) {
                String s = file2.getName().split(".csv")[0];
                String[] split = s.split("-");
                String province = split[0];
                String type = split[1];
                System.out.println("----------"+province+":"+type+"----------");
                String csv = file2.getAbsolutePath();
                List<String> strings = OfficeUtils.readCsv(csv);
                System.out.println(strings.get(0));
                String sql = "insert into score_paragraph(province,year,type,score,number) values(?,?,?,?,?)";
                for (int i = 1; i < strings.size(); i++) {
                    String line = strings.get(i);
                    String[] split1 = line.split(",");
                    String score = split1[0];
                    String number = split1[1];
                    jdbcTemplate.update(sql,province,2020,type,score,number);
                }
            }
        }
    }


    @Test
    public void test(){
        String sql =  "select name from school";

    }

}
