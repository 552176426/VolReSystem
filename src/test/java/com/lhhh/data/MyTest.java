package com.lhhh.data;

import com.jayway.jsonpath.JsonPath;
import com.lhhh.reptile.DownLoadMajorScoreThread;
import com.lhhh.reptile.DownLoadSchoolScoreThread;
import com.lhhh.utils.ReptileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author: lhhh
 * @date: Created in 2020/10/15
 * @description:
 * @version:1.0
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class MyTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Test
    public void downLoadBaiDuSchool() {
        String sql = "select name from school";
        int visit = 0;
        int notVisit = 0;
        List<String> names = jdbcTemplate.queryForList(sql, String.class);
        for (String name : names) {
            String schoolUrl = "https://gaokao.baidu.com/gaokao/gkschool/overview?ajax=1&query=" + name + "&tab=intro";
            System.out.println(schoolUrl);
            String content = ReptileUtils.getContent(schoolUrl);
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_info\\" + name + ".txt")));
                bw.write(content);
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<Map> lists = JsonPath.read(content, "$.data.[*]");
            if (lists.size() > 2) {
                visit++;
            } else {
                notVisit++;
                System.out.println(name);
            }
        }
        System.out.println("可访问:" + visit + ",不可访问:" + notVisit);
    }

    @Test
    public void noVisit() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_info");
        List<String> names = new ArrayList<>();
        for (File f : file.listFiles()) {
            String s = ReptileUtils.readJsonFile(f.getAbsolutePath());
            List<Map> lists = JsonPath.read(s, "$.data.[*]");
            if (lists.size() <= 2) {
                names.add(f.getName().split(".txt")[0]);
                System.out.println(lists);
            }
        }
        System.out.println("----------------");
        System.out.println(names);

    }

    @Test
    public void downLoadSelectData() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_info");
        File[] files = file.listFiles();
        for (int i = 256; i < files.length; i++) {
            String name = files[i].getName().split(".txt")[0];
            String url = "https://gaokao.baidu.com/gaokao/gkschool/score?ajax=1&query=" + name + "&province=广东&curriculum=&batchName=";
            String content = ReptileUtils.getContent(url);
            System.out.println(content);
            ReptileUtils.downLoad(content, "D:\\Downloads\\高校志愿推荐\\百度高考数据\\select_data\\" + name + ".txt");
            long length = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\select_data\\" + name + ".txt").length();
            System.out.println(i + ":" + name + ":" + "写入完成,容量为:" + length);
        }

    }


    @Test
    public void schoolScoreSelectorToSql() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\select_data");
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            String json = ReptileUtils.readJsonFile(files[i].getAbsolutePath());
            String schoolName = files[i].getName().split(".txt")[0];
            //有数据
            if (JsonPath.read(json, "$.msg").equals("success")) {
                System.out.println(schoolName);
                for (int j = 0; j < 31; j++) {
                    Map provinceItem = JsonPath.read(json, "$.data.school.selectData[0].selectorList[" + j + "]");
                    List<Map> curriculumItem = JsonPath.read(json, "$.data.school.selectData[1].selectorList[" + j + "]");
                    List<Map> batchNameItem = JsonPath.read(json, "$.data.school.selectData[2].selectorList[" + j + "]");
                    String province = provinceItem.get("text").toString();
                    for (Map curriculumMap : curriculumItem) {
                        String curriculum = curriculumMap.get("text").toString();
                        for (Map batchNameMap : batchNameItem) {
                            String batchName = batchNameMap.get("text").toString();
                            System.out.println(province + ":" + curriculum + ":" + batchName);
                            String sql = "insert into school_score_selector(school_name,province_name,curriculum,batch_name) values(?,?,?,?)";
                            jdbcTemplate.update(sql, schoolName, province, curriculum, batchName);
                        }
                    }
                }
            } else {
                System.err.println(files[i].getName());
                //无数据则记录该学校到txt
                try {
                    FileWriter fw = new FileWriter(new File("nodata.txt"), true);
                    fw.write(files[i].getName());
                    fw.write("\n");
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("------------------------------------");
        }


    }

    @Test
    public void schoolMajorSelectorToSql() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\select_data");
        File[] files = file.listFiles();
        int num = 0;
        for (int i = 0; i < files.length; i++) {
            String json = ReptileUtils.readJsonFile(files[i].getAbsolutePath());
            String schoolName = files[i].getName().split(".txt")[0];
            System.out.println(schoolName);

            if (JsonPath.read(json, "$.msg").equals("success")) {
                //有数据
                List majorSelectList = JsonPath.read(json, "$.data.major.selectData.[*]");
                //selectData:[] 可能为空
                int size = majorSelectList.size();
                if (size != 0) {
                    for (int j = 0; j < 31; j++) {
                        Map provinceItem = JsonPath.read(json, "$.data.major.selectData[0].selectorList[" + j + "]");
                        List<Map> curriculumItem = JsonPath.read(json, "$.data.major.selectData[1].selectorList[" + j + "]");
                        List<Map> yearsItem = JsonPath.read(json, "$.data.major.selectData[2].selectorList[" + j + "]");
                        String province = provinceItem.get("text").toString();
                        for (Map curriculumMap : curriculumItem) {
                            String curriculum = curriculumMap.get("text").toString();
                            for (Map yearsMap : yearsItem) {
                                Integer year = Integer.valueOf(yearsMap.get("text").toString());
                                System.out.println(province + ":" + curriculum + ":" + year);
                                String sql = "insert into school_major_selector(school_name,province_name,curriculum,year) values(?,?,?,?)";
                                jdbcTemplate.update(sql, schoolName, province, curriculum, year);
                                num++;
                            }
                        }
                    }
                } else {
                    System.err.println(files[i].getName());
                    //无数据则记录该学校到txt
                    try {
                        FileWriter fw = new FileWriter(new File("noMajor.txt"), true);
                        fw.write(files[i].getName());
                        fw.write("\n");
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                System.err.println(files[i].getName());
                //无数据则记录该学校到txt
                try {
                    FileWriter fw = new FileWriter(new File("noMajor.txt"), true);
                    fw.write(files[i].getName());
                    fw.write("\n");
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("------------------------------------");
        }
        System.out.println(num);


    }

    @Test
    public void downLoadSchoolScore() {
        String sql = "select * from school_score_selector";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        //System.out.println(maps.size()); //285733
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 12; i++) {
            pool.execute(new DownLoadSchoolScoreThread(i * 25000, i * 25000 + 25000, maps));
        }
        pool.shutdown();

        while (true) {
            if (pool.isTerminated()) {
                long end = System.currentTimeMillis();
                System.out.println("------------" + (end - start) + "----------------");
                break;
            }
        }


    }


    @Test
    public void downLoadSchoolMajor1() {
        String sql = "select * from school_major_selector";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        //System.out.println(maps.size()); //683035个
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 12; i++) {
            pool.execute(new DownLoadMajorScoreThread(i * 57500, i * 57500 + 57500, 683035, 1, maps));
        }
        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                long end = System.currentTimeMillis();
                System.out.println("------------" + (end - start) + "----------------");
                break;
            }
        }

    }

    @Test
    public void downLoadSchoolMajor2() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_major\\pn_1");
        File[] files = file.listFiles();
        List<Map<String, Object>> maps = new ArrayList<>();
        for (File f : files) {
            String content = ReptileUtils.readJsonFile(f.getAbsolutePath());
            List majorList = JsonPath.read(content, "$.data[*]");
            if (majorList.size() == 10) {
                Integer id = Integer.valueOf(f.getName().split(".txt")[0]);
                String sql = "select * from school_major_selector where id=? ";
                Map<String, Object> map = jdbcTemplate.queryForMap(sql, id);
                maps.add(map);
            }
        }
        System.out.println(maps.size()); //83611

        int index = maps.size() / 12 + 1;
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 12; i++) {
            pool.execute(new DownLoadMajorScoreThread(i * index, i * index + index, maps.size(), 2, maps));
        }
        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                long end = System.currentTimeMillis();
                System.out.println("------------" + (end - start) + "----------------");
                break;
            }
        }
    }

    @Test
    public void downLoadSchoolMajor3() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_major\\pn_2");
        File[] files = file.listFiles();
        List<Map<String, Object>> maps = new ArrayList<>();
        for (File f : files) {
            String content = ReptileUtils.readJsonFile(f.getAbsolutePath());
            List majorList = JsonPath.read(content, "$.data[*]");
            if (majorList.size() == 10) {
                Integer id = Integer.valueOf(f.getName().split(".txt")[0]);
                String sql = "select * from school_major_selector where id=? ";
                Map<String, Object> map = jdbcTemplate.queryForMap(sql, id);
                maps.add(map);
            }
        }
        System.out.println(maps.size()); //35214

        int index = maps.size() / 12 + 1;
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 12; i++) {
            pool.execute(new DownLoadMajorScoreThread(i * index, i * index + index, maps.size(), 3, maps));
        }
        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                long end = System.currentTimeMillis();
                System.out.println("------------" + (end - start) + "----------------");
                break;
            }
        }
    }

    @Test
    public void downLoadSchoolMajor4() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_major\\pn_3");
        File[] files = file.listFiles();
        List<Map<String, Object>> maps = new ArrayList<>();
        for (File f : files) {
            String content = ReptileUtils.readJsonFile(f.getAbsolutePath());
            List majorList = JsonPath.read(content, "$.data[*]");
            if (majorList.size() == 10) {
                Integer id = Integer.valueOf(f.getName().split(".txt")[0]);
                String sql = "select * from school_major_selector where id=? ";
                Map<String, Object> map = jdbcTemplate.queryForMap(sql, id);
                maps.add(map);
            }
        }
        System.out.println(maps.size()); //16967

        int index = maps.size() / 12 + 1;
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 12; i++) {
            pool.execute(new DownLoadMajorScoreThread(i * index, i * index + index, maps.size(), 4, maps));
        }
        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                long end = System.currentTimeMillis();
                System.out.println("------------" + (end - start) + "----------------");
                break;
            }
        }
    }

    @Test
    public void downLoadSchoolMajor5() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_major\\pn_4");
        File[] files = file.listFiles();
        List<Map<String, Object>> maps = new ArrayList<>();
        for (File f : files) {
            String content = ReptileUtils.readJsonFile(f.getAbsolutePath());
            List majorList = JsonPath.read(content, "$.data[*]");
            if (majorList.size() == 10) {
                Integer id = Integer.valueOf(f.getName().split(".txt")[0]);
                String sql = "select * from school_major_selector where id=? ";
                Map<String, Object> map = jdbcTemplate.queryForMap(sql, id);
                maps.add(map);
            }
        }
        System.out.println(maps.size()); //8436

        int index = maps.size() / 12 + 1;
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 12; i++) {
            pool.execute(new DownLoadMajorScoreThread(i * index, i * index + index, maps.size(), 5, maps));
        }
        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                long end = System.currentTimeMillis();
                System.out.println("------------" + (end - start) + "----------------");
                break;
            }
        }
    }

    @Test
    public void downLoadSchoolMajor6() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_major\\pn_5");
        File[] files = file.listFiles();
        List<Map<String, Object>> maps = new ArrayList<>();
        for (File f : files) {
            String content = ReptileUtils.readJsonFile(f.getAbsolutePath());
            List majorList = JsonPath.read(content, "$.data[*]");
            if (majorList.size() == 10) {
                Integer id = Integer.valueOf(f.getName().split(".txt")[0]);
                String sql = "select * from school_major_selector where id=? ";
                Map<String, Object> map = jdbcTemplate.queryForMap(sql, id);
                maps.add(map);
            }
        }
        System.out.println(maps.size()); //4288

        int index = maps.size() / 12 + 1;
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 12; i++) {
            pool.execute(new DownLoadMajorScoreThread(i * index, i * index + index, maps.size(), 6, maps));
        }
        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                long end = System.currentTimeMillis();
                System.out.println("------------" + (end - start) + "----------------");
                break;
            }
        }
    }

    @Test
    public void downLoadSchoolMajor7() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_major\\pn_6");
        File[] files = file.listFiles();
        List<Map<String, Object>> maps = new ArrayList<>();
        for (File f : files) {
            String content = ReptileUtils.readJsonFile(f.getAbsolutePath());
            List majorList = JsonPath.read(content, "$.data[*]");
            if (majorList.size() == 10) {
                Integer id = Integer.valueOf(f.getName().split(".txt")[0]);
                String sql = "select * from school_major_selector where id=? ";
                Map<String, Object> map = jdbcTemplate.queryForMap(sql, id);
                maps.add(map);
            }
        }
        System.out.println(maps.size()); //2327

        int index = maps.size() / 12 + 1;
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 12; i++) {
            pool.execute(new DownLoadMajorScoreThread(i * index, i * index + index, maps.size(), 7, maps));
        }
        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                long end = System.currentTimeMillis();
                System.out.println("------------" + (end - start) + "----------------");
                break;
            }
        }
    }

    @Test
    public void downLoadSchoolMajor8() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_major\\pn_7");
        File[] files = file.listFiles();
        List<Map<String, Object>> maps = new ArrayList<>();
        for (File f : files) {
            String content = ReptileUtils.readJsonFile(f.getAbsolutePath());
            List majorList = JsonPath.read(content, "$.data[*]");
            if (majorList.size() == 10) {
                Integer id = Integer.valueOf(f.getName().split(".txt")[0]);
                String sql = "select * from school_major_selector where id=? ";
                Map<String, Object> map = jdbcTemplate.queryForMap(sql, id);
                maps.add(map);
            }
        }
        System.out.println(maps.size()); //1325

        int index = maps.size() / 12 + 1;
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 12; i++) {
            pool.execute(new DownLoadMajorScoreThread(i * index, i * index + index, maps.size(), 8, maps));
        }
        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                long end = System.currentTimeMillis();
                System.out.println("------------" + (end - start) + "----------------");
                break;
            }
        }
    }

    @Test
    public void downLoadSchoolMajor9() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_major\\pn_8");
        File[] files = file.listFiles();
        List<Map<String, Object>> maps = new ArrayList<>();
        for (File f : files) {
            String content = ReptileUtils.readJsonFile(f.getAbsolutePath());
            List majorList = JsonPath.read(content, "$.data[*]");
            if (majorList.size() == 10) {
                Integer id = Integer.valueOf(f.getName().split(".txt")[0]);
                String sql = "select * from school_major_selector where id=? ";
                Map<String, Object> map = jdbcTemplate.queryForMap(sql, id);
                maps.add(map);
            }
        }
        System.out.println(maps.size()); //785

        int index = maps.size() / 12 + 1;
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 12; i++) {
            pool.execute(new DownLoadMajorScoreThread(i * index, i * index + index, maps.size(), 9, maps));
        }
        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                long end = System.currentTimeMillis();
                System.out.println("------------" + (end - start) + "----------------");
                break;
            }
        }
    }

    @Test
    public void downLoadSchoolMajor10() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_major\\pn_9");
        File[] files = file.listFiles();
        List<Map<String, Object>> maps = new ArrayList<>();
        for (File f : files) {
            String content = ReptileUtils.readJsonFile(f.getAbsolutePath());
            List majorList = JsonPath.read(content, "$.data[*]");
            if (majorList.size() == 10) {
                Integer id = Integer.valueOf(f.getName().split(".txt")[0]);
                String sql = "select * from school_major_selector where id=? ";
                Map<String, Object> map = jdbcTemplate.queryForMap(sql, id);
                maps.add(map);
            }
        }
        System.out.println(maps.size()); //459

        int index = maps.size() / 12 + 1;
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 12; i++) {
            pool.execute(new DownLoadMajorScoreThread(i * index, i * index + index, maps.size(), 10, maps));
        }
        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                long end = System.currentTimeMillis();
                System.out.println("------------" + (end - start) + "----------------");
                break;
            }
        }
    }

    @Test
    public void downLoadSchoolMajor11() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_major\\pn_10");
        File[] files = file.listFiles();
        List<Map<String, Object>> maps = new ArrayList<>();
        for (File f : files) {
            String content = ReptileUtils.readJsonFile(f.getAbsolutePath());
            List majorList = JsonPath.read(content, "$.data[*]");
            if (majorList.size() == 10) {
                Integer id = Integer.valueOf(f.getName().split(".txt")[0]);
                String sql = "select * from school_major_selector where id=? ";
                Map<String, Object> map = jdbcTemplate.queryForMap(sql, id);
                maps.add(map);
            }
        }
        System.out.println(maps.size()); //270

        int index = maps.size() / 12 + 1;
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 12; i++) {
            pool.execute(new DownLoadMajorScoreThread(i * index, i * index + index, maps.size(), 11, maps));
        }
        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                long end = System.currentTimeMillis();
                System.out.println("------------" + (end - start) + "----------------");
                break;
            }
        }
    }

    @Test
    public void downLoadSchoolMajor12() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_major\\pn_11");
        File[] files = file.listFiles();
        List<Map<String, Object>> maps = new ArrayList<>();
        for (File f : files) {
            String content = ReptileUtils.readJsonFile(f.getAbsolutePath());
            List majorList = JsonPath.read(content, "$.data[*]");
            if (majorList.size() == 10) {
                Integer id = Integer.valueOf(f.getName().split(".txt")[0]);
                String sql = "select * from school_major_selector where id=? ";
                Map<String, Object> map = jdbcTemplate.queryForMap(sql, id);
                maps.add(map);
            }
        }
        System.out.println(maps.size()); //181

        int index = maps.size() / 12 + 1;
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 12; i++) {
            pool.execute(new DownLoadMajorScoreThread(i * index, i * index + index, maps.size(), 12, maps));
        }
        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                long end = System.currentTimeMillis();
                System.out.println("------------" + (end - start) + "----------------");
                break;
            }
        }
    }

    @Test
    public void downLoadSchoolMajor13() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_major\\pn_12");
        File[] files = file.listFiles();
        List<Map<String, Object>> maps = new ArrayList<>();
        for (File f : files) {
            String content = ReptileUtils.readJsonFile(f.getAbsolutePath());
            List majorList = JsonPath.read(content, "$.data[*]");
            if (majorList.size() == 10) {
                Integer id = Integer.valueOf(f.getName().split(".txt")[0]);
                String sql = "select * from school_major_selector where id=? ";
                Map<String, Object> map = jdbcTemplate.queryForMap(sql, id);
                maps.add(map);
            }
        }
        System.out.println(maps.size()); //113

        int index = maps.size() / 12 + 1;
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 12; i++) {
            pool.execute(new DownLoadMajorScoreThread(i * index, i * index + index, maps.size(), 13, maps));
        }
        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                long end = System.currentTimeMillis();
                System.out.println("------------" + (end - start) + "----------------");
                break;
            }
        }
    }

    @Test
    public void downLoadSchoolMajor14() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_major\\pn_13");
        File[] files = file.listFiles();
        List<Map<String, Object>> maps = new ArrayList<>();
        for (File f : files) {
            String content = ReptileUtils.readJsonFile(f.getAbsolutePath());
            List majorList = JsonPath.read(content, "$.data[*]");
            if (majorList.size() == 10) {
                Integer id = Integer.valueOf(f.getName().split(".txt")[0]);
                String sql = "select * from school_major_selector where id=? ";
                Map<String, Object> map = jdbcTemplate.queryForMap(sql, id);
                maps.add(map);
            }
        }
        System.out.println(maps.size()); //75

        int index = maps.size() / 12 + 1;
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 12; i++) {
            pool.execute(new DownLoadMajorScoreThread(i * index, i * index + index, maps.size(), 14, maps));
        }
        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                long end = System.currentTimeMillis();
                System.out.println("------------" + (end - start) + "----------------");
                break;
            }
        }
    }

    @Test
    public void downLoadSchoolMajor15() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_major\\pn_14");
        File[] files = file.listFiles();
        List<Map<String, Object>> maps = new ArrayList<>();
        for (File f : files) {
            String content = ReptileUtils.readJsonFile(f.getAbsolutePath());
            List majorList = JsonPath.read(content, "$.data[*]");
            if (majorList.size() == 10) {
                Integer id = Integer.valueOf(f.getName().split(".txt")[0]);
                String sql = "select * from school_major_selector where id=? ";
                Map<String, Object> map = jdbcTemplate.queryForMap(sql, id);
                maps.add(map);
            }
        }
        System.out.println(maps.size()); //54

        int index = maps.size() / 12 + 1;
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 12; i++) {
            pool.execute(new DownLoadMajorScoreThread(i * index, i * index + index, maps.size(), 15, maps));
        }
        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                long end = System.currentTimeMillis();
                System.out.println("------------" + (end - start) + "----------------");
                break;
            }
        }
    }

    @Test
    public void downLoadSchoolMajor16() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_major\\pn_15");
        File[] files = file.listFiles();
        List<Map<String, Object>> maps = new ArrayList<>();
        for (File f : files) {
            String content = ReptileUtils.readJsonFile(f.getAbsolutePath());
            List majorList = JsonPath.read(content, "$.data[*]");
            if (majorList.size() == 10) {
                Integer id = Integer.valueOf(f.getName().split(".txt")[0]);
                String sql = "select * from school_major_selector where id=? ";
                Map<String, Object> map = jdbcTemplate.queryForMap(sql, id);
                maps.add(map);
            }
        }
        System.out.println(maps.size()); //37

        int index = maps.size() / 12 + 1;
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 12; i++) {
            pool.execute(new DownLoadMajorScoreThread(i * index, i * index + index, maps.size(), 16, maps));
        }
        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                long end = System.currentTimeMillis();
                System.out.println("------------" + (end - start) + "----------------");
                break;
            }
        }
    }

    @Test
    public void downLoadSchoolMajor17() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_major\\pn_16");
        File[] files = file.listFiles();
        List<Map<String, Object>> maps = new ArrayList<>();
        for (File f : files) {
            String content = ReptileUtils.readJsonFile(f.getAbsolutePath());
            List majorList = JsonPath.read(content, "$.data[*]");
            if (majorList.size() == 10) {
                Integer id = Integer.valueOf(f.getName().split(".txt")[0]);
                String sql = "select * from school_major_selector where id=? ";
                Map<String, Object> map = jdbcTemplate.queryForMap(sql, id);
                maps.add(map);
            }
        }
        System.out.println(maps.size()); //27

        int index = maps.size() / 12 + 1;
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 12; i++) {
            pool.execute(new DownLoadMajorScoreThread(i * index, i * index + index, maps.size(), 17, maps));
        }
        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                long end = System.currentTimeMillis();
                System.out.println("------------" + (end - start) + "----------------");
                break;
            }
        }
    }

    @Test
    public void downLoadSchoolMajor18() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_major\\pn_17");
        File[] files = file.listFiles();
        List<Map<String, Object>> maps = new ArrayList<>();
        for (File f : files) {
            String content = ReptileUtils.readJsonFile(f.getAbsolutePath());
            List majorList = JsonPath.read(content, "$.data[*]");
            if (majorList.size() == 10) {
                Integer id = Integer.valueOf(f.getName().split(".txt")[0]);
                String sql = "select * from school_major_selector where id=? ";
                Map<String, Object> map = jdbcTemplate.queryForMap(sql, id);
                maps.add(map);
            }
        }
        System.out.println(maps.size()); //19

        int index = maps.size() / 12 + 1;
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 12; i++) {
            pool.execute(new DownLoadMajorScoreThread(i * index, i * index + index, maps.size(), 18, maps));
        }
        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                long end = System.currentTimeMillis();
                System.out.println("------------" + (end - start) + "----------------");
                break;
            }
        }
    }

    @Test
    public void downLoadSchoolMajor19() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_major\\pn_18");
        File[] files = file.listFiles();
        List<Map<String, Object>> maps = new ArrayList<>();
        for (File f : files) {
            String content = ReptileUtils.readJsonFile(f.getAbsolutePath());
            List majorList = JsonPath.read(content, "$.data[*]");
            if (majorList.size() == 10) {
                Integer id = Integer.valueOf(f.getName().split(".txt")[0]);
                String sql = "select * from school_major_selector where id=? ";
                Map<String, Object> map = jdbcTemplate.queryForMap(sql, id);
                maps.add(map);
            }
        }
        System.out.println(maps.size()); //13

        int index = maps.size() / 12 + 1;
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 12; i++) {
            pool.execute(new DownLoadMajorScoreThread(i * index, i * index + index, maps.size(), 19, maps));
        }
        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                long end = System.currentTimeMillis();
                System.out.println("------------" + (end - start) + "----------------");
                break;
            }
        }
    }

    @Test
    public void downLoadSchoolMajor20() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_major\\pn_19");
        File[] files = file.listFiles();
        List<Map<String, Object>> maps = new ArrayList<>();
        for (File f : files) {
            String content = ReptileUtils.readJsonFile(f.getAbsolutePath());
            List majorList = JsonPath.read(content, "$.data[*]");
            if (majorList.size() == 10) {
                Integer id = Integer.valueOf(f.getName().split(".txt")[0]);
                String sql = "select * from school_major_selector where id=? ";
                Map<String, Object> map = jdbcTemplate.queryForMap(sql, id);
                maps.add(map);
            }
        }
        System.out.println(maps.size()); //10

        int index = maps.size() / 12 + 1;
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 12; i++) {
            pool.execute(new DownLoadMajorScoreThread(i * index, i * index + index, maps.size(), 20, maps));
        }
        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                long end = System.currentTimeMillis();
                System.out.println("------------" + (end - start) + "----------------");
                break;
            }
        }
    }

    @Test
    public void downLoadSchoolMajor21() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_major\\pn_20");
        File[] files = file.listFiles();
        List<Map<String, Object>> maps = new ArrayList<>();
        for (File f : files) {
            String content = ReptileUtils.readJsonFile(f.getAbsolutePath());
            List majorList = JsonPath.read(content, "$.data[*]");
            if (majorList.size() == 10) {
                Integer id = Integer.valueOf(f.getName().split(".txt")[0]);
                String sql = "select * from school_major_selector where id=? ";
                Map<String, Object> map = jdbcTemplate.queryForMap(sql, id);
                maps.add(map);
            }
        }
        System.out.println(maps.size()); //7

        int index = maps.size() / 12 + 1;
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 12; i++) {
            pool.execute(new DownLoadMajorScoreThread(i * index, i * index + index, maps.size(), 21, maps));
        }
        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                long end = System.currentTimeMillis();
                System.out.println("------------" + (end - start) + "----------------");
                break;
            }
        }
    }

    @Test
    public void downLoadSchoolMajor22() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_major\\pn_21");
        File[] files = file.listFiles();
        List<Map<String, Object>> maps = new ArrayList<>();
        for (File f : files) {
            String content = ReptileUtils.readJsonFile(f.getAbsolutePath());
            List majorList = JsonPath.read(content, "$.data[*]");
            if (majorList.size() == 10) {
                Integer id = Integer.valueOf(f.getName().split(".txt")[0]);
                String sql = "select * from school_major_selector where id=? ";
                Map<String, Object> map = jdbcTemplate.queryForMap(sql, id);
                maps.add(map);
            }
        }
        System.out.println(maps.size()); //4

        int index = maps.size() / 12 + 1;
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 12; i++) {
            pool.execute(new DownLoadMajorScoreThread(i * index, i * index + index, maps.size(), 22, maps));
        }
        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                long end = System.currentTimeMillis();
                System.out.println("------------" + (end - start) + "----------------");
                break;
            }
        }
    }

    @Test
    public void downLoadSchoolMajor23() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_major\\pn_22");
        File[] files = file.listFiles();
        List<Map<String, Object>> maps = new ArrayList<>();
        for (File f : files) {
            String content = ReptileUtils.readJsonFile(f.getAbsolutePath());
            List majorList = JsonPath.read(content, "$.data[*]");
            if (majorList.size() == 10) {
                Integer id = Integer.valueOf(f.getName().split(".txt")[0]);
                String sql = "select * from school_major_selector where id=? ";
                Map<String, Object> map = jdbcTemplate.queryForMap(sql, id);
                maps.add(map);
            }
        }
        System.out.println(maps.size()); //4

        int index = maps.size() / 12 + 1;
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 12; i++) {
            pool.execute(new DownLoadMajorScoreThread(i * index, i * index + index, maps.size(), 23, maps));
        }
        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                long end = System.currentTimeMillis();
                System.out.println("------------" + (end - start) + "----------------");
                break;
            }
        }
    }

    @Test
    public void downLoadSchoolMajor24() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_major\\pn_23");
        File[] files = file.listFiles();
        List<Map<String, Object>> maps = new ArrayList<>();
        for (File f : files) {
            String content = ReptileUtils.readJsonFile(f.getAbsolutePath());
            List majorList = JsonPath.read(content, "$.data[*]");
            if (majorList.size() == 10) {
                Integer id = Integer.valueOf(f.getName().split(".txt")[0]);
                String sql = "select * from school_major_selector where id=? ";
                Map<String, Object> map = jdbcTemplate.queryForMap(sql, id);
                maps.add(map);
            }
        }
        System.out.println(maps.size()); //1

        int index = maps.size() / 12 + 1;
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 12; i++) {
            pool.execute(new DownLoadMajorScoreThread(i * index, i * index + index, maps.size(), 24, maps));
        }
        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                long end = System.currentTimeMillis();
                System.out.println("------------" + (end - start) + "----------------");
                break;
            }
        }
    }

    @Test
    public void downLoadSchoolMajor25() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_major\\pn_24");
        File[] files = file.listFiles();
        List<Map<String, Object>> maps = new ArrayList<>();
        for (File f : files) {
            String content = ReptileUtils.readJsonFile(f.getAbsolutePath());
            List majorList = JsonPath.read(content, "$.data[*]");
            if (majorList.size() == 10) {
                Integer id = Integer.valueOf(f.getName().split(".txt")[0]);
                String sql = "select * from school_major_selector where id=? ";
                Map<String, Object> map = jdbcTemplate.queryForMap(sql, id);
                maps.add(map);
            }
        }
        System.out.println(maps.size()); //1

        int index = maps.size() / 12 + 1;
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 12; i++) {
            pool.execute(new DownLoadMajorScoreThread(i * index, i * index + index, maps.size(), 25, maps));
        }
        pool.shutdown();
        while (true) {
            if (pool.isTerminated()) {
                long end = System.currentTimeMillis();
                System.out.println("------------" + (end - start) + "----------------");
                break;
            }
        }
    }


    @Test
    public void schoolScoreToSql() {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_score");
        for (File f : file.listFiles()) {
            Integer sss_id = Integer.valueOf(f.getName().split(".txt")[0]);
            String content = ReptileUtils.readJsonFile(f.getAbsolutePath());
            String msg = JsonPath.read(content, "$.msg").toString();
            System.out.println(f.getName());
            if (msg.equals("success")) {
                try {
                    Map map = JsonPath.read(content, "$.data");
                    System.out.println("----------" + sss_id + "----------");
                    for (int i = 0; i < 5; i++) {

                        Integer year = null;
                        Integer min_cha = null;
                        Integer min_score = null;
                        Integer enroll_num = null;
                        Integer min_score_order = null;
                        String batch_name = null;
                        switch (i) {
                            case 0:
                                year = 2015;
                                break;
                            case 1:
                                year = 2016;
                                break;
                            case 2:
                                year = 2017;
                                break;
                            case 3:
                                year = 2018;
                                break;
                            case 4:
                                year = 2019;
                                break;
                        }

                        if (map.containsKey("minCha")) {
                            batch_name = JsonPath.read(content, "$.data.minCha[" + i + "].value.[0].name").toString();
                            if (batch_name.equals("--")) {
                                batch_name = null;
                            }
                            String min_cha_str = JsonPath.read(content, "$.data.minCha[" + i + "].value.[0].value").toString();
                            if (!min_cha_str.equals("--")) {
                                min_cha = Integer.valueOf(min_cha_str);
                            }
                        }
                        if (map.containsKey("minScore")) {
                            String min_score_str = JsonPath.read(content, "$.data.minScore[" + i + "].value.[0].value").toString();
                            if (!min_score_str.equals("--")) {
                                min_score = Integer.valueOf(min_score_str);
                            }
                        }
                        if (map.containsKey("enrollNum")) {
                            String enroll_num_str = JsonPath.read(content, "$.data.enrollNum[" + i + "].value.[0].value").toString();
                            if (!enroll_num_str.equals("--")) {
                                enroll_num = Integer.valueOf(enroll_num_str);
                            }
                        }
                        if (map.containsKey("minScoreOrder")) {
                            String min_score_order_str = JsonPath.read(content, "$.data.minScoreOrder[" + i + "].value.[0].value").toString();
                            if (!min_score_order_str.equals("--")) {
                                min_score_order = Integer.valueOf(min_score_order_str);
                            }
                        }
                        System.out.println(year + ":" + min_cha + ":" + enroll_num + ":" + min_score + ":" + min_score_order);
                        String sql = "insert into school_score(sss_id,year,min_cha,min_score,enroll_num,min_score_order,batch_name)" +
                                "values(?,?,?,?,?,?,?)";
                        jdbcTemplate.update(sql, sss_id, year, min_cha, min_score, enroll_num, min_score_order, batch_name);
                    }
                } catch (ClassCastException classCastException) {
                    try {
                        FileWriter fw = new FileWriter(new File("id.txt"),true);
                        fw.write(sss_id+",");
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }


        }


    }

    @Test
    public void test1() {
        int[] ids = {102, 99, 42, 31, 140, 104, 123, 114, 934, 125, 111, 60, 126, 44, 73, 48, 62, 118, 119, 51, 132, 499, 391, 36, 127, 105, 122, 61, 504, 109, 128, 138, 509, 232, 661, 86, 108, 46, 330, 71, 76, 134, 59, 143, 101, 554, 459, 106, 414, 97, 284, 34, 130, 52, 460, 310, 131, 537, 342, 1073, 107, 173, 81, 463, 540, 33, 63, 57, 566, 293, 47, 88, 82, 507, 187, 37, 178, 265, 133, 116, 535, 35, 332, 471, 558, 307, 85, 464, 420, 139, 112, 180, 490, 528, 79, 184, 77, 245, 555, 67, 389, 38, 135, 622, 84, 220, 311, 120, 80, 428, 385, 30, 831, 1018, 164, 263, 115, 53, 334, 286, 352, 50, 218, 41, 175, 229, 542, 144, 268, 557, 124, 569, 547, 157, 466, 349, 510, 179, 277, 1249, 142, 426, 54, 496, 494, 1061, 491, 1007, 39, 434, 393, 597, 96, 103, 458, 313, 199, 160, 388, 433, 935, 231, 177, 270, 165, 335, 242, 121, 110, 58, 575, 534, 320, 526, 98, 78, 247, 604, 361, 302, 354, 833, 151, 358, 351, 431, 423, 787, 100, 159, 55, 69, 243, 398, 45, 375, 602, 170, 171, 373, 421, 356, 129, 473, 275, 117, 174, 136, 68, 533, 939, 875, 532, 308, 176, 429, 465, 217, 323, 376, 488, 273, 49, 589, 481, 113, 227, 576, 43, 479, 264, 363, 56, 381, 210, 417, 599, 163, 374, 230, 193, 262, 1178, 290, 166, 146, 430, 66, 607, 240, 556, 287, 1009, 596, 271, 1323, 257, 552, 309, 87, 209, 1142, 425, 288, 213, 1010, 462, 401, 318, 214, 1219, 419, 483, 216, 278, 584, 445, 960, 252, 317, 266, 412, 511, 1066, 1062, 655, 2054, 91, 484, 161, 1265, 1031, 357, 241, 169, 444, 32, 595, 379, 312, 480, 315, 505, 531, 211, 274, 570, 2498, 396, 553, 853, 606, 208, 551, 876, 1336};
        List<Integer> collect = Arrays.stream(ids).boxed().collect(Collectors.toList());

        long l1 = System.currentTimeMillis();
        collect.parallelStream().forEach(id->{
//            String content = ReptileUtils.getContent("https://static-data.eol.cn/www/2.0/school/" + id + "/info.json");
            System.out.println(Thread.currentThread().getName());

        });



    }

}
