package com.lhhh.mapper;

import com.lhhh.bean.ProvinceScore;
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
public interface ProvinceScoreMapper {
    @Select("select * from province_score")
    List<ProvinceScore> findProvinceScoreList();
    @Select("select * from province_score where province_name = #{proName} and year=#{year}")
    List<ProvinceScore> findProvinceScoreByProvinceNameAndYear(@Param("proName") String proName,@Param("year") Integer year);
    @Select("select distinct province_name from province_score")
    List<String> getAllProvince();

    @Select("select score from province_score where province_name = #{provinceName} and batch_name=#{batchName} and year=#{year} and type=#{curriculum}")
    Integer findProvinceScore(Map<String,Object> map);

    List<Integer> findProvinceScores(Map<String,Object> map);
}
