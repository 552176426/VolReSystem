package com.lhhh.data;

import com.jayway.jsonpath.JsonPath;
import com.lhhh.utils.ReptileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: lhhh
 * @date: Created in 2020/12/7
 * @description:
 * @version:1.0
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class Plan {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Test
    public void downPlanSelector() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_info");
        List<String> schoolNames = new ArrayList<>();
        for (File listFile : file.listFiles()) {
            schoolNames.add(listFile.getName().split(".txt")[0]);
        }
        schoolNames.parallelStream().forEach(i -> {
            String url = "https://gaokao.baidu.com/gaokao/gktool/getprovincefilter?ajax=1&query=" + i;
            String content = ReptileUtils.getContent(url);
            ReptileUtils.downLoad(content, "D:\\Downloads\\高校志愿推荐\\百度高考数据\\招生计划selector\\" + i + ".txt");
        });
    }

    @Test
    public void planSelectorToSql() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\招生计划selector");
        for (File listFile : file.listFiles()) {
            try {
                String schoolName = listFile.getName().split(".txt")[0];
                String s = ReptileUtils.readJsonFile(listFile.getAbsolutePath());
                if (JsonPath.read(s, "$.data").equals("")) {
                    System.out.println(s);
                    continue;
                }
                List<Map> list = JsonPath.read(s, "$.data.province");
                List<Object[]> arrayList = new ArrayList<>();
                for (Map map : list) {
                    String province = map.get("text").toString();
                    List<Map> categoryList = (List<Map>) map.get("category");
                    for (Map map1 : categoryList) {
                        String curriculum = map1.get("text").toString();
                        List<Map> yearList = (List<Map>) map1.get("year");
                        for (Map map2 : yearList) {
                            String year = map2.get("text").toString();
                            Object[] objects = new Object[4];
                            objects[0] = schoolName;
                            objects[1] = province;
                            objects[2] = curriculum;
                            objects[3] = year;
                            arrayList.add(objects);
//                        System.out.println(province + ":" + curriculum + ":" + year);
                        }
                    }
                }
//        System.out.println(arrayList.size());
                String sql = "insert into special_plan_selector(school_name,province_name,curriculum,year) values(?,?,?,?)";
                jdbcTemplate.batchUpdate(sql, arrayList);
            } catch (Exception e) {
                System.err.println(listFile.getName());
            }
        }

    }

    @Test
    public void downPlanSpecial() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("plan.txt"), true));
        String sql = "select * from special_plan_selector";
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(sql);
        AtomicInteger atomicInteger = new AtomicInteger();
        mapList.parallelStream().forEach(i -> {
            System.out.println(atomicInteger.incrementAndGet());
            i.put("pn", 0);
            i.put("rn", 1000);
            try {
                recursionDown(i, bw);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        bw.close();
    }

    public void recursionDown(Map filter, BufferedWriter bw) throws IOException {
        String provinceName = filter.get("province_name").toString();
        String curriculum = filter.get("curriculum").toString();
        String year = filter.get("year").toString();
        String id = filter.get("id").toString();
        int pn = Integer.parseInt(filter.get("pn").toString());
        int rn = Integer.parseInt(filter.get("rn").toString());
        String schoolName = filter.get("school_name").toString();
        String url = "https://gaokao.baidu.com/gaokao/gkschool/getrecruitingscheme?ajax=1&province=" + provinceName + "&category=" + curriculum + "&year=" + year + "&rn=" + rn + "&query=" + schoolName + "&pn=" + pn;
        try {
            if (new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\招生计划1\\" + schoolName + "_" + filter.get("id") + ".txt").exists()) {
                return;
            }
            String content = ReptileUtils.getContent(url);
            if (content.equals("{\"msg\":\"success\",\"status\":0}")) {
                return;
            } else if (content.equals("{\"msg\":\"success\",\"data\":{\"authorized\":{\"entry\":\"经高校确认联合发布\",\"feedback_url\":\"http://ufosdk.baidu.com/ufosdk/postview/IXT854OBKeave6nMTeU5iA%3D%3D/240507\",\"icon\":\"http://static.open.baidu.com/media/ch2/png/1586507294882.png\",\"text\":\"数据由各高校招生办提供，经中国教育在线（www.eol.cn）核实整理后，在百度公开展示。因数据需经整理核实，存在一定的时间延迟，敬请谅解。\",\"title\":\"数据来源说明\",\"type\":\"1\",\"feedback_text\":\"意见反馈\"}},\"status\":0}")) {
                return;
            } else {
                Map map = JsonPath.read(content, "$.data");
                List list = (List) map.get("list");
                int disp_num = Integer.parseInt(map.get("disp_num").toString());
                System.out.println(list.size() == disp_num);
                ReptileUtils.downLoad(content, "D:\\Downloads\\高校志愿推荐\\百度高考数据\\招生计划1\\" + schoolName + "_" + filter.get("id") + ".txt");
            }
        } catch (Exception p) {
            bw.write(id + ":" + schoolName + ":" + provinceName + ":" + curriculum + ":" + year + "\n");
            bw.flush();
            return;
        }
    }


    @Test
    public void downPlanSpecial1() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File("plan.txt")));
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("plan1.txt"), true));
        String s;
        List<String> list = new ArrayList<>();
        while ((s = br.readLine()) != null) {
            list.add(s);
        }
        AtomicInteger i = new AtomicInteger();
        list.parallelStream().forEach(str -> {
            i.getAndIncrement();
            System.out.println(i);
            String[] split = str.split(":");
            Map<String, Object> filter = new HashMap<>();
            filter.put("id", split[0]);
            filter.put("school_name", split[1]);
            filter.put("province_name", split[2]);
            filter.put("curriculum", split[3]);
            filter.put("year", split[4]);
            filter.put("pn", 0);
            filter.put("rn", 1000);
            try {
                recursionDown1(filter, bw);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        bw.close();
        br.close();
    }

    public void recursionDown1(Map filter, BufferedWriter bw) throws IOException {
        String provinceName = filter.get("province_name").toString();
        String curriculum = filter.get("curriculum").toString();
        String year = filter.get("year").toString();
        int pn = Integer.parseInt(filter.get("pn").toString());
        int rn = Integer.parseInt(filter.get("rn").toString());
        String id = filter.get("id").toString();
        String schoolName = filter.get("school_name").toString();
        String url = "https://gaokao.baidu.com/gaokao/gkschool/getrecruitingscheme?ajax=1&province=" + provinceName + "&category=" + curriculum + "&year=" + year + "&rn=" + rn + "&query=" + schoolName + "&pn=" + pn;
        try {
            String content = ReptileUtils.getContent(url);
            System.out.println(content);
            if (content.equals("{\"msg\":\"success\",\"status\":0}")) {
                return;
            } else if (content.equals("{\"msg\":\"success\",\"data\":{\"authorized\":{\"entry\":\"经高校确认联合发布\",\"feedback_url\":\"http://ufosdk.baidu.com/ufosdk/postview/IXT854OBKeave6nMTeU5iA%3D%3D/240507\",\"icon\":\"http://static.open.baidu.com/media/ch2/png/1586507294882.png\",\"text\":\"数据由各高校招生办提供，经中国教育在线（www.eol.cn）核实整理后，在百度公开展示。因数据需经整理核实，存在一定的时间延迟，敬请谅解。\",\"title\":\"数据来源说明\",\"type\":\"1\",\"feedback_text\":\"意见反馈\"}},\"status\":0}")) {
                return;
            } else {
                Map map = JsonPath.read(content, "$.data");
                int disp_num = Integer.parseInt(map.get("disp_num").toString());
                ReptileUtils.downLoad(content, "D:\\Downloads\\高校志愿推荐\\百度高考数据\\招生计划2\\" + schoolName + "_" + filter.get("id") + "_" + pn + ".txt");
                if (disp_num <= (pn + 10)) {
                    return;
                }
                filter.put("pn", pn + 10);
                recursionDown(filter, bw);
            }
        } catch (Exception e) {
            bw.write(id + ":" + schoolName + ":" + provinceName + ":" + curriculum + ":" + year + "\n");
            bw.flush();
        }


    }


    @Test
    public void planToSql() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\招生计划1");
        AtomicInteger id = new AtomicInteger();
        Arrays.stream(file.listFiles()).parallel().forEach(i -> {
            String[] strings = i.getName().split(".txt")[0].split("_");
            String schoolName = strings[0];
            String sps_id = strings[1];
            String json = ReptileUtils.readJsonFile(i.getAbsolutePath());
            List<Map> list = JsonPath.read(json, "$.data.list");
            List<Object[]> objects = new ArrayList<>();
            list.forEach(plan->{
                Object[] arr = new Object[8];
                String batch_name = plan.get("batch_name").toString();
                String major_name = plan.get("major_name").toString();
                String category = plan.get("category").toString();
                int enroll_num = Integer.parseInt(plan.get("enroll_num").toString());
                int year = Integer.parseInt(plan.get("year").toString());
                arr[0] = id.incrementAndGet();;
                System.out.println(id);
                arr[1] = schoolName;
                arr[2] = sps_id;
                arr[3] = batch_name;
                arr[4] = major_name;
                arr[5] = enroll_num;
                arr[6] = category;
                arr[7] = year;
                objects.add(arr);
            });
            String sql =  "insert into special_plan values(?,?,?,?,?,?,?,?)";
           /* objects.forEach(arr->{
                System.out.print(Arrays.toString(arr) +" ");
                System.out.println();
            });*/
            jdbcTemplate.batchUpdate(sql,objects);
        });
    }
}
