package com.lhhh.mapper;

import com.lhhh.bean.School;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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


}
