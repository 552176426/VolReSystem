package com.lhhh.reptile;

import com.lhhh.utils.ReptileUtils;
import org.apache.commons.httpclient.NameValuePair;

/**
 * @author: lhhh
 * @date: Created in 2020/10/31
 * @description:
 * @version:1.0
 */
public class SubjectAssessment {
    public static void main(String[] args) {
        for (int i = 1; i <= 111; i++) {
            System.out.println(i+"读取中...");
            String postURL = "https://www.cingta.com/data/api/get_subjectresult/";
            NameValuePair[] data = {new NameValuePair("id",String.valueOf(i))};
            String content = ReptileUtils.getContentFromPost(postURL,data);
            System.out.println(i+"写入中...");
            ReptileUtils.downLoad(content,"D:\\Downloads\\高校志愿推荐\\学科评估\\"+i+".txt");
            System.out.println(i+"写入完成");
        }
    }
}


