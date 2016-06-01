package com.architecture.realarchitecture.datasource.sync;

import com.architecture.realarchitecture.datasource.DALFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by liushuo on 16/3/30.
 */
public class SqlSyncStrategy extends BaseMemorySyncStrategy {
    public static final BaseMemorySyncStrategy mStrategy = new SqlSyncStrategy();

    private SqlSyncStrategy() {
    }

    @Override
    public List<Map<String, Object>> syncArray(String dataType) {
        List<Map<String, Object>> datas = DALFactory.getAdvanceStorage().queryItemsByTypes(dataType);
        return datas;
    }

    @Override
    public Map<String, Object> syncObject(String dataType, String id) {
        List<Map<String, Object>> datas = DALFactory.getAdvanceStorage().queryItemsByIds(dataType, id);
        if (datas.size() > 0) {
            Map<String, Object> data = datas.get(0);
            return data;
        }
        return null;
    }
}
