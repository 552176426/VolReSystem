package com.lhhh.data;

import com.alibaba.fastjson.JSONPath;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.lhhh.utils.ReptileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.*;
import java.util.ArrayList;
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
        String sql = "select * from school_special";
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

    /**
     * 国家特色和省级特色专业
     */
    @Test
    public void schoolSpecialToSql4(){
        for (File file : new File("D:\\Downloads\\高校志愿推荐\\all").listFiles()) {
            for (File listFile : file.listFiles()) {
                if (listFile.getName().equals("pc_special.txt")){
                    System.out.println(file.getName());
                    String json = ReptileUtils.readJsonFile(listFile.getAbsolutePath());
                    List<Map<String, String>> mapList = null;
                    try {
                        if (!JSONPath.read(json,"$.data.special_detail.1").equals("")){
                            mapList = (List) JSONPath.read(json,"$.data.special_detail.1");
                            System.out.println(mapList);
                            ArrayList<Object[]> objects = new ArrayList<>();
                            for (Map<String, String> stringStringMap : mapList) {
                                String id = stringStringMap.get("id");
                                String nation_feature = stringStringMap.get("nation_feature");
                                String province_feature = stringStringMap.get("province_feature");
                                System.out.println(id+":"+nation_feature+":"+province_feature);
                                objects.add(new Object[]{nation_feature,province_feature,id});
                            }
                            String sql = "update school_special set nation_feature=?,province_feature=? where school_sp_id=?";
                            jdbcTemplate.batchUpdate(sql,objects);
                        }
                        if (!JSONPath.read(json,"$.data.special_detail.2").equals("")){
                            mapList = (List) JSONPath.read(json,"$.data.special_detail.2");
                            System.out.println(mapList);
                            ArrayList<Object[]> objects = new ArrayList<>();
                            for (Map<String, String> stringStringMap : mapList) {
                                String id = stringStringMap.get("id");
                                String nation_feature = stringStringMap.get("nation_feature");
                                String province_feature = stringStringMap.get("province_feature");
                                System.out.println(id+":"+nation_feature+":"+province_feature);
                                objects.add(new Object[]{nation_feature,province_feature,id});
                            }
                            String sql = "update school_special set nation_feature=?,province_feature=? where school_sp_id=?";
                            jdbcTemplate.batchUpdate(sql,objects);
                        }



                    } catch (Exception e){

                    }



                }
            }
        }
    }

    @Test
    public void update_SSSS_special_name(){
        String sql = "SELECT\n" +
                "\tsss.id,\n" +
                "\tschool_name,\n" +
                "\tprovince_name,\n" +
                "\tcurriculum,\n" +
                "\tyear,\n" +
                "\tspecial_name,\n" +
                "\tbatch_name,\n" +
                "\tmin,\n" +
                "\tmax,\n" +
                "\taverage,\n" +
                "\tmin_order\n" +
                "FROM\n" +
                "\tschool_special_score_selector ssss \n" +
                "\tright JOIN school_special_score sss on ssss.id=sss.sms_id\n" +
                "WHERE\n" +
                "   year=2019 and\n" +
                "-- \tand min is not null and min_order is not null\n" +
                "\tsss.special_name like \"%...%\"\n" +
                "\torder BY id";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        int count1 = 0;
        int count2 = 0;
        int count = 0;
        int count3 = 0;
        for (Map<String, Object> map : maps) {
            String special_name = map.get("special_name").toString().substring(0, 12);
            String school_name = map.get("school_name").toString();
            String province_name = map.get("province_name").toString();
            String curriculum = map.get("curriculum").toString();
            int id = Integer.parseInt(map.get("id").toString());

            String sql1 = "SELECT special_name FROM school_special_score_selector ssss RIGHT JOIN school_special_score sss ON ssss.id = sss.sms_id  WHERE"+
                    " YEAR =2017 " +
                    " AND school_name = "+  "'"+ school_name+ "'"+
                    " AND province_name = "+"'"+province_name+"'" +
                    " AND curriculum = "+"'"+curriculum+" '" +
                    " and special_name like '%"+special_name+"%'";
            try {
                String speName = jdbcTemplate.queryForObject(sql1, String.class);
                if (speName.substring(speName.length()-1,speName.length()).equals("）")){
                    String sql2 = "update school_special_score set special_name="+"'"+speName+"'"+"where id = "+id;
//                jdbcTemplate.update(sql2);
                    count++;

                } else {
                    System.out.println(special_name+":"+speName);
                    count3++;
                }
            } catch (EmptyResultDataAccessException e){
                count1++;
            } catch (IncorrectResultSizeDataAccessException i){
                count2++;
            }
        }
        System.out.println("empty:"+count1);
        System.out.println("more:"+count2);
        System.out.println("right:"+count);
        System.out.println("other:"+count3);

    }

}
