package com.architecture.realarchitecture.datasource.base;

import com.architecture.realarchitecture.datasource.net.NetClientCallback;

/**
 * Created by liushuo on 16/3/19.
 */
public interface NetClient {

    <T> void performHttpGet(String url,
                            String requestTag,
                            NetClientCallback<T> responseListener);


    <T> void performHttpHead(String url,
                             String requestTag,
                             NetClientCallback<T> responseListener);

    <K, V> void performHttpPost(String url,
                                String requestTag,
                                NetClientCallback<V> responseListener,
                                K data);

    <K, V> void performHttpPut(String url,
                               String requestTag,
                               NetClientCallback<V> responseListener,
                               K data);

    void cancelRequest(String tag);
}
