package com.atjiao.cloud.Enum;

import lombok.Getter;

/**
 * @author 焦叶鹏
 * * @data 2025/7/20 16:54
 * @description: 阿里云文件名枚举类
 **/
@Getter
public enum BizType {
    TRAVEL_PLAY("travel-play", "travel/play"),
    TRAVEL_TRANSPORTATION("travel-transportation", "travel/transportation"),
    TRAVEL_CATERING("travel-catering", "travel/catering"),
    TRAVEL_ACCOMMODATE("travel-accommodate", "travel/accommodate"),
    HEADER_IMAGE("header-image", "header/image"),
    ATTACHMENT_URL("attachment-url","attachment/file"),
    OTHER_OPERATION("other-operation","other/image");
    private final String businessType;
    private final String businessDirectory;
    BizType(String businessType, String businessDirectory) {
        this.businessType = businessType;
        this.businessDirectory = businessDirectory;
    }
    public static String getDirectory(String businessType) {
        for (BizType business : BizType.values()) {
            if (business.getBusinessType().equals(businessType)) {
                return business.getBusinessDirectory();
            }
        }
        return null;
    }
}