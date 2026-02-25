package com.atjiao.cloud.config.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 自定义TypeHandler，用于处理List<String>类型与JSON字符串之间的转换
 * 
 * @author atjiao
 * @since 2024-01-01
 */
@MappedTypes(List.class)
public class ListTypeHandler extends BaseTypeHandler<List<String>> {

    /**
     * 设置非空参数
     * 
     * @param ps PreparedStatement
     * @param i 参数位置
     * @param parameter 参数值
     * @param jdbcType JDBC类型
     * @throws SQLException SQL异常
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) 
            throws SQLException {
        if (parameter != null) {
            ps.setString(i, JSON.toJSONString(parameter));
        } else {
            ps.setString(i, null);
        }
    }

    /**
     * 根据列名获取可空结果
     * 
     * @param rs ResultSet
     * @param columnName 列名
     * @return List<String>结果
     * @throws SQLException SQL异常
     */
    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return parseJsonToList(value);
    }

    /**
     * 根据列索引获取可空结果
     * 
     * @param rs ResultSet
     * @param columnIndex 列索引
     * @return List<String>结果
     * @throws SQLException SQL异常
     */
    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return parseJsonToList(value);
    }

    /**
     * 根据存储过程获取可空结果
     * 
     * @param cs CallableStatement
     * @param columnIndex 列索引
     * @return List<String>结果
     * @throws SQLException SQL异常
     */
    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return parseJsonToList(value);
    }

    /**
     * 解析JSON字符串为List<String>
     * 
     * @param json JSON字符串
     * @return List<String>结果
     */
    private List<String> parseJsonToList(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return JSON.parseObject(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            // 如果解析失败，返回null
            return null;
        }
    }
} 