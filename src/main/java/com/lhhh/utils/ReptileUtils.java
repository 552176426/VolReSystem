package com.lhhh.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;

/**
 * @author: lhhh
 * @date: Created in 2020/10/10
 * @description:
 * @version:1.0
 */
public class ReptileUtils {

    /**
     * 根据url获取html或者json数据
     *
     * @param url
     * @return
     */
    public static String getContent(String url) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        String content = null;
        try {
            CloseableHttpResponse response = client.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                content = EntityUtils.toString(entity, "utf8");
                content = JSONObject.parse(content).toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }

    public static String getContentFromPost(String postURL,NameValuePair[] data) {
        String result=null;
        try {
            PostMethod postMethod = null;
            postMethod = new PostMethod(postURL);
            postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            //参数设置，需要注意的就是里边不能传NULL，要传空字符串
            postMethod.setRequestBody(data);
            org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();
            httpClient.executeMethod(postMethod); // 执行POST方法
            result = postMethod.getResponseBodyAsString();
            result = JSONObject.parse(result).toString();
            return result;
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 读取Json文件
     *
     * @param file
     * @return
     */
    public static String readJsonFile(String file) {
        String s = "";
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(file)));
            while ((s = br.readLine()) != null) {
                sb = sb.append(s);
            }
            s = sb.toString();
            return s;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 下载
     *
     * @param content
     * @param fileName
     */
    public static void downLoad(String content, String fileName) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(new File(fileName)));
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String content = getContent("https://api.eol.cn/gkcx/api/?access_token=&page=1&province_id=11&signsafe=&size=20&uri=apidata/api/gk/score/proprovince&year=2020");
        System.out.println(content);
    }
}
