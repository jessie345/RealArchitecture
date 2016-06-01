package com.architecture.realarchitecture.domain.request.controller;

import com.architecture.realarchitecture.domain.Request;
import com.architecture.realarchitecture.domain.RequestRespondable;

/**
 * Created by liushuo on 16/3/27.
 */
public interface RequestControllable extends RequestRespondable {

    void enqueueRequest(Request request);

    void cancelRequest();

    boolean isManagedRequest(Request request);

}
