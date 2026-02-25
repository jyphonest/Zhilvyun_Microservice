package com.atjiao.cloud.controller;

import com.atjiao.cloud.mapper.GoodsCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 焦叶鹏
 * * @data 2025/7/19 20:26
 * @description: TODO
 **/
@RestController
@RequestMapping("/goodsCategory")
@RequiredArgsConstructor
public class GoodsCategoryController {

    private GoodsCategoryMapper goodsCategoryMapper;
}
