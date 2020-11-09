package com.lhhh.service.Impl;

import com.lhhh.bean.ProvinceScore;
import com.lhhh.mapper.ProvinceScoreMapper;
import com.lhhh.service.ProvinceScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: lhhh
 * @date: Created in 2020/11/4
 * @description:
 * @version:1.0
 */
@Service
public class ProvinceScoreServiceImpl implements ProvinceScoreService {
    @Autowired
    private ProvinceScoreMapper provinceScoreMapper;

    @Override
    public List<ProvinceScore> findProvinceScoreByProvinceNameAndYear(String provinceName, Integer year) {
        return provinceScoreMapper.findProvinceScoreByProvinceNameAndYear(provinceName, year);
    }

    @Override
    public List<String> getAllProvince() {
        return provinceScoreMapper.getAllProvince();
    }
}
