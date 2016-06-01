package com.architecture.realarchitecture.domain.eventbus;

import com.architecture.realarchitecture.domain.DataFrom;
import com.architecture.realarchitecture.domain.Request;

/**
 * Created by liushuo on 16/3/27.
 */
public class EventResponse {
    public final Request mRequest;
    public final DataFrom mDataFrom;

    public EventResponse(Request request, DataFrom from) {
        mRequest = request;
        mDataFrom = from;
    }
}
