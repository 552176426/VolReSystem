package com.lhhh.reptile;

import com.lhhh.utils.ReptileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author: lhhh
 * @date: Created in 2020/10/17
 * @description:
 * @version:1.0
 */
public class DownLoadMajorScoreThread implements Runnable {
    private int start;
    private int end;
    private int max;
    private int pn;
    private List<Map<String, Object>> mapList;

    public DownLoadMajorScoreThread(int start, int end, int max, int pn, List<Map<String, Object>> mapList) {
        this.start = start;
        this.end = end;
        this.max = max;
        this.pn = pn;
        this.mapList = mapList;
    }

    @Override
    public void run() {
        int num = 0;
        for (int i = start; i < end; i++) {
            if (i == max) {
                break;
            }
            Map<String, Object> map = mapList.get(i);
            Integer id = Integer.valueOf(map.get("id").toString());
            String schoolName = map.get("school_name").toString();
            String provinceName = map.get("province_name").toString();
            String curriculum = map.get("curriculum").toString();
            Integer year = Integer.valueOf(map.get("year").toString());

            String url = "https://gaokao.baidu.com/gaokao/gkschool/scoremajor?ajax=1&school=" + schoolName + "&province=" + provinceName + "&curriculum=" + curriculum + "&year=" + year + "&pn=" + pn + "&rn=10";
            String fileName = "D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_major\\pn_" + pn + "\\" + id + ".txt";
            File file = new File(fileName);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (file.length() == 0) {
                num++;
                try {
                    String content = ReptileUtils.getContent(url);
                    ReptileUtils.downLoad(content, fileName);
                    System.out.println(id + ":" + "写入完成");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println(id+": 内容已存在，无需写入");
            }
        }
        System.out.println(num+"个为空");

    }
}