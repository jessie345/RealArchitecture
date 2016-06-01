package com.architecture.realarchitecture.domain;

import com.architecture.realarchitecture.datasource.base.NetClient;
import com.architecture.realarchitecture.datasource.DALFactory;
import com.architecture.realarchitecture.domain.request.PointRequest;
import com.architecture.realarchitecture.utils.LogUtils;

import java.util.concurrent.Executor;

/**
 * Created by liushuo on 16/3/17.
 */
public class RequestManager {
    private RequestExecutor mRequestExecutor;
    private PointDispatcher mPointDispatcher;

    private NetClient mNetClient;

    private static RequestManager mInstance;

    private RequestManager() {
        mRequestExecutor = new RequestExecutor();
        mPointDispatcher = PointDispatcher.getInstance();
        mNetClient = DALFactory.getNetClient();
    }

    public static RequestManager getInstance() {
        synchronized (RequestManager.class) {
            if (mInstance == null) {
                mInstance = new RequestManager();
            }
        }
        return mInstance;
    }

    public synchronized void enqueueRequest(Request request) {
        if (request == null) return;

        //1.将请求添加到任务队列
        if (request instanceof PointRequest) {
            //发送埋点
            mPointDispatcher.dispatchPointRequest(request);
            return;

        }

        Runnable runnable = new RequestRunnable(request);
        mRequestExecutor.submit(request.getRequestTag(), runnable);
        LogUtils.d("running task count:" + mRequestExecutor.getActiveCount() + ",request:" + request.getClass().getSimpleName());
    }

    public void enqueueRequest(Executor executor, Request request) {
        if (request == null || executor == null) return;

        Runnable runnable = new RequestRunnable(request);
        executor.execute(runnable);

    }


    /**
     * 取消正在执行的或者正在排队的请求
     *
     * @param tag
     */
    public void cancelRequest(String tag) {
        if (tag != null) {
            mNetClient.cancelRequest(tag);
            mRequestExecutor.cancelTask(tag);
        }
    }


    class RequestRunnable implements Runnable {
        Request request;

        public RequestRunnable(Request request) {
            this.request = request;
        }

        @Override
        public void run() {
            request.perform();
        }
    }

}
