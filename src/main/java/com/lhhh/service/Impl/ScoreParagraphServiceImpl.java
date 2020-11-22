package com.lhhh.service.Impl;

import com.lhhh.bean.ScoreParagraph;
import com.lhhh.mapper.ScoreParagraphMapper;
import com.lhhh.service.ScoreParagraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author: lhhh
 * @date: Created in 2020/10/24
 * @description:
 * @version:1.0
 */
@Service
public class ScoreParagraphServiceImpl implements ScoreParagraphService {
    @Autowired
    private ScoreParagraphMapper scoreParagraphMapper;
    @Override
    public List<String> findTypesByProvinceNameAndYear(String provinceName, Integer year) {
        return scoreParagraphMapper.findTypesByProvinceNameAndYear(provinceName,year);
    }

    @Override
    public List<Integer> findYearsByProvinceName(String provinceName) {
        return scoreParagraphMapper.findYearsByProvinceName(provinceName);
    }

    @Override
    public List<ScoreParagraph> findScoreParagraphs(Map<String, Object> chooseMap) {
        return scoreParagraphMapper.findScoreParagraphs(chooseMap);
    }
}
