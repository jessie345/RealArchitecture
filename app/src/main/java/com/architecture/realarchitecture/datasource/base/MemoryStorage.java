package com.architecture.realarchitecture.datasource.base;

import com.architecture.realarchitecture.datasource.sync.BaseMemorySyncStrategy;

import java.util.List;
import java.util.Map;

/**
 * Created by liushuo on 16/3/19.
 * 内存池对外访问接口
 */
public interface MemoryStorage {

    //缓存非列表数据到内存
    void cacheObjectDataInMemory(String dataType, String id, Map<String, Object> data);

    //缓存列表数据到内存
    void cacheArrayDatasInMemory(String dataType, List<Map<String, Object>> datas);

    //根据id查找数据
    Map<String, Object> getObjectDataForId(String dataType, final String id);

    //获取指定类型的所有数据
    List<Map<String, Object>> getArrayDatasCached(final String dataType);

    //配置内存池的同步策略
    MemoryStorage configureSyncStrategy(BaseMemorySyncStrategy syncStrategy);


}
