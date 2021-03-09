package com.lhhh.reptile;

import com.lhhh.utils.ReptileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: lhhh
 * @date: Created in 2020/12/16
 * @description:
 * @version:1.0
 */
public class BaiduJobDetail {
    public static void main(String[] args) {
        File file = new File("D:\\Downloads\\高校志愿推荐\\百度高考数据\\school_info");
        List<String> list  = new ArrayList<>();
        for (File listFile : file.listFiles()) {
            list.add(listFile.getName().split(".txt")[0]);
        }
        list.parallelStream().forEach(i->{
            String url = "https://gaokao.baidu.com/gaokao/gkschool/career?ajax=1&query="+i;
            String content = ReptileUtils.getContent(url);
            ReptileUtils.downLoad(content,"D:\\Downloads\\高校志愿推荐\\百度高考数据\\就业\\"+i+".txt");
        });
    }
}
