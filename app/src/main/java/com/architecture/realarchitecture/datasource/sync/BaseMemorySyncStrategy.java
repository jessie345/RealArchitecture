package com.architecture.realarchitecture.datasource.sync;

import java.util.List;
import java.util.Map;

/**
 * Created by liushuo on 16/3/30.
 * 内存池的本地数据同步方案
 */
public class BaseMemorySyncStrategy {

    public Map<String, Object> syncObject(String dataType, String id) {
        return null;
    }

    public List<Map<String, Object>> syncArray(String dataType) {
        return null;
    }
}
