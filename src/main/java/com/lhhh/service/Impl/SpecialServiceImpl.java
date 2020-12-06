package com.lhhh.service.Impl;

import com.github.pagehelper.PageHelper;
import com.lhhh.mapper.SpecialMapper;
import com.lhhh.service.SpecialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author: lhhh
 * @date: Created in 2020/11/18
 * @description:
 * @version:1.0
 */
@Service
public class SpecialServiceImpl implements SpecialService {
    @Autowired
    private SpecialMapper specialMapper;

    @Override
    public Map findSpecials() {

        List<Map<String, Object>> specials1 = specialMapper.findSpecials1();
        List<Map<String, Object>> specials2 = specialMapper.findSpecials2();


        Map<String, List<Map<String, Object>>> level2TypesMap1 = specials1.stream()
                .map(i -> {
                    String degree = i.get("degree").toString();
                    if (!(degree == null || degree.length() == 0)) {
                        i.put("degree", degree.substring(0, degree.length() - 2));
                    }
                    return i;
                })
                .collect(Collectors.groupingBy(i -> i.get("level2_name").toString()));
        //{1:[{},{}],2:[{},{}]}
        Map result1 = new HashMap();
        level2TypesMap1.forEach((key, value) -> {
            Map<String, List<Map<String, Object>>> level3TypesMap = value.stream()
                    .collect(Collectors.groupingBy(i -> i.get("level3_name").toString()));
            result1.put(key, level3TypesMap);
        });

        Map<String, List<Map<String, Object>>> level2TypesMap2 = specials2.stream()
                .map(i -> {
                    String degree = i.get("degree").toString();
                    if (!(degree == null || degree.length() == 0)) {
                        i.put("degree", degree.substring(0, degree.length() - 2));
                    }
                    return i;
                })
                .collect(Collectors.groupingBy(i -> i.get("level2_name").toString()));
        //{1:[{},{}],2:[{},{}]}
        Map result2 = new HashMap();
        level2TypesMap2.forEach((key, value) -> {
            Map<String, List<Map<String, Object>>> level3TypesMap = value.stream()
                    .collect(Collectors.groupingBy(i -> i.get("level3_name").toString()));
            result2.put(key, level3TypesMap);
        });

        Map result = new HashMap();
        result.put("1", result1);
        result.put("2", result2);
        return result;
    }

    @Override
    public Map findSpecialOne(Integer id) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> special = specialMapper.findSpecialById(id);

        if (StringUtils.isEmpty(special.get("sel_adv"))){
            special.put("sel_dev", "不限");
        }

        //男女比例
        if(StringUtils.isEmpty(special.get("rate"))){
            special.put("rate"," ");
        } else {
            String rate = special.get("rate").toString();
            System.out.println(rate);
            String[] split = rate.split(":");
            System.out.println(Arrays.toString(split));
            String rateStr = "";
            rateStr += ((int) Double.parseDouble(split[0]) + ":" + (int) Double.parseDouble(split[1]));
            special.put("rate", rateStr);
        }


        //就业率
        List<Map<String, Object>> specialJobRate = specialMapper.findSpecialJobRate(id);
        if (specialJobRate.size()!=0){
            Map<String, Object> map = specialJobRate.get(specialJobRate.size() - 1);
            String jobrate = map.get("rate").toString();
            String jobrate_1 = jobrate.split("-")[0];
            String jobrate_2 = jobrate.split("-")[1];
            jobrate_1 = jobrate_1.substring(0, jobrate_1.length() - 1);
            jobrate_2 = jobrate_2.substring(0, jobrate_2.length() - 1);
            special.put("jobrate_1", jobrate_1);
            special.put("jobrate_2", jobrate_2);
        } else {
            special.put("jobrate_1", "-- ");
            special.put("jobrate_2", " --");
        }


        //专业排名
        Integer salaryRank = specialMapper.findSpecialSalaryRank(id);
        if (StringUtils.isEmpty(salaryRank)){
            special.put("salaryRank", "--");
        }else {
            special.put("salaryRank", salaryRank);
        }


        //相似专业
        int level3 = Integer.parseInt(special.get("level3").toString());
        List<Map<String, Object>> similarSpecial = specialMapper.findSimilarSpecial(level3, id);
        result.put("similarSpecial", similarSpecial);

        //课程
        if (!StringUtils.isEmpty(special.get("learn_what"))){
            String content = special.get("learn_what").toString();
            Pattern pattern = Pattern.compile("《(.*?)》");
            Matcher matcher = pattern.matcher(content);
            List<String> learns = new ArrayList<String>();
            while (matcher.find()) {
                String tag = matcher.group(1);
                learns.add(tag);
            }
            special.put("learns", learns);
        } else {
            special.put("learns", null);
        }


        result.put("baseInfo", special);

        List<Map<String, Object>> specialImpress = specialMapper.findSpecialImpress(id);
        if (specialImpress.size()!=0){
            result.put("impress", specialImpress);
        }else {
            result.put("impress", null);
        }


        //近十年薪酬
        Map<String, Object> salary = specialMapper.find10YearsSalary(id);
        result.put("salary", salary);

        //就业行业，地区，岗位
        List<Map<String, Object>> jobDetail = specialMapper.findSpecialJobDetail(id);
        Map<Object, List<Map<String, Object>>> type = jobDetail.stream().collect(Collectors.groupingBy(i -> i.get("type")));
        Map<String, List<Map<String, Object>>> jobDetailMap = new HashMap<>();
        if (type.get(1) != null) {
            List<Map<String, Object>> type1 = type.get(1).stream().map(i -> {
                double r = Double.parseDouble(i.get("rate").toString());
                String rStr = String.format("%.1f", r);
                i.put("rate", rStr);
                return i;
            }).sorted(Comparator.comparing((Map<String, Object> h) -> (Double.parseDouble( h.get("rate").toString()))).reversed())
                    .collect(Collectors.toList());
            jobDetailMap.put("type1", type1);
        } else if (type.get(2) != null) {
            List<Map<String, Object>> type2 = type.get(2).stream()
                    .sorted(Comparator.comparing((Map<String, Object> h) -> ((Double) h.get("rate"))).reversed())
                    .collect(Collectors.toList());
            jobDetailMap.put("type2", type2);
        } else if (type.get(3) != null) {
            List<Map<String, Object>> type3 = type.get(3).stream()
                    .sorted(Comparator.comparing((Map<String, Object> h) -> ((Double) h.get("rate"))).reversed())
                    .collect(Collectors.toList());
            jobDetailMap.put("type3", type3);
        }
        System.out.println(jobDetailMap);
        result.put("jobDetail", jobDetailMap);
        return result;
    }

    @Override
    public List<Map<String, Object>> findSchoolBySpId(Map map) {
        int page = Integer.parseInt(map.get("page").toString());
        int pageCount = Integer.parseInt(map.get("pageCount").toString());
        PageHelper.startPage(page, pageCount);
        return specialMapper.findSchoolBySpId(Integer.parseInt(map.get("id").toString()));

    }


}
