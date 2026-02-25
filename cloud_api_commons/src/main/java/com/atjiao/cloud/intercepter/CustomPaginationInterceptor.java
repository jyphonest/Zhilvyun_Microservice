package com.atjiao.cloud.intercepter;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ParameterUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.SQLException;

/**
 * 自定义分页拦截器
 * 支持 pageSize=-1 查询所有记录
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomPaginationInterceptor extends PaginationInnerInterceptor {

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        // 获取分页参数
        IPage<?> page = (IPage) ParameterUtils.findPage(parameter).orElse(null);
        if (ObjUtil.isNotNull(page)) {
            if (page.getSize() == -1) {
                return;
            }
        }
        super.beforeQuery(executor, ms, parameter, rowBounds, resultHandler, boundSql);
    }
}
