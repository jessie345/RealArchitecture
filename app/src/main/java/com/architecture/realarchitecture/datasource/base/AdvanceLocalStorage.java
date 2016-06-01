package com.architecture.realarchitecture.datasource.base;

import java.util.List;
import java.util.Map;

/**
 * Created by liushuo on 16/4/5.
 * 高级数据存储服务访问接口，提供基本数据存储服务和高级查询功能
 */
public interface AdvanceLocalStorage extends BaseLocalStorage {

    List<Map<String, Object>> queryItemsByColumn(String dataType, String column, Object... values);

    /**
     * row id 由系统维护，自增
     *
     * @param dataType
     */
    void resetInternalRowId(String dataType);
}
