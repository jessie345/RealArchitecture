package com.architecture.realarchitecture.domain;

import android.text.TextUtils;

import com.architecture.realarchitecture.domain.request.controller.RequestControllable;
import com.architecture.realarchitecture.manager.PreferenceManager;

/**
 * Created by liushuo on 16/3/17.
 * 注意:请求网络数据，底层可能返回null，所以调用者需要进行判空处理
 */
public abstract class Request<K, V> implements ResponseListener<V> {
    private static final int RESPONSE_VALID_THRESHOLD = 30 * 60 * 1000;

    public enum State {IDLE, RUNNING, DONE}

    protected String mRequestTag;
    protected String mUrl;
    protected String mDataType;
    protected String mRequestId;//可以用于判定响应是否过期
    private RequestControllable mRequestController;

    private volatile K mResult;
    private volatile State mState = State.IDLE;


    public Request(String docType) {
        this.mDataType = docType;
        mRequestTag = getClass().getSimpleName();
    }

    public Request(String docType, String url) {
        this(docType);
        mUrl = url;
    }

    /**
     * 子类必须调用父类方法，初始化请求状态
     */
    public void perform() {
        if (mState != State.IDLE) throw new IllegalStateException("请求无法重复添加");

        mState = State.RUNNING;
    }

    protected abstract K transformForUiLayer(V v);

    protected abstract void cacheNetResponse(V data);


    public K getResult() {
        return mResult;
    }

    protected void setDone(boolean isDone) {
        mState = isDone ? State.DONE : State.RUNNING;
    }

    public boolean isRunning() {
        return mState == State.RUNNING;
    }

    public boolean isDone() {
        return mState == State.DONE;
    }


    /**
     * 判定请求过期的策略由父亲类决定，子类提供requestId标示不同的请求，为空则每次都过期
     *
     * @return
     */
    public final boolean isResponseValid() {
        if (TextUtils.isEmpty(mRequestId)) return false;

        long lastRequestTime = PreferenceManager.getLongValue(mRequestId);
        long currentTime = System.currentTimeMillis();
        return currentTime - lastRequestTime < getResponseValidThreshold();
    }

    /**
     * 子类如果有不同的过期时间，需要重写
     *
     * @return
     */
    protected int getResponseValidThreshold() {
        return RESPONSE_VALID_THRESHOLD;
    }

    public void setRequestTag(String requestTag) {
        this.mRequestTag = requestTag;
    }

    public String getRequestTag() {
        return mRequestTag;
    }

    public void attachRequestController(RequestControllable requestController) {
        this.mRequestController = requestController;
    }

    public RequestControllable getRequestController() {
        return mRequestController;
    }

    /**
     * 子类必须调用父类方法,网络数据返回，客户端需要判断null
     * 此方法在volley请求执行线程中执行
     *
     * @param from
     * @param data
     */
    @Override
    public void onResponse(DataFrom from, V data, boolean isDone) {
        if (from == DataFrom.NET && data != null) {
            cacheNetResponse(data);

            extendNetResponseValid();
        }

        mResult = transformForUiLayer(data);
        setDone(isDone);
    }

    private void extendNetResponseValid() {
        if (!TextUtils.isEmpty(mRequestId)) {
            PreferenceManager.putLong(mRequestId, System.currentTimeMillis());
        }
    }

}
