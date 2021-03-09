package com.lhhh.mapper;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author: lhhh
 * @date: Created in 2021/1/5
 * @description:
 * @version:1.0
 */
@Repository
public interface SearchMapper {
    @Select("select id,name,badge_url from school where name like concat('%',#{str},'%')")
    List<Map<String,Object>> searchSchool(String str);

    @Select("select special_id,level1,name from special where name like concat('%',#{str},'%')")
    List<Map<String,Object>> searchSpecial(String str);
}
