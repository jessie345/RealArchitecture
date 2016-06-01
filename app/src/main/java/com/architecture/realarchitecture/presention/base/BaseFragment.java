/**
 * Copyright (c) 2014 Guanghe.tv
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 **/
package com.architecture.realarchitecture.presention.base;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.architecture.realarchitecture.datasource.net.ResponseBean;
import com.architecture.realarchitecture.domain.FreedomRequestHandler;
import com.architecture.realarchitecture.domain.Request;
import com.architecture.realarchitecture.domain.RequestRespondable;
import com.architecture.realarchitecture.domain.eventbus.EventNetError;
import com.architecture.realarchitecture.domain.eventbus.EventPreNetRequest;
import com.architecture.realarchitecture.domain.eventbus.EventResponse;
import com.architecture.realarchitecture.domain.request.controller.RequestControllable;
import com.architecture.realarchitecture.domain.request.controller.RequestController;
import com.architecture.realarchitecture.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Android Studio
 * User: killnono(陈凯)
 * Date: 16/3/17
 * Time: 下午5:46
 * Version: 1.0
 */
public abstract class BaseFragment extends Fragment implements RequestControllable {

    protected Activity mAct;
    private RequestController mController;
    private FreedomRequestHandler mFreedomRequestHandler;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAct = getActivity();
        mController = new RequestController(mAct);
        mFreedomRequestHandler = new FreedomRequestHandler();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAct = null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void onPreNetRequest(EventPreNetRequest event) {
        if (mFreedomRequestHandler.isFreedomRequest(event.mRequest)) {
            mFreedomRequestHandler.onPreNetRequest(event);
            return;
        }

        if (!isManagedRequest(event.mRequest)) return;

        mController.onPreNetRequest(event);
        handlePreNetRequest(event.mRequest);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void onNetRequestError(EventNetError error) {
        if (mFreedomRequestHandler.isFreedomRequest(error.mRequest)) {
            mFreedomRequestHandler.onNetRequestError(error);
            return;
        }

        if (!isManagedRequest(error.mRequest)) return;

        mController.onNetRequestError(error);
        Utils.showServerErrorMsg(error.mRB);
        handleNetRequestError(error.mRequest, error.mRB);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void onReceiveResponse(EventResponse event) {
        if (mFreedomRequestHandler.isFreedomRequest(event.mRequest)) {
            mFreedomRequestHandler.onReceiveResponse(event);
            return;
        }

        if (!isManagedRequest(event.mRequest)) return;

        mController.onReceiveResponse(event);
        handleReceivedResponse(event);
    }


    @Override
    public void enqueueRequest(Request request) {
        mController.enqueueRequest(request);
    }


    @Override
    public void cancelRequest() {
        mController.cancelRequest();
    }

    @Override
    public boolean isManagedRequest(Request request) {
        return mController.isManagedRequest(request);
    }

    public void registerFreedomRequestHandler(String tag, RequestRespondable handler) {
        mFreedomRequestHandler.registerFreedomRequestHandler(tag, handler);
    }


    protected abstract void handlePreNetRequest(Request request);

    protected abstract void handleNetRequestError(Request request, ResponseBean rb);

    protected abstract void handleReceivedResponse(EventResponse event);
}
