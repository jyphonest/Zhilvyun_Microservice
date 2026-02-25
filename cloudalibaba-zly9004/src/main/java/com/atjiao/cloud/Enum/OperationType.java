package com.atjiao.cloud.Enum;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author 焦叶鹏
 * @data 2025/9/8 21:15
 * @description: 操作类型枚举
 **/
public enum OperationType {
    // 审核通过
    THROUGH("通过", 1),
    FAILED("不通过", 2),
    RELEASE("发布", 3),
    SUSPEND("暂停中", 4),
    INEFFECT("生效中", 5),
    EXPIRE("过期", 6);

    private final String label;
    private final int code;

    OperationType(String label, int code) {
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
     * @param code 操作类型code
     * @return 标签值
     */
    public static String getLabelByCode(int code) {
        for (OperationType operation : values()) {
            if (operation.code == code) {
                return operation.label;
            }
        }
        throw new IllegalArgumentException("无效的操作类型: " + code);
    }

    /**
     * 根据code获取name（兼容旧版本）
     * @param code 操作类型code
     * @return 标签值
     */
    public static String getNameByCode(Integer code) {
        if (code == null) {
            throw new IllegalArgumentException("操作类型不能为空");
        }
        return getLabelByCode(code);
    }

    /**
     * 根据标签值查询code
     * @param label 操作类型标签
     * @return code值
     */
    public static int getCodeByLabel(String label) {
        if (label == null || label.trim().isEmpty()) {
            throw new IllegalArgumentException("操作类型标签不能为空");
        }
        
        for (OperationType operation : values()) {
            if (operation.label.equals(label.trim())) {
                return operation.code;
            }
        }
        throw new IllegalArgumentException("无效的操作类型: " + label);
    }

    /**
     * 根据name获取code（兼容旧版本）
     * @param name 操作类型名称
     * @return code值
     */
    public static Integer getCodeByName(String name) {
        return getCodeByLabel(name);
    }

    /**
     * 根据code获取枚举实例
     * @param code 操作类型code
     * @return 枚举实例
     */
    public static OperationType fromCode(int code) {
        for (OperationType operation : values()) {
            if (operation.code == code) {
                return operation;
            }
        }
        throw new IllegalArgumentException("无效的操作类型: " + code);
    }

    /**
     * 根据标签值获取枚举实例
     * @param label 操作类型标签
     * @return 枚举实例
     */
    public static OperationType fromLabel(String label) {
        if (label == null || label.trim().isEmpty()) {
            throw new IllegalArgumentException("操作类型标签不能为空");
        }
        
        for (OperationType operation : values()) {
            if (operation.label.equals(label.trim())) {
                return operation;
            }
        }
        throw new IllegalArgumentException("无效的操作类型: " + label);
    }

    /**
     * 获取所有操作类型映射
     * @return 操作类型映射（code -> label）
     */
    public static Map<Integer, String> getAllOperationTypes() {
        Map<Integer, String> map = new LinkedHashMap<>();
        for (OperationType operation : values()) {
            map.put(operation.code, operation.label);
        }
        return map;
    }

    /**
     * 验证code是否有效
     * @param code 操作类型code
     * @return 是否有效
     */
    public static boolean isValidCode(int code) {
        for (OperationType operation : values()) {
            if (operation.code == code) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证标签值是否有效
     * @param label 操作类型标签
     * @return 是否有效
     */
    public static boolean isValidLabel(String label) {
        if (label == null || label.trim().isEmpty()) {
            return false;
        }
        
        for (OperationType operation : values()) {
            if (operation.label.equals(label.trim())) {
                return true;
            }
        }
        return false;
    }
}
