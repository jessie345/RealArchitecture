package com.architecture.realarchitecture.domain.eventbus;

import com.architecture.realarchitecture.domain.Request;

/**
 * Created by liushuo on 16/3/20.
 */
public class EventPreNetRequest {
    public final Request mRequest;

    public EventPreNetRequest(Request request) {
        mRequest = request;
    }

}
