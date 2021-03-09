package com.lhhh.mapper;

import com.lhhh.bean.School;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author: lhhh
 * @date: Created in 2020/11/7
 * @description:
 * @version:1.0
 */
@Repository
public interface SchoolMapper {

    /**
     * 找大学首页的数据
     * @return
     */
    List<School> findSchoolsOrderByXyhRank(@Param("chooseLocations") List<String> chooseLocations, @Param("chooseSchoolTypes") List<String> chooseSchoolTypes, @Param("chooseBanxueTypes") List<String> chooseBanxueTypes, @Param("chooseSchoolLevels") List<String> chooseSchoolLevels);

    Integer findSchoolsCount(@Param("chooseLocations") List<String> chooseLocations, @Param("chooseSchoolTypes") List<String> chooseSchoolTypes, @Param("chooseBanxueTypes") List<String> chooseBanxueTypes, @Param("chooseSchoolLevels") List<String> chooseSchoolLevels);

    @Select("select * from school where id = #{id}")
    Map<String, Object> findOne(Integer id);

    @Select("select * from dualclass where school_id = #{id}")
    List<Map<String, Object>> findDualClass(Integer id);

    @Select("select * from image where school_id=#{id}")
    List<Map<String, String>> findSchoolImages(Integer id);

    @Select("select * from school_special where school_id=#{id}")
    List<Map<String, String>> findSchoolSpecial(Integer id);

    @Select("select * from school_job_detail where school_id=#{id}")
    List<Map<String, String>> findSchoolJobDetail(Integer id);

    @Select("select special.special_id,special.name,special.salary from school_special left join special on school_special.special_id=special.special_id where school_id=#{id} and salary is not null ORDER BY salary desc limit 5")
    List<Map<String, String>> findBestJobSpecial(Integer id);

    @Select("select year,min_score from school_score where sss_id = (select id from school_score_selector where school_name=#{schoolName} and province_name=#{provinceName} and curriculum='理科' and batch_name=#{batchName}) ORDER BY year desc LIMIT 1")
    Map findMinScore(Map map);

    @Select("select * from school_score where sss_id=(select id from school_score_selector where school_name=#{schoolName} and province_name=#{provinceName} and curriculum='理科' and batch_name=#{batchName}) ORDER BY year desc")
    List<Map> findSchoolScore(Map map);

    @Select("SELECT id,name,badge_url,view_week FROM `school` ORDER BY view_week desc,id")
    List<Map> findSchoolRankByViewWeek();

    @Select("SELECT id,name,badge_url,view_month FROM `school` ORDER BY view_month desc,id")
    List<Map> findSchoolRankByViewMonth();

    @Select("SELECT id,name,badge_url,view_total FROM `school` ORDER BY view_total desc,id")
    List<Map> findSchoolRankByViewTotal();

    @Select("SELECT id,name,badge_url,create_date FROM `school` where create_date is not null and create_date !='0' ORDER BY create_date")
    List<Map> findSchoolRankByCreateDate();









}
