package com.lhhh.mapper;

import com.lhhh.bean.ProvinceScore;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: lhhh
 * @date: Created in 2020/10/24
 * @description:
 * @version:1.0
 */
@Repository
public interface ProvinceScoreMapper {
    @Select("select * from province_score")
    public List<ProvinceScore> findProvinceScoreList();
    @Select("select * from province_score where province_name = #{proName} order by year desc")
    public List<ProvinceScore> findProvinceScoreByProvinceName(String proName);
}
