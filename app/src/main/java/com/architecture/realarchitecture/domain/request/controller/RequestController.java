package com.architecture.realarchitecture.domain.request.controller;

import android.content.Context;
import android.text.TextUtils;

import com.architecture.realarchitecture.domain.Request;
import com.architecture.realarchitecture.domain.RequestManager;
import com.architecture.realarchitecture.domain.eventbus.EventNetError;
import com.architecture.realarchitecture.domain.eventbus.EventPreNetRequest;
import com.architecture.realarchitecture.domain.eventbus.EventResponse;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by liushuo on 16/3/27.
 */
public class RequestController implements RequestControllable {
    private Set<String> mTags = new HashSet<>();
    private Context mContext;

    public RequestController(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public void onPreNetRequest(EventPreNetRequest event) {
        //do nothing
    }

    @Override
    public void onNetRequestError(EventNetError error) {
        removeRequestTag(error.mRequest.getRequestTag());
    }

    private void removeRequestTag(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            mTags.remove(tag);

        }
    }

    @Override
    public void enqueueRequest(Request request) {

        //首先执行同步操作
        String tag = request.getRequestTag();
        if (!TextUtils.isEmpty(tag)) {
            mTags.add(request.getRequestTag());
        }

        request.attachRequestController(this);

        //最后执行将任务添加到任务队列
        RequestManager.getInstance().enqueueRequest(request);
    }

    @Override
    public void cancelRequest() {
        Iterator<String> itr = mTags.iterator();
        while (itr.hasNext()) {
            String tag = itr.next();
            RequestManager.getInstance().cancelRequest(tag);
            itr.remove();
        }
    }

    @Override
    public void onReceiveResponse(EventResponse event) {
        if (event.mRequest.isDone()) {
            removeRequestTag(event.mRequest.getRequestTag());
        }
    }

    @Override
    public boolean isManagedRequest(Request request) {
        if (request == null) return false;

        return request.getRequestController() == this;
    }
}
