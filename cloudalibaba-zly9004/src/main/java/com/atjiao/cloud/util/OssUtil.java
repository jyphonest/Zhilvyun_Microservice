package com.atjiao.cloud.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.atjiao.cloud.config.OssConfig;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

/**
 * 阿里云OSS上传工具类
 */
@Component
public class OssUtil {
    @Resource
    private OssConfig ossConfig;
    private OSS ossClient;

    @PostConstruct
    public void init() {
        ossClient = new OSSClientBuilder().build(
                ossConfig.getEndPoint(),
                ossConfig.getAccessKeyId(),
                ossConfig.getAccessKeySecret()
        );
    }

    /**
     * 上传文件到OSS
     * @param file 文件
     * @param ossPath OSS路径（如travel/xxx/xxx.jpg）
     * @return 文件url
     */
    public String uploadFile(MultipartFile file, String ossPath) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            ossClient.putObject(ossConfig.getBucketName(), ossPath, inputStream);
        }
        // 拼接url
        return generateUrl(ossPath);
    }

    /**
     * 生成文件访问url
     * @param ossPath OSS路径
     * @return url
     */
    public String generateUrl(String ossPath) {
        return String.format("https://%s.%s/%s",
                ossConfig.getBucketName(),
                ossConfig.getEndPoint().replace("http://","").replace("https://","") ,
                ossPath);
    }
}
