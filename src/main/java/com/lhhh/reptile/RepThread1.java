package com.lhhh.reptile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static com.lhhh.reptile.Reptile.getContent;

/**
 * @author: lhhh
 * @date: Created in 2020/9/25
 * @description: 获取id
 * @version:1.0
 */
public class RepThread1 implements Callable<List<Integer>> {

    private volatile int i;

    public RepThread1(int i) {
        this.i = i;
    }

    @Override
    public List<Integer> call() throws Exception {
        ArrayList<Integer> schoolIds = new ArrayList<>();
        for (int j = 0; j < 30; j++) {
            if (i==149){
                break;
            }
            String url = "https://api.eol.cn/gkcx/api/?request_type=1&size=20&sort=view_total&uri=apidata/api/gk/school/lists&page=";
            url += String.valueOf(i);
            i++;
            System.out.println(url);
            String json = getContent(url);
            /*ArrayList<Integer> idS = JsonPath.read(json, "$.data.item[*].school_id");
            schoolIds.addAll(idS);*/
        }
        return schoolIds;
    }
}
