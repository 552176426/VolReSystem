package com.lhhh.data;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author: lhhh
 * @date: Created in 2020/11/4
 * @description:
 * @version:1.0
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class LogTest {

    @Test
    public void t(){
        log.info("123");
    }
}
