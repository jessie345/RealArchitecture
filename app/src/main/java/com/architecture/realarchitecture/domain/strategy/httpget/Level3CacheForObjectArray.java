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
 * 检测到请求未过期，使用三级缓存数据获取策略
 */
public class Level3CacheForObjectArray extends ArrayGetStrategy<Map<String, Object>> {

    private MemoryStorage mMemoryDataPool;
    private NetClient mNetClient;

    public Level3CacheForObjectArray(String dataType, String url, String requestTag, ResponseListener<List<Map<String, Object>>> callback) {
        super(dataType, url, requestTag, callback);
        mMemoryDataPool = DALFactory.getMemoryStorage().configureSyncStrategy(NoSqlSyncStrategy.mStrategy);
        mNetClient = DALFactory.getNetClient();

    }


    @Override
    public void fetchData() {
        final List<Map<String, Object>> datas = mMemoryDataPool.getArrayDatasCached(mDataType);

        if (datas != null && datas.size() > 0) {

            notifyCallerSuccess(DataFrom.CACHE, datas, true);

        } else {
            //执行网络之前，回调reqeust
            if (mResponseListener != null) {
                mResponseListener.preNetRequest();
            }
            //初始化锁变量
            mLock.netResponse = false;

            //缓存（本地&内存）数据不可用，刷新网络
            mNetClient.performHttpGet(mUrl, mRequestTag, new NetClientCallback<List<Map<String, Object>>>() {
                @Override
                public void onResponse(ResponseBean rb, List<Map<String, Object>> data) {
                    Utils.notifyNetResponse(mLock, rb, data);
                }
            });

           Utils.waitForNetResponse(mLock);

            //网络数据返回
            onResponse(mLock.mRB, mLock.mResponseDatas);


        }
    }


}
