package com.lhhh.reptile;

import com.lhhh.utils.ReptileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author: lhhh
 * @date: Created in 2020/10/10
 * @description:
 * @version:1.0
 */
public class ProvinceScoreData {
    private static int[] provinceIds = {11, 12, 13, 14, 15, 21,22, 23, 31, 32, 33, 34, 35, 36, 37,41, 42, 43, 44, 45, 46, 50, 51, 52, 53, 54, 61, 62, 63, 64, 65};
    private static int[] years = {2020,2019,2018,2017,2016,2015,2014};

    public static void main(String[] args) throws IOException {
        downLoad();
    }

    public static void downLoad(){
        for (int proId : provinceIds) {
            String url1 = "https://api.eol.cn/gkcx/api/?" +
                    "access_token=&page=1&signsafe=&size=20&uri=apidata/api/gk/score/proprovince&province_id=";
            File file1 = new File("D:\\Downloads\\高校志愿推荐\\provinceScore\\" + proId);
            if (!file1.exists()){
                file1.mkdir();
            }
            url1+=proId;
            for (int year : years) {
                String url2 = url1;
                File file2 = new File("D:\\Downloads\\高校志愿推荐\\provinceScore\\" + proId + "\\" + year);
                if (!file2.exists()){
                    file2.mkdir();
                }
                url2+="&year="+year;
                System.out.println(url2);
                String content = ReptileUtils.getContent(url2);
                BufferedWriter bw = null;
                try {
                    bw = new BufferedWriter(new FileWriter(new File(file2 + "\\score.txt")));
                    bw.write(content);
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



}
