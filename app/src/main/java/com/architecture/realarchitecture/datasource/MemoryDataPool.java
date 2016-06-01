package com.architecture.realarchitecture.datasource;

import android.text.TextUtils;

import com.architecture.realarchitecture.datasource.base.MemoryStorage;
import com.architecture.realarchitecture.datasource.sync.BaseMemorySyncStrategy;
import com.architecture.realarchitecture.utils.LogUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liushuo on 16/3/19.
 */
public class MemoryDataPool implements MemoryStorage {
    private static final int CACHE_SIZE = 10;

    //缓存某种类型的数据<dataType(dataType or table name),<id,数据>>
    Map<String, Map<String, Map<String, Object>>> mObjectDatas = newLruMap("object datas", CACHE_SIZE);

    Map<String, List<Map<String, Object>>> mArrayDatas = newLruMap("array datas", CACHE_SIZE);

    private ThreadLocal<BaseMemorySyncStrategy> mMemorySyncStrategy = new ThreadLocal<>();

    private static MemoryDataPool mInstance;

    private MemoryDataPool() {
    }

    public static MemoryDataPool getInstance() {
        synchronized (MemoryDataPool.class) {
            if (mInstance == null) {
                mInstance = new MemoryDataPool();
            }
            mInstance.configureSyncStrategy(null);
        }
        return mInstance;
    }

    @Override
    public MemoryDataPool configureSyncStrategy(BaseMemorySyncStrategy syncStrategy) {
        mMemorySyncStrategy.set(syncStrategy);
        return this;
    }


    @Override
    public void cacheObjectDataInMemory(String dataType, String id, Map<String, Object> data) {
        if (!TextUtils.isEmpty(dataType) && !TextUtils.isEmpty(id) && data != null && data.size() > 0) {
            synchronized (mObjectDatas) {
                Map<String, Map<String, Object>> datasOfType = mObjectDatas.get(dataType);
                if (datasOfType == null) {
                    datasOfType = newLruMap("element count of type :" + dataType, CACHE_SIZE);
                    mObjectDatas.put(dataType, datasOfType);
                }
                datasOfType.put(id, data);
            }
        }
    }

    @Override
    public void cacheArrayDatasInMemory(String dataType, List<Map<String, Object>> datas) {
        if (!TextUtils.isEmpty(dataType) && datas != null) {
            synchronized (mArrayDatas) {
                mArrayDatas.put(dataType, datas);
            }
        }
    }

    /**
     * 内存有数据，使用内存数据，内存无数据，读取本地数据并存储到内存
     *
     * @param dataType
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> getObjectDataForId(String dataType, String id) {
        Map<String, Object> data = null;
        if (!TextUtils.isEmpty(dataType) && !TextUtils.isEmpty(id)) {
            if (mObjectDatas.containsKey(dataType)) {
                Map<String, Map<String, Object>> datasOfType = mObjectDatas.get(dataType);
                if (datasOfType != null && datasOfType.containsKey(id)) {
//                    LogUtils.d("内存数据返回,dataType:" + dataType);
                    data = datasOfType.get(id);
                }
            }
            if (mMemorySyncStrategy.get() != null && data == null) {
                //内存无数据，取本地
                if (!TextUtils.isEmpty(dataType) && !TextUtils.isEmpty(id)) {
                    data = mMemorySyncStrategy.get().syncObject(dataType, id);
                    cacheObjectDataInMemory(dataType, id, data);
                }
            }
        }
        return data;
    }

    @Override
    public List<Map<String, Object>> getArrayDatasCached(String dataType) {
        if (!TextUtils.isEmpty(dataType)) {
            if (mArrayDatas.containsKey(dataType)) {
                LogUtils.d("内存数据返回,dataType:" + dataType);

                return mArrayDatas.get(dataType);
            } else if (mMemorySyncStrategy.get() != null) {
                List<Map<String, Object>> list = mMemorySyncStrategy.get().syncArray(dataType);
                cacheArrayDatasInMemory(dataType, list);

                LogUtils.d("本地数据返回,dataType:" + dataType);

                return list;
            }
        }
        return null;
    }

    private static Map newLruMap(final String des, final int size) {
        return new LinkedHashMap(size, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Entry eldest) {
                LogUtils.d(String.format("%1$s cache size:%2$d", des, size()));
                return size() > size;
            }
        };
    }

}
