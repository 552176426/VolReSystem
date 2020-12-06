package com.lhhh.data;

import com.alibaba.fastjson.JSONPath;
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
 * @date: Created in 2020/12/3
 * @description:
 * @version:1.0
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class SpecialSalary {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void downLoadSalary(){
        String sql = "select special_id,name from special";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        System.out.println(maps.size());

        maps.parallelStream().forEach(i->{
            String name = i.get("name").toString();
            String id = i.get("special_id").toString();
            String url = "https://gaokao.baidu.com/gaokao/gkmajor/prospects?ajax=1&query="+name;
            String content = ReptileUtils.getContent(url);
            ReptileUtils.downLoad(content, "D:\\Downloads\\高校志愿推荐\\百度高考数据\\专业薪酬\\"+id+"_"+name+".txt");
        });
    }

    @Test
    public void salaryToSql(){
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\专业薪酬");
        for (File listFile : file.listFiles()) {
            String json = ReptileUtils.readJsonFile(listFile.getAbsolutePath());
            List list = (List) JSONPath.read(json, "$.data.salary.data");
            if (list.size()!=0){
                Object year0 = JSONPath.read(json, "$.data.salary.data[0].value[0].value");
                Object year2=  JSONPath.read(json, "$.data.salary.data[1].value[0].value");
                Object year5 = JSONPath.read(json, "$.data.salary.data[2].value[0].value");
                Object year10 = JSONPath.read(json, "$.data.salary.data[3].value[0].value");
                String level = JSONPath.read(json, "$.data.salary.salaryLevel").toString();
                double y0 = Double.parseDouble(year0.toString());
                double y2 = Double.parseDouble(year2.toString());
                double y5 = Double.parseDouble(year5.toString());
                double y10 = Double.parseDouble(year10.toString());
                String sql = "insert into special_salary(special_id,0_year,2_year,5_year,10_year,level) values(?,?,?,?,?,?)";
                System.out.println(y0+":"+y2+":"+y5+":"+y10);
                String id = listFile.getName().split("_")[0];
                jdbcTemplate.update(sql,id,y0,y2,y5,y10,level);

            }
        }
    }

}
