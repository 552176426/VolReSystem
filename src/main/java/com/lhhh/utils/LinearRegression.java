package com.lhhh.utils;

import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;

import java.util.List;

/**
 * @author: lhhh
 * @date: Created in 2020/11/12
 * @description:
 * @version:1.0
 */
public class LinearRegression {

    public static void main(String[] args) {
        long l = System.currentTimeMillis();
        List<Word> wo = WordSegmenter.seg("计算机科学与技术");
        wo.forEach(System.out::println);
        long r = System.currentTimeMillis();
        System.out.println(l-r);
    }


}
