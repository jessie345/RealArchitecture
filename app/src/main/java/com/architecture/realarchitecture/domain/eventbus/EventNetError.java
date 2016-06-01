package com.architecture.realarchitecture.domain.eventbus;

import com.architecture.realarchitecture.datasource.net.ResponseBean;
import com.architecture.realarchitecture.domain.Request;

/**
 * Created by liushuo on 16/3/20.
 */
public class EventNetError {
    public ResponseBean mRB;
    public Request mRequest;

    public EventNetError(Request request, ResponseBean rb) {
        this.mRB = rb;
        this.mRequest = request;
    }

}
