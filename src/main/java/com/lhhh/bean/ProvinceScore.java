package com.lhhh.bean;

import lombok.Data;

/**
 * @author: lhhh
 * @date: Created in 2020/10/24
 * @description:
 * @version:1.0
 */
@Data
public class ProvinceScore {
    private Integer id;
    private String provinceName;
    private String batchName;
    private Integer year;
    private Integer score;
    private String type;
}
