package com.architecture.realarchitecture.domain;

import com.architecture.realarchitecture.datasource.net.ResponseBean;

/**
 * Created by liushuo on 16/3/29.
 */
public class NetResponseLock<T> {
    public volatile boolean netResponse = false;
    public volatile ResponseBean mRB;
    public volatile T mResponseDatas;
}
