package com.lhhh.mapper;

import org.apache.ibatis.annotations.Param;
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

    @Select("select special_id,degree,limit_year,level2_name,level3_name,name from special where level1=1")
    List<Map<String,Object>> findSpecials1();

    @Select("select special_id,degree,limit_year,level2_name,level3_name,name from special where level1=2")
    List<Map<String,Object>> findSpecials2();

    @Select("select * from special where special_id=#{id}")
    Map<String, Object> findSpecialById(Integer id);
    @Select("select * from special_impress where special_id=#{id}")
    List<Map<String, Object>> findSpecialImpress(Integer id);
    @Select("select * from special_jobrate where special_id=#{id}")
    List<Map<String, Object>> findSpecialJobRate(Integer id);

    @Select("select salaryRank from (select a.special_id,IFNULL(a.salary,0) salary,(@rowNum:=@rowNum+1) as salaryRank" +
            " from special a," +
            " (Select (@rowNum :=0) ) b" +
            " order by salary DESC ) c where c.special_id=#{id}")
    Integer findSpecialSalaryRank(Integer id);

    @Select("SELECT special_id,name from special where level3=#{level3} and special_id!=#{special_id}")
    List<Map<String, Object>> findSimilarSpecial(@Param("level3") Integer level3, @Param("special_id") Integer special_id);

    List<Map<String, Object>> findSchoolBySpId(Integer id);

    @Select("select * from special_salary where special_id = #{id}")
    Map<String, Object> find10YearsSalary(Integer id);


    @Select("select * from special_jobdetail where special_id = #{id}")
    List<Map<String, Object>> findSpecialJobDetail(Integer id);


}
