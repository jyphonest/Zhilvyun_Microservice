package com.atjiao.cloud.controller;

import com.atjiao.cloud.domain.vo.SysOssVo;
import com.atjiao.cloud.resp.ResultData;
import com.atjiao.cloud.service.SysOssService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 焦叶鹏
 * * @data 2025/7/20 18:15
 * @description: TODO
 **/
@RequiredArgsConstructor
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    private final SysOssService sysOssService;

    /**
     * 文件上传接口（支持多文件）
     * - 兼容字段名：files 或 file
     * - Content-Type: multipart/form-data
     * @param fileArray 多文件字段：file（有些客户端使用此字段名）
     * @param bizId 业务主键id
     * @param bizType 业务类型
     * @return 多个文件的SysOssVo列表
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResultData<List<SysOssVo>> upload(
            @RequestPart(value = "file", required = false) MultipartFile[] fileArray,
            @RequestParam(value = "bizId",required = false) Long bizId,
            @RequestParam(value = "bizType",required = false) String bizType) {

        log.info("上传文件，bizId: {}, bizType: {}", bizId, bizType);
        List<MultipartFile> allFiles = new ArrayList<>();

        if (fileArray != null && fileArray.length > 0) {
            allFiles.addAll(Arrays.asList(fileArray));
        }
        if (allFiles.isEmpty()) {
            return ResultData.fail("400", "未接收到文件，请使用字段名 files 或 file 提交");
        }
        return ResultData.success(sysOssService.uploadToOss(allFiles, bizId, bizType));
    }

    /**
     * 文件删除接口
     * @param ossId oss主键id
     * @return 是否成功
     */
    @PostMapping("/delete")
    public ResultData<Boolean> delete(@RequestParam("ossId") Long ossId) {
        return ResultData.success(sysOssService.deleteAndResort(ossId));
    }
}
