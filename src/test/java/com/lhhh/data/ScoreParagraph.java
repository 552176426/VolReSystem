package com.lhhh.data;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.*;

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
        File file = new File("D:\\Downloads\\高校志愿推荐\\近三年一分一段表");
        for (File listFile : file.listFiles()) {
            String province = listFile.getName();
            for (File f : listFile.listFiles()) {
                String[] strings = f.getName().split("-");
                Integer year = Integer.valueOf(strings[0]);
                String type = strings[1].split(".txt")[0];
                try {
                    BufferedReader br = new BufferedReader(new FileReader(f));
                    String s;
                    int i = 1;
                    int score=-1;
                    int score_number=-1;
                    int number=-1;
                    while ((s=br.readLine())!=null){
                        if (i%3==1){
                            score = Integer.valueOf(s);
                        } else if (i%3==2){
                            score_number = Integer.valueOf(s);
                        } else if (i%3==0){
                            number=Integer.valueOf(s);
                            String sql = "insert into score_paragraph(province,year,type,score,score_number,number) values(?,?,?,?,?,?)";
                            jdbcTemplate.update(sql,province,year,type,score,score_number,number);
                            System.out.println(province+":"+year+":"+type+":"+score+":"+score_number+":"+number);
                        }
                        i++;
                    }
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
        String sql =  "select name from school";

    }

}
