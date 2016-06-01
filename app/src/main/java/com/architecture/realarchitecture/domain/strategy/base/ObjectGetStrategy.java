package com.architecture.realarchitecture.domain.strategy.base;
import com.architecture.realarchitecture.datasource.net.NetClientCallback;
import com.architecture.realarchitecture.datasource.net.ResponseBean;
import com.architecture.realarchitecture.datasource.net.VolleyClient;
import com.architecture.realarchitecture.domain.DataFrom;
import com.architecture.realarchitecture.domain.NetResponseLock;
import com.architecture.realarchitecture.domain.ResponseListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liushuo on 16/3/17.
 * the object is json object
 */
public abstract class ObjectGetStrategy implements NetClientCallback<Map<String, Object>> {
    protected ResponseListener<Map<String, Object>> mResponseListener;
    protected String mRequestTag;
    protected String mUrl;
    protected String mDataType;
    protected String mId;
    protected NetResponseLock<Map<String, Object>> mLock = new NetResponseLock();


    public ObjectGetStrategy(String dataType, String url, String id, String requestTag, ResponseListener<Map<String, Object>> listener) {
        this.mResponseListener = listener;
        this.mRequestTag = requestTag;
        mDataType = dataType;
        mUrl = url;
        mId = id;
    }

    public abstract void fetchData();

    @Override
    public void onResponse(ResponseBean rb, Map<String, Object> data) {
        if (data != null) data = new ConcurrentHashMap<>(data);
        if (data == null) data = new ConcurrentHashMap<>();//返回客户端安全类型

        if (rb.getResponseCode() == VolleyClient.RESPONSE_OK) {
            notifyCallerSuccess(DataFrom.NET, data, true);//网络数据返回，认为请求已经执行完成
        } else {
            notifyCallerError(rb);
        }
    }


    protected void notifyCallerSuccess(DataFrom from, Map<String, Object> data, boolean isDone) {
        if (mResponseListener != null) {
            mResponseListener.onResponse(from, data, isDone);
        }
    }

    protected void notifyCallerError(ResponseBean rb) {
        if (mResponseListener != null) {
            mResponseListener.onNetError(rb);
        }
    }
}
