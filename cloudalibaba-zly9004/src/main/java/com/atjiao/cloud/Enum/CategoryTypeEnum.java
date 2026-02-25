package com.atjiao.cloud.Enum;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author 焦叶鹏
 * @data 2025/7/20 16:21
 * @description: 商品分类类型枚举
 **/
public enum CategoryTypeEnum {

    // 新增分类 - 家居
    HOME_APPLIANCES("家用电器", 101),
    FURNITURE("家具", 102),
    DECOR("家居装饰", 103),
    KITCHENWARE("厨具", 104),

    // 新增分类 - 美妆个护
    COSMETICS("化妆品", 201),
    SKINCARE("护肤品", 202),
    HAIRCARE("护发品", 203),
    PERSONAL_CARE("个人护理", 204),

    // 新增分类 - 母婴
    BABY_PRODUCTS("婴儿用品", 301),
    MATERNITY("孕妇用品", 302),
    TOYS("玩具", 303),
    BABY_FOOD("婴儿食品", 304),

    // 新增分类 - 运动户外
    SPORTS_EQUIPMENT("运动器材", 401),
    OUTDOOR_GEAR("户外装备", 402),
    FITNESS("健身器材", 403),
    CYCLING("骑行装备", 404),

    // 新增分类 - 数码配件
    PHONE_ACCESSORIES("手机配件", 501),
    COMPUTER_ACCESSORIES("电脑配件", 502),
    CAMERA_ACCESSORIES("相机配件", 503),
    POWER_BANKS("移动电源", 504),

    // 新增分类 - 汽车用品
    CAR_ACCESSORIES("汽车内饰", 601),
    CAR_CARE("汽车保养", 602),
    CAR_ELECTRONICS("车载电子", 603),
    CAR_SEATS("儿童安全座椅", 604),

    // 基础分类
    ELECTRONICS("电子产品", 701),
    CLOTHING("服装", 702),
    FOOD("食品", 703),
    BOOKS("图书", 704);

    private final String label;
    private final int code;

    CategoryTypeEnum(String label, int code) {
        this.label = label;
        this.code = code;
    }

    /**
     * 获取标签值
     * @return 标签值
     */
    public String getLabel() {
        return label;
    }

    /**
     * 获取标签值（兼容旧版本）
     * @return 标签值
     */
    public String getName() {
        return label;
    }

    /**
     * 获取code值
     * @return code值
     */
    public int getCode() {
        return code;
    }

    /**
     * 根据code查询标签值
     * @param code 分类代码
     * @return 标签值
     */
    public static String getLabelByCode(int code) {
        for (CategoryTypeEnum category : values()) {
            if (category.code == code) {
                return category.label;
            }
        }
        throw new IllegalArgumentException("无效的分类代码: " + code);
    }

    /**
     * 根据code直接获取name（兼容旧版本）
     * @param code 分类代码
     * @return 标签值
     */
    public static String getNameByCode(int code) {
        return getLabelByCode(code);
    }

    /**
     * 根据标签值查询code
     * @param label 分类标签
     * @return code值
     */
    public static int getCodeByLabel(String label) {
        if (label == null || label.trim().isEmpty()) {
            throw new IllegalArgumentException("分类标签不能为空");
        }
        
        for (CategoryTypeEnum category : values()) {
            if (category.label.equals(label.trim())) {
                return category.code;
            }
        }
        throw new IllegalArgumentException("无效的分类标签: " + label);
    }

    /**
     * 根据code获取枚举实例
     * @param code 分类代码
     * @return 枚举实例
     */
    public static CategoryTypeEnum fromCode(int code) {
        for (CategoryTypeEnum category : values()) {
            if (category.code == code) {
                return category;
            }
        }
        throw new IllegalArgumentException("无效的分类代码: " + code);
    }

    /**
     * 根据标签值获取枚举实例
     * @param label 分类标签
     * @return 枚举实例
     */
    public static CategoryTypeEnum fromLabel(String label) {
        if (label == null || label.trim().isEmpty()) {
            throw new IllegalArgumentException("分类标签不能为空");
        }
        
        for (CategoryTypeEnum category : values()) {
            if (category.label.equals(label.trim())) {
                return category;
            }
        }
        throw new IllegalArgumentException("无效的分类标签: " + label);
    }

    /**
     * 获取所有分类映射
     * @return 分类映射（code -> label）
     */
    public static Map<Integer, String> getAllCategories() {
        Map<Integer, String> map = new LinkedHashMap<>();
        for (CategoryTypeEnum category : values()) {
            map.put(category.code, category.label);
        }
        return map;
    }

    /**
     * 验证code是否有效
     * @param code 分类代码
     * @return 是否有效
     */
    public static boolean isValidCode(int code) {
        for (CategoryTypeEnum category : values()) {
            if (category.code == code) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证标签值是否有效
     * @param label 分类标签
     * @return 是否有效
     */
    public static boolean isValidLabel(String label) {
        if (label == null || label.trim().isEmpty()) {
            return false;
        }
        
        for (CategoryTypeEnum category : values()) {
            if (category.label.equals(label.trim())) {
                return true;
            }
        }
        return false;
    }
}
