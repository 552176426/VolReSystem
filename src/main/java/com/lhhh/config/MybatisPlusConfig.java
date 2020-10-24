package com.lhhh.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author: lhhh
 * @date: Created in 2020/9/25
 * @description:
 * @version:1.0
 */
@Configuration
@MapperScan("com.lhhh.mapper")
public class MybatisPlusConfig {
}
