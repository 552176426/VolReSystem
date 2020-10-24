package com.lhhh.reptile;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author: lhhh
 * @date: Created in 2020/9/25
 * @description:
 * @version:1.0
 */
public class RepThread3 implements Callable<List<Integer>> {


    @Override
    public List<Integer> call() throws Exception {
        for (int j = 0; j < 10000; j++) {
            System.out.println(Thread.currentThread().getName()+":"+j);
        }
        return null;
    }
}
