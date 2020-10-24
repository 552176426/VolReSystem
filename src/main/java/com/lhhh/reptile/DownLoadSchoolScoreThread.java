package com.lhhh.reptile;

import com.lhhh.utils.ReptileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author: lhhh
 * @date: Created in 2020/10/17
 * @description:
 * @version:1.0
 */
public class DownLoadSchoolScoreThread implements Runnable {
    private int start;
    private int end;
    private List<Map<String, Object>> mapList;

    public DownLoadSchoolScoreThread(int start, int end, List<Map<String, Object>> mapList) {
        this.start = start;
        this.end = end;
        this.mapList = mapList;
    }

    @Override
    public void run() {
        int num = 0;
        for (int i = start; i < end; i++) {
            if (i==285733){
                break;
            }
            Map<String, Object> map = mapList.get(i);
            Integer id = Integer.valueOf(map.get("id").toString());
            String schoolName = map.get("school_name").toString();
            String provinceName = map.get("province_name").toString();
            String curriculum = map.get("curriculum").toString();
            String batchName = map.get("batch_name").toString();
            String url = "https://gaokao.baidu.com/gaokao/gkschool/scoreenroll?ajax=1&query=" + schoolName + "&province=" + provinceName + "&curriculum=" + curriculum + "&batchName=" + batchName;
            String fileName = "D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_score\\" + id+".txt";
            File file = new File(fileName);
            if (!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (file.length()==0){
                num++;
                try {
                    String content = ReptileUtils.getContent(url);
                    ReptileUtils.downLoad(content,fileName);
                    System.out.println(id+" : 写入完成");
                } catch (Exception e){
                    try {
                        //有问题的id
                        FileWriter fileWriter = new FileWriter(new File("id.txt"), true);
                        fileWriter.write(id+",");
                        fileWriter.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            } else if (file.length()!=0){
                System.err.println(id+"内容已存在无需写入");
            }
        }
        System.out.println(num+"个为空");
    }
}
