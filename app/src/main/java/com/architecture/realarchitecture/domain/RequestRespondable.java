package com.architecture.realarchitecture.domain;

import com.architecture.realarchitecture.domain.eventbus.EventNetError;
import com.architecture.realarchitecture.domain.eventbus.EventPreNetRequest;
import com.architecture.realarchitecture.domain.eventbus.EventResponse;

/**
 * Created by liushuo on 16/4/13.
 * 具有请求响应能力
 */
public interface RequestRespondable {

    //    @Subscribe(threadMode = ThreadMode.MAIN)
    void onPreNetRequest(EventPreNetRequest event);

    //    @Subscribe(threadMode = ThreadMode.MAIN)
    void onNetRequestError(EventNetError error);//remove tag

    void onReceiveResponse(EventResponse event); //remove tag if over


}
