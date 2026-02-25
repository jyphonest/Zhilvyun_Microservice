package com.atjiao.cloud.Enum;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author 焦叶鹏
 * @data 2025/11/12
 * @description: 商品类型枚举类
 **/
public enum GoodsTypeEnum {

    // 旅游商品类型
    TICKET("门票", 1001),
    HOTEL("酒店", 1002),
    FLIGHT("机票", 1003),
    TRAIN("火车票", 1004),
    BUS("汽车票", 1005),
    SCENIC_SPOT("景点门票", 1006),
    TRAVEL_PACKAGE("旅游套餐", 1007),
    GUIDE_SERVICE("导游服务", 1008),
    INSURANCE("旅游保险", 1009),
    
    // 旅游装备
    LUGGAGE("行李箱", 1101),
    BACKPACK("背包", 1102),
    CAMERA("相机", 1103),
    TENT("帐篷", 1104),
    SLEEPING_BAG("睡袋", 1105),
    
    // 旅游特产
    LOCAL_FOOD("地方特产", 1201),
    SOUVENIR("纪念品", 1202),
    HANDICRAFT("手工艺品", 1203),
    
    // 旅游服务
    CAR_RENTAL("租车服务", 1301),
    TRANSLATION("翻译服务", 1302),
    PHOTOGRAPHY("摄影服务", 1303),

    //常用类型
    LIMITEDTIME_EXCHANGE("限时兑换",1401),
    LIMITEDTIME_EVENT("限时活动",1402),
    PERSONAL_CREATION("个人创建",1403),
    PERSONAL_PURCHASE("个人购买",1404),
    
    // 其他
    OTHER("其他", 9999);

    private final String label;
    private final int code;

    GoodsTypeEnum(String label, int code) {
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
     * 获取code值
     * @return code值
     */
    public int getCode() {
        return code;
    }

    /**
     * 根据code查询标签值
     * @param code 商品类型code
     * @return 标签值
     */
    public static String getLabelByCode(int code) {
        for (GoodsTypeEnum goodsType : values()) {
            if (goodsType.code == code) {
                return goodsType.label;
            }
        }
        throw new IllegalArgumentException("无效的商品类型代码: " + code);
    }

    /**
     * 根据标签值查询code
     * @param label 商品类型标签
     * @return code值
     */
    public static int getCodeByLabel(String label) {
        if (label == null || label.trim().isEmpty()) {
            throw new IllegalArgumentException("商品类型标签不能为空");
        }
        
        for (GoodsTypeEnum goodsType : values()) {
            if (goodsType.label.equals(label.trim())) {
                return goodsType.code;
            }
        }
        throw new IllegalArgumentException("无效的商品类型标签: " + label);
    }

    /**
     * 根据code获取枚举实例
     * @param code 商品类型code
     * @return 枚举实例
     */
    public static GoodsTypeEnum fromCode(int code) {
        for (GoodsTypeEnum goodsType : values()) {
            if (goodsType.code == code) {
                return goodsType;
            }
        }
        throw new IllegalArgumentException("无效的商品类型代码: " + code);
    }

    /**
     * 根据标签值获取枚举实例
     * @param label 商品类型标签
     * @return 枚举实例
     */
    public static GoodsTypeEnum fromLabel(String label) {
        if (label == null || label.trim().isEmpty()) {
            throw new IllegalArgumentException("商品类型标签不能为空");
        }
        
        for (GoodsTypeEnum goodsType : values()) {
            if (goodsType.label.equals(label.trim())) {
                return goodsType;
            }
        }
        throw new IllegalArgumentException("无效的商品类型标签: " + label);
    }

    /**
     * 获取所有商品类型映射
     * @return 商品类型映射（code -> label）
     */
    public static Map<Integer, String> getAllGoodsTypes() {
        Map<Integer, String> map = new LinkedHashMap<>();
        for (GoodsTypeEnum goodsType : values()) {
            map.put(goodsType.code, goodsType.label);
        }
        return map;
    }

    /**
     * 验证code是否有效
     * @param code 商品类型code
     * @return 是否有效
     */
    public static boolean isValidCode(int code) {
        for (GoodsTypeEnum goodsType : values()) {
            if (goodsType.code == code) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证标签值是否有效
     * @param label 商品类型标签
     * @return 是否有效
     */
    public static boolean isValidLabel(String label) {
        if (label == null || label.trim().isEmpty()) {
            return false;
        }
        
        for (GoodsTypeEnum goodsType : values()) {
            if (goodsType.label.equals(label.trim())) {
                return true;
            }
        }
        return false;
    }
}
