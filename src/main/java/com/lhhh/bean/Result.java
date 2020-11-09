package com.lhhh.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author: lhhh
 * @date: Created in 2020/10/24
 * @description:
 * @version:1.0
 */
@Data
@AllArgsConstructor
public class Result {
    private Integer code;
    private String msg;
    private Object data;
}
