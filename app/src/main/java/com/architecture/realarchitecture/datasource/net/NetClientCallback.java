package com.architecture.realarchitecture.datasource.net;

/**
 * Created by Administrator on 2015/4/2 0002.
 */

/**
 * request回调监听
 * http 代理层和策略层之间，策略层和core之间通过此接口传递网络数据
 * 该回调在volley线程中执行
 *
 * @param <T>
 */
public interface NetClientCallback<T> {
    /**
     * 接口协议请求返回处理类
     *
     * @param rb   true是请求成功，false是请求失败
     * @param data 返回的报文提取，处理成body的bean
     * @since 1.0.0
     */
    void onResponse(ResponseBean rb, T data);


}