package com.lhhh.mapper;

import com.lhhh.bean.ScoreParagraph;
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
public interface ScoreParagraphMapper {

    @Select("select * from score_paragraph")
    public List<ScoreParagraph> findScoreParagraphList();
}
