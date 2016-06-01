package com.architecture.realarchitecture.datasource.sync;

import com.architecture.realarchitecture.datasource.base.BaseLocalStorage;
import com.architecture.realarchitecture.datasource.DALFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by liushuo on 16/3/30.
 */
public class NoSqlSyncStrategy extends BaseMemorySyncStrategy {
    public static final BaseMemorySyncStrategy mStrategy = new NoSqlSyncStrategy();

    private NoSqlSyncStrategy() {
    }

    @Override
    public List<Map<String, Object>> syncArray(String dataType) {
        BaseLocalStorage bls = DALFactory.getBaseStorage();
        if (bls != null) {
            List<Map<String, Object>> datas = bls.queryItemsByTypes(dataType);
            return datas;
        }
        return null;
    }

    @Override
    public Map<String, Object> syncObject(String dataType, String id) {

        BaseLocalStorage bls = DALFactory.getBaseStorage();


        if (bls != null) {

            List<Map<String, Object>> datas = bls.queryItemsByIds(dataType, id);

            if (datas.size() > 0) {
                Map<String, Object> data = datas.get(0);
                return data;
            }
        }
        return null;
    }
}
