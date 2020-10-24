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
public class SchoolSpecialData {
    public static void main(String[] args) {
        downLoad1(2623,5100);
    }
    public static void downLoad1(int start,int end){
        for (int i = start; i <= end; i++) {
            String url = "https://api.eol.cn/gkcx/api/?access_token=&keyword=&page="+i+"&province_id=&request_type=1&school_type=&signsafe=&size=20&special_id=&type=&uri=apidata/api/gk/schoolSpecial/lists";
            System.out.print("准备读取第"+i+"页...");
            String content = ReptileUtils.getContent(url);
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(new File("D:\\Downloads\\高校志愿推荐\\schoolSpecial\\page_" + i + "." + "txt")));
                System.out.print("写入中....");
                bw.write(content);
                System.out.println("写入完成");
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
