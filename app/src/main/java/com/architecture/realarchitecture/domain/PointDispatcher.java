package com.architecture.realarchitecture.domain;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;

import com.architecture.realarchitecture.domain.request.PointRequest;

/**
 * Created by liushuo on 16/3/20.
 */
public class PointDispatcher implements Handler.Callback {
    private static final String TAG = "PointDispatcher";
    private HandlerThread mThread;
    private Handler mHandler;

    private static PointDispatcher mInstance;

    public static PointDispatcher getInstance() {
        synchronized (PointDispatcher.class) {
            if (mInstance == null) {
                mInstance = new PointDispatcher();
            }
        }
        return mInstance;
    }

    private PointDispatcher() {
        mThread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
        mThread.start();
        mHandler = new Handler(mThread.getLooper(), this);
    }

    public void dispatchPointRequest(Request request) {
        if (request != null) {
            Message msg = mHandler.obtainMessage();
            msg.obj = request;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        PointRequest request = (PointRequest) msg.obj;
        request.perform();
        return true;
    }
}
