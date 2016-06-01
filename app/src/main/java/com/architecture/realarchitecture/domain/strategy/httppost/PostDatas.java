package com.architecture.realarchitecture.domain.strategy.httppost;

import com.architecture.realarchitecture.datasource.base.NetClient;
import com.architecture.realarchitecture.datasource.net.NetClientCallback;
import com.architecture.realarchitecture.datasource.net.ResponseBean;
import com.architecture.realarchitecture.datasource.net.VolleyClient;
import com.architecture.realarchitecture.datasource.DALFactory;
import com.architecture.realarchitecture.domain.DataFrom;
import com.architecture.realarchitecture.domain.NetResponseLock;
import com.architecture.realarchitecture.domain.ResponseListener;
import com.architecture.realarchitecture.utils.Utils;

/**
 * Created by liushuo on 16/3/17.
 */
public class PostDatas<K, V> implements NetClientCallback<V> {
    protected ResponseListener<V> mResponseListener;
    protected String mRequestTag;
    protected String mUrl;
    protected String mDataType;
    protected K mData;
    protected NetClient mNetClient;
    private NetResponseLock<V> mLock = new NetResponseLock();

    public PostDatas(String dataType, String url, String requestTag, ResponseListener<V> listener, K data) {
        this.mResponseListener = listener;
        this.mRequestTag = requestTag;
        mDataType = dataType;
        mUrl = url;
        mData = data;
        mNetClient = DALFactory.getNetClient();
    }

    public void postData() {

        if (mResponseListener != null) {
            mResponseListener.preNetRequest();
        }

        mLock.netResponse = false;


        mNetClient.performHttpPost(mUrl, mRequestTag, new NetClientCallback<V>() {
            @Override
            public void onResponse(ResponseBean rb, V data) {
                Utils.notifyNetResponse(mLock, rb, data);
            }
        }, mData);

        Utils.waitForNetResponse(mLock);

        //网络返回
        onResponse(mLock.mRB, mLock.mResponseDatas);
    }

    @Override
    public void onResponse(ResponseBean rb, V data) {

        if (rb.getResponseCode() == VolleyClient.RESPONSE_OK) {
            notifyCallerSuccess(DataFrom.NET, data, true);//网络数据返回，认为请求已经执行完成
        } else {
            notifyCallerError(rb);
        }

    }


    protected void notifyCallerSuccess(DataFrom from, V data, boolean isDone) {
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
