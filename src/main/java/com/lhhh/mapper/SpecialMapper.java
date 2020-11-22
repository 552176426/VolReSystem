package com.lhhh.mapper;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author: lhhh
 * @date: Created in 2020/11/18
 * @description:
 * @version:1.0
 */
@Repository
public interface SpecialMapper {

    @Select("select degree,limit_year,level2_name,level3_name,name from special where level1=1")
    List<Map<String,Object>> findSpecials1();

    @Select("select degree,limit_year,level2_name,level3_name,name from special where level1=2")
    List<Map<String,Object>> findSpecials2();
}
