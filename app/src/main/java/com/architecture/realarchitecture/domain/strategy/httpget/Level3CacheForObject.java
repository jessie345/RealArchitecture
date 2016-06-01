package com.architecture.realarchitecture.domain.strategy.httpget;

import com.architecture.realarchitecture.datasource.base.MemoryStorage;
import com.architecture.realarchitecture.datasource.base.NetClient;
import com.architecture.realarchitecture.datasource.net.NetClientCallback;
import com.architecture.realarchitecture.datasource.net.ResponseBean;
import com.architecture.realarchitecture.datasource.sync.NoSqlSyncStrategy;
import com.architecture.realarchitecture.datasource.DALFactory;
import com.architecture.realarchitecture.domain.DataFrom;
import com.architecture.realarchitecture.domain.ResponseListener;
import com.architecture.realarchitecture.domain.strategy.base.ObjectGetStrategy;
import com.architecture.realarchitecture.utils.Utils;

import java.util.Map;

/**
 * Created by liushuo on 16/3/17.
 * 检测到请求未过期，使用三级缓存数据获取策略
 */
public class Level3CacheForObject extends ObjectGetStrategy {
    private MemoryStorage mMemoryDataPool;
    private NetClient mNetClient;

    public Level3CacheForObject(String dataType, String url, String id, String requestTag, ResponseListener<Map<String, Object>> callback) {
        super(dataType, url, id, requestTag, callback);

        mMemoryDataPool = DALFactory.getMemoryStorage().configureSyncStrategy(NoSqlSyncStrategy.mStrategy);
        mNetClient = DALFactory.getNetClient();
    }


    @Override
    public void fetchData() {
        Map<String, Object> data = mMemoryDataPool.getObjectDataForId(mDataType, mId);

        if (data != null) {

            notifyCallerSuccess(DataFrom.CACHE, data, true);

        } else {
            //执行网络之前，回调reqeust
            if (mResponseListener != null) {
                mResponseListener.preNetRequest();
            }
            //初始化响应锁
            mLock.netResponse = false;

            //缓存没有数据（内存&本地），请求网络
            mNetClient.performHttpGet(mUrl, mRequestTag, new NetClientCallback<Map<String, Object>>() {
                @Override
                public void onResponse(ResponseBean rb, Map<String, Object> data) {
                    Utils.notifyNetResponse(mLock, rb, data);
                }
            });

            Utils.waitForNetResponse(mLock);

            //网络数据返回
            onResponse(mLock.mRB, mLock.mResponseDatas);

        }

    }


}
