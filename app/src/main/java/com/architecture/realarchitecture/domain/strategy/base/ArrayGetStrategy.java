package com.architecture.realarchitecture.domain.strategy.base;

import com.architecture.realarchitecture.datasource.net.NetClientCallback;
import com.architecture.realarchitecture.datasource.net.ResponseBean;
import com.architecture.realarchitecture.datasource.net.VolleyClient;
import com.architecture.realarchitecture.domain.DataFrom;
import com.architecture.realarchitecture.domain.NetResponseLock;
import com.architecture.realarchitecture.domain.ResponseListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by liushuo on 16/3/17.
 */
public abstract class ArrayGetStrategy<T> implements NetClientCallback<List<T>> {
    protected ResponseListener<List<T>> mResponseListener;
    protected String mRequestTag;
    protected String mUrl;
    protected String mDataType;
    protected NetResponseLock<List<T>> mLock = new NetResponseLock();


    public ArrayGetStrategy(String dataType, String url, String requestTag, ResponseListener<List<T>> listener) {
        this.mResponseListener = listener;
        this.mRequestTag = requestTag;
        this.mUrl = url;
        this.mDataType = dataType;
    }


    public abstract void fetchData();

    @Override
    public void onResponse(ResponseBean rb, List<T> data) {
        if (data != null) data = new CopyOnWriteArrayList<>(data);
        if (data == null) data = new CopyOnWriteArrayList<>();

        if (rb.getResponseCode() == VolleyClient.RESPONSE_OK) {
            notifyCallerSuccess(DataFrom.NET, data, true);//网络数据返回，认为请求已经执行完成
        } else {
            notifyCallerError(rb);
        }
    }


    protected void notifyCallerSuccess(DataFrom from, List<T> data, boolean isDone) {
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
