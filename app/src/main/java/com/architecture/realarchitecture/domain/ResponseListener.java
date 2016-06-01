package com.architecture.realarchitecture.domain;

import com.architecture.realarchitecture.datasource.net.ResponseBean;

/**
 * Created by liushuo on 16/3/17.
 */
public interface ResponseListener<T> {
    /**
     * 执行网络请求之前策略需要回调接口通知request执行相应操作(eg.弹窗加载对话框)
     */
    void preNetRequest();

    void onResponse(DataFrom from, T t, boolean isDone);

    void onNetError(ResponseBean responseBean);
}
