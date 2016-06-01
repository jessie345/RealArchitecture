package com.architecture.realarchitecture.domain.strategy.httpget;

import com.architecture.realarchitecture.datasource.base.MemoryStorage;
import com.architecture.realarchitecture.datasource.base.NetClient;
import com.architecture.realarchitecture.datasource.net.NetClientCallback;
import com.architecture.realarchitecture.datasource.net.ResponseBean;
import com.architecture.realarchitecture.datasource.sync.NoSqlSyncStrategy;
import com.architecture.realarchitecture.datasource.DALFactory;
import com.architecture.realarchitecture.domain.DataFrom;
import com.architecture.realarchitecture.domain.ResponseListener;
import com.architecture.realarchitecture.domain.strategy.base.ArrayGetStrategy;
import com.architecture.realarchitecture.utils.Utils;

import java.util.List;
import java.util.Map;

/**
 * Created by liushuo on 16/3/17.
 */
public class ForceNetForPrimitiveArray<T> extends ArrayGetStrategy<T> {
    public static final String DATA_KEY = "data";
    public static final String ID_KEY = "_id";

    private MemoryStorage mMemoryDataPool;
    private NetClient mNetClient;

    private String mCacheId;

    public ForceNetForPrimitiveArray(String cacheId, String dataType, String url, String requestTag, ResponseListener<List<T>> callback) {
        super(dataType, url, requestTag, callback);
        mMemoryDataPool = DALFactory.getMemoryStorage().configureSyncStrategy(NoSqlSyncStrategy.mStrategy);
        mNetClient = DALFactory.getNetClient();

        mCacheId = cacheId;
    }


    @Override
    public void fetchData() {
        Map<String, Object> data = mMemoryDataPool.getObjectDataForId(mDataType, mCacheId);

        if (data != null) {
            List<T> list = (List<T>) data.get(DATA_KEY);
            notifyCallerSuccess(DataFrom.CACHE, list, false);
        }

        //执行网络之前，回调reqeust
        if (mResponseListener != null) {
            mResponseListener.preNetRequest();
        }

        //初始化响应锁
        mLock.netResponse = false;

        //缓存没有数据（内存&本地），请求网络
        mNetClient.performHttpGet(mUrl, mRequestTag, new NetClientCallback<List<T>>() {
            @Override
            public void onResponse(ResponseBean rb, List<T> data) {
                Utils.notifyNetResponse(mLock, rb, data);
            }
        });

        Utils.waitForNetResponse(mLock);

        //网络数据返回
        onResponse(mLock.mRB, mLock.mResponseDatas);
    }


}