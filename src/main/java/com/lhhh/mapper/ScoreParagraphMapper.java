package com.lhhh.mapper;

import com.lhhh.bean.ScoreParagraph;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author: lhhh
 * @date: Created in 2020/10/24
 * @description:
 * @version:1.0
 */
@Repository
public interface ScoreParagraphMapper {

    @Select("select * from score_paragraph where province=#{chooseProvince} and year =#{chooseYear} and type=#{chooseType}")
    List<ScoreParagraph> findScoreParagraphs(Map<String,Object> chooseMap);

    @Select("select distinct year from score_paragraph where province=#{provinceName} order by year desc")
    List<Integer> findYearsByProvinceName(String provinceName);

    @Select("select distinct type from score_paragraph where province=#{provinceName} and year =#{year}")
    List<String> findTypesByProvinceNameAndYear(@Param("provinceName") String provinceName, @Param("year") Integer year);

    /**
     * @param map 省份，年份，分科情况，分数
     * @return 排名
     */
    @Select("SELECT (" +
            "(select number from score_paragraph where score >=#{score} and province=#{provinceName} and year =#{year} and type=#{type} ORDER BY score asc limit 1)+" +
            "(select number from score_paragraph where score <=#{score} and province=#{provinceName} and year =#{year} and type=#{type} ORDER BY score desc limit 1))" +
            "/2")
    Integer findNumberByScore(Map<String, Object> map);

    /**
     * @param map 省份，年份，分科情况，排名
     * @return 分数
     */
    @Select("select (" +
            "(select score from score_paragraph where number >= #{number} and province=#{provinceName} and year =#{year} and type=#{type} ORDER BY number asc LIMIT 1)+" +
            "(select score from score_paragraph where number <= #{number} and province=#{provinceName} and year =#{year} and type=#{type} ORDER BY number desc LIMIT 1)" +
            ")/2")
    Double findScoreByNumber(Map<String, Object> map);

}
