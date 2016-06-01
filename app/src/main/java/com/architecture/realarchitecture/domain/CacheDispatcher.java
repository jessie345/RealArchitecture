package com.architecture.realarchitecture.domain;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;

import com.architecture.realarchitecture.datasource.base.BaseLocalStorage;
import com.architecture.realarchitecture.datasource.DALFactory;
import com.architecture.realarchitecture.utils.LogUtils;

import java.util.Map;

/**
 * Created by liushuo on 16/3/20.
 */
public class CacheDispatcher implements Handler.Callback {
    public static final String TAG = "CacheDispatcher";

    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private BaseLocalStorage mLocalStorage;

    private static CacheDispatcher mInstance;

    private CacheDispatcher() {
        mHandlerThread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper(), this);
        mLocalStorage = DALFactory.getBaseStorage();
    }

    public static CacheDispatcher getInstance() {
        synchronized (CacheDispatcher.class) {
            if (mInstance == null) {
                mInstance = new CacheDispatcher();
            }
        }
        return mInstance;
    }

    public void dispatchDataCache(String dataType, Map<String, Object>... datas) {
        if (mLocalStorage != null && !TextUtils.isEmpty(dataType) && datas != null && datas.length > 0) {
            Message msg = mHandler.obtainMessage();
            msg.obj = new CacheElement(dataType, datas);
            mHandler.sendMessage(msg);
        } else {
            LogUtils.d("无法存储数据 baselocalstorage:" + mLocalStorage + ",type:" + dataType + ",datas:" + datas);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.obj != null && msg.obj instanceof CacheElement) {
            CacheElement ce = (CacheElement) msg.obj;
            //1.数据缓存本地
            mLocalStorage.saveDatas(ce.mDataType, ce.mArray);
        }
        return true;
    }

    public static class CacheElement {
        public final Map<String, Object>[] mArray;
        public final String mDataType;

        public CacheElement(String dataType, Map<String, Object>... array) {
            this.mArray = array;
            this.mDataType = dataType;
        }
    }
}
