package com.lhhh.data;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.lhhh.utils.ReptileUtils;
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
public class SchoolSpecialData {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void schoolSpecialToSql1(){
        int num  = 0;
        for (int i = 1; i <= 5100; i++) {
            String url = "D:\\Downloads\\高校志愿推荐\\schoolSpecial\\page_"+i+".txt";
            File file = new File(url);
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(file));
                String s;
                StringBuffer sb = new StringBuffer();
                while ((s=br.readLine())!=null){
                    sb.append(s);
                }
                String content = sb.toString();
                List<Map<Object, Object>> items = JsonPath.read(content, "$.data.item[*]");
                for (Map<Object, Object> item : items) {
                    String id =  item.get("id").toString();
                    id = id.substring(15);
                    Integer school_id =  Integer.valueOf(item.get("school_id").toString());
                    Integer special_id =  Integer.valueOf(item.get("special_id").toString());
                    Integer is_important =  Integer.valueOf(item.get("is_important").toString());
                    Integer level1 =  Integer.valueOf(item.get("level1").toString());
                    String special_name = item.get("spname").toString();
                    String school_name = item.get("name").toString();
                    System.out.println(id+":"+school_id+":"+school_name+":"+special_id+":"+special_name);

                    String sql = "replace into school_special(school_sp_id,school_id,special_id,is_important,school_name,special_name,level1) values" +
                            "(?,?,?,?,?,?,?)";
                    jdbcTemplate.update(sql,id,school_id,special_id,is_important,school_name,special_name,level1);

                    /*String sql1 = "select * from special where special_id=?";
                    Map<String, Object> map = null;
                    try {
                        map = jdbcTemplate.queryForMap(sql1, special_id);
                    } catch (EmptyResultDataAccessException e){
                        num++;
                        System.out.println(name+":"+spname+":"+special_id+":"+map);
                    }*/
                }
                br.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(num);
    }

    @Test
    public void schoolSpecialToSql2(){
        String sql = "select * from school_special where school_sp_id >=1034725";
        List<Map<String, Object>> items = jdbcTemplate.queryForList(sql);
        BufferedWriter bw = null;
        for (Map<String, Object> item : items) {
            Integer school_id = (Integer) item.get("school_id");
            Integer school_sp_id = (Integer) item.get("school_sp_id");
            String url = "https://static-data.eol.cn/www/2.0/school/"+school_id+"/special/"+school_sp_id+".json";
            System.out.print("school_id_"+school_id+"_school_sp_id_"+school_sp_id+"正在读取....");
            String content = ReptileUtils.getContent(url);
            File file = new File("D:\\Downloads\\高校志愿推荐\\schoolSpecialContent\\school_" + school_id + "_school_sp_id_" + school_sp_id + ".txt");
            try {
                bw = new BufferedWriter(new FileWriter(file));
                System.out.print("写入中....");
                bw.write(content);
                System.out.println("已完成");
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void schoolSpecialToSql3(){
        String sql = "select * from school_special where school_sp_id>=89849";
        List<Map<String, Object>> items = jdbcTemplate.queryForList(sql);
        BufferedReader br = null;
        String content="";
        for (Map<String, Object> item : items) {
            Integer school_id = (Integer) item.get("school_id");
            Integer school_sp_id = (Integer) item.get("school_sp_id");
            System.out.println("school_id_"+school_id+"_school_sp_id_"+school_sp_id+"正在读取....");
            File file = new File("D:\\Downloads\\高校志愿推荐\\schoolSpecialContent\\school_" + school_id + "_school_sp_id_" + school_sp_id + ".txt");
            try {
                br = new BufferedReader(new FileReader(file));
                String s;
                StringBuffer sb = new StringBuffer();
                while ((s= br.readLine())!=null){
                    sb.append(s);
                }
                String json = sb.toString();
                content = JsonPath.read(json, "$.data.content").toString();
                String sql1 =  "update school_special set content=? where school_sp_id=?";
                jdbcTemplate.update(sql1,content,school_sp_id);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException illegalArgumentException){
                System.out.println("---------school_sp_id_"+school_sp_id+"的content为null-----------");
            } catch (PathNotFoundException pathNotFoundException){
                System.out.println("---------school_sp_id_"+school_sp_id+"的无content-----------");
            }
        }
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
