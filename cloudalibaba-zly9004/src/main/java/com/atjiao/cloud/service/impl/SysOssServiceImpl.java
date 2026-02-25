package com.atjiao.cloud.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atjiao.cloud.domain.SysOss;
import com.atjiao.cloud.service.SysOssService;
import com.atjiao.cloud.mapper.SysOssMapper;
import org.springframework.stereotype.Service;
import com.atjiao.cloud.Enum.BizType;
import com.atjiao.cloud.domain.vo.SysOssVo;
import com.atjiao.cloud.util.OssUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.ArrayList;
import java.util.List;

/**
* @author 焦叶鹏
* @description 针对表【sys_oss】的数据库操作Service实现
* @createDate 2025-07-05 20:30:33
*/
@Service
public class SysOssServiceImpl extends ServiceImpl<SysOssMapper, SysOss>
    implements SysOssService{

    @Autowired
    private OssUtil ossUtil;

    @Override
    public SysOssVo uploadToOss(MultipartFile file, Long bizId, String bizType) {
        // 复用批量上传实现
        List<MultipartFile> list = new ArrayList<>();
        list.add(file);
        List<SysOssVo> vos = uploadToOss(list, bizId, bizType);
        return vos.isEmpty() ? null : vos.get(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<SysOssVo> uploadToOss(List<MultipartFile> files, Long bizId, String bizType) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        // 1. 校验业务目录
        String bizDirectory = BizType.getDirectory(bizType);
        if (bizDirectory == null) {
            throw new IllegalArgumentException("无效的业务类型: " + bizType);
        }
        // 2. 查询当前最大sort，便于连续递增
        int nextSort = 1;
        LambdaQueryWrapper<SysOss> query = new LambdaQueryWrapper<>();
        query.eq(SysOss::getBizId, bizId).eq(SysOss::getBizType, bizType).orderByDesc(SysOss::getSort).last("limit 1");
        SysOss last = this.getOne(query);
        if (last != null && last.getSort() != null) {
            nextSort = last.getSort() + 1;
        }
        // 3. 循环上传与入库
        List<SysOssVo> result = new ArrayList<>(files.size());
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue; // 跳过空文件
            }
            // 3.1 计算OSS路径：travel/{业务目录}/{文件名}
            String fileName = file.getOriginalFilename();
            String ossPath = bizDirectory + "/" + (fileName == null ? System.currentTimeMillis() + "" : fileName);
            // 3.2 上传到OSS
            String url;
            try {
                url = ossUtil.uploadFile(file, ossPath);
            } catch (Exception e) {
                throw new RuntimeException("文件上传失败: " + (fileName == null ? "unknown" : fileName) + ", " + e.getMessage(), e);
            }
            // 3.3 组装实体并入库
            SysOss sysOss = new SysOss();
            sysOss.setFileName(fileName);
            sysOss.setOriginalName(fileName);
            sysOss.setFileSuffix(fileName != null && fileName.contains(".") ? fileName.substring(fileName.lastIndexOf('.') + 1) : "");
            sysOss.setUrl(url);
            sysOss.setService("aliyun");
            sysOss.setBizType(bizType);
            sysOss.setBizId(bizId);
            sysOss.setSort(nextSort++);
            this.save(sysOss);
            // 3.4 映射为VO
            SysOssVo vo = new SysOssVo();
            BeanUtils.copyProperties(sysOss, vo);
            result.add(vo);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAndResort(Long ossId) {
        // 1. 查询要删除的记录
        SysOss toDelete = this.getById(ossId);
        if (toDelete == null) {
            throw new IllegalArgumentException("未找到对应的文件记录");
        }
        // 2. 逻辑删除（假设delFlag=1为删除，0为未删除）
        toDelete.setDelFlag(1);
        boolean deleted = this.updateById(toDelete);
        if (!deleted) return false;
        // 3. 查询同bizType和bizId下未删除的所有记录，按sort升序
        LambdaQueryWrapper<SysOss> query2 = new LambdaQueryWrapper<>();
        query2.eq(SysOss::getBizType, toDelete.getBizType())
                .eq(SysOss::getBizId, toDelete.getBizId())
                .eq(SysOss::getDelFlag, 0)
                .orderByAsc(SysOss::getSort);
        List<SysOss> ossList = this.list(query2);
        // 4. 重新排序
        int sort = 1;
        for (SysOss oss : ossList) {
            oss.setSort(sort++);
        }
        // 5. 批量更新
        return this.updateBatchById(ossList);
    }
}




