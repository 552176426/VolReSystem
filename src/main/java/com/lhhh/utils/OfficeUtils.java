package com.lhhh.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: lhhh
 * @date: Created in 2020/10/15
 * @description:
 * @version:1.0
 */
public class OfficeUtils {
    public static void main(String[] args) {
        String csvFileName = "D:\\Downloads\\高校志愿推荐\\2020一分一段表\\江西\\江西-文科.csv";
        List<String> strings = readCsv(csvFileName);
        for (String string : strings) {
            System.out.println("1");
            System.out.println(string);
        }
    }

    public static List<String> readCsv(String csvFileName){
        List<String> allString = null;
        File csv = new File(csvFileName);
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(csv),"GBK"));
            String line = "";
            allString = new ArrayList<>();
            while ((line = br.readLine()) != null)  //读取到的内容给line变量
            {
//                System.out.println(line);
                allString.add(line);
            }
            br.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allString;
    }
}
