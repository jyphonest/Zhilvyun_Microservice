package com.atjiao.cloud.service;

import com.atjiao.cloud.domain.SysOss;
import com.atjiao.cloud.domain.vo.SysOssVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 焦叶鹏
* @description 针对表【sys_oss】的数据库操作Service
* @createDate 2025-07-05 20:30:33
*/
public interface SysOssService extends IService<SysOss> {

    /**
     * 上传文件到OSS并保存记录（单文件）
     * @param file 文件
     * @param bizId 业务主键id
     * @param bizType 业务类型
     * @return SysOssVo
     */
    SysOssVo uploadToOss(org.springframework.web.multipart.MultipartFile file, Long bizId, String bizType);

    /**
     * 批量上传文件到OSS并保存记录（多文件）
     * @param files 文件列表
     * @param bizId 业务主键id
     * @param bizType 业务类型
     * @return SysOssVo列表
     */
    List<SysOssVo> uploadToOss(java.util.List<org.springframework.web.multipart.MultipartFile> files, Long bizId, String bizType);

    /**
     * 根据ossId逻辑删除文件，并对同一bizType和bizId下的sort重新排序
     * @param ossId oss主键id
     * @return 是否成功
     */
    boolean deleteAndResort(Long ossId);
}



