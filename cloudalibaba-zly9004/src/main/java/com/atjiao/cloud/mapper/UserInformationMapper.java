package com.atjiao.cloud.mapper;


import com.atjiao.cloud.domain.UserInformation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 焦叶鹏
* @description 针对表【user_information】的数据库操作Mapper
* @createDate 2025-07-05 20:17:43
* @Entity com.atjiao.cloud.domain.UserInformation
*/
public interface UserInformationMapper extends BaseMapper<UserInformation> {

    List<String> getAuthorityByUserId(Long id);
}




