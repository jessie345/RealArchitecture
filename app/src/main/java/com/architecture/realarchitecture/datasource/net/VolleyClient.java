package com.architecture.realarchitecture.datasource.net;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.architecture.realarchitecture.datasource.base.NetClient;
import com.architecture.realarchitecture.utils.Utils;
import com.architecture.realarchitecture.utils.LogUtils;

import org.json.JSONObject;

public class VolleyClient implements NetClient {

    public static final int RESPONSE_ERROR = 100;
    public static final int RESPONSE_OK = 200;
    public static final int NO_NET = 300;


    private Context mContext;

    private static VolleyClient mInstance;

    private VolleyClient(Context context) {
        mContext = context;
    }

    public static VolleyClient getInstance(Context context) {
        synchronized (VolleyClient.class) {
            if (mInstance == null) {
                mInstance = new VolleyClient(context);
            }
        }
        return mInstance;
    }

    private <T> boolean checkPreCondition(String url, NetClientCallback<T> callback) {

        if (TextUtils.isEmpty(url)) {
            return false;
        }

        if (!Utils.checkNet(mContext)) {
            if (callback != null) {
                callback.onResponse(new ResponseBean(NO_NET), null);
            }
            return false;
        }

        return true;
    }


    /**
     * 如果传入的tag有效，则可以通过该tag取消请求
     *
     * @param url
     * @param callback
     * @param requestTag
     * @param <T>
     */
    @Override
    public <T> void performHttpGet(final String url, String requestTag,
                                   final NetClientCallback<T> callback) {

        if (!checkPreCondition(url, callback)) {
            return;
        }
        //请求成功回调函数实例
        Response.Listener listener = getVolleyListener(url, callback);


        //请求失败回调函数实例
        Response.ErrorListener errorListener = getVolleyErrorListener(url, callback);

        LogUtils.v("API:" + url + "==request:");

        VolleyRequest<T> request = new VolleyRequest<T>(mContext, Request.Method.GET, url, null, listener, errorListener);

        makeRequest(request, requestTag);
    }


    /**
     * 如果传入的tag有效，则可以通过该tag取消请求
     *
     * @param url      apiurl
     * @param callback 回调监听
     *                 v1.1
     */
    @Override
    public <K, V> void performHttpPost(final String url, String requestTag,
                                       final NetClientCallback<V> callback, K data
    ) {

        if (!checkPreCondition(url, callback)) {
            return;
        }

        //请求成功回调函数实例
        Response.Listener listener = getVolleyListener(url, callback);
        //请求失败回调函数实例
        Response.ErrorListener errorListener = getVolleyErrorListener(url, callback);

        //初始化请求实体

        Gson gson = Utils.getGsonForThread();
        String requestBody = gson.toJson(data);

        LogUtils.v("API:" + url + "==request:" + requestBody);


        VolleyRequest<V> request = new VolleyRequest(mContext, Request.Method.POST, url, requestBody, listener, errorListener);

        makeRequest(request, requestTag);
    }


    /**
     * @param url        url
     * @param requestTag tag
     * @param callback   回调
     */
    @Override
    public <K, V> void performHttpPut(final String url, String requestTag,
                                      final NetClientCallback<V> callback, K data) {

        if (!checkPreCondition(url, callback)) {
            return;
        }

        //请求成功回调函数实例
        Response.Listener listener = getVolleyListener(url, callback);
        //请求失败回调函数实例
        Response.ErrorListener errorListener = getVolleyErrorListener(url, callback);

        //初始化请求实体

        Gson gson = Utils.getGsonForThread();
        String requestBody = gson.toJson(data);

        LogUtils.v("API:" + url + "==request:" + requestBody);


        VolleyRequest<V> request = new VolleyRequest<V>(mContext, Request.Method.PUT, url, requestBody, listener, errorListener);
        makeRequest(request, requestTag);
    }

    /**
     * 如果传入的tag有效，则可以通过该tag取消请求
     *
     * @param url
     * @param callback
     * @param requestTag
     * @param <T>
     */
    @Override
    public <T> void performHttpHead(final String url, String requestTag,
                                    final NetClientCallback<T> callback) {

        if (!checkPreCondition(url, callback)) {
            return;
        }
        //请求成功回调函数实例
        Response.Listener listener = getVolleyListener(url, callback);


        //请求失败回调函数实例
        Response.ErrorListener errorListener = getVolleyErrorListener(url, callback);

        LogUtils.v("API:" + url + "==request:");

        VolleyRequest<T> request = new VolleyRequest<T>(mContext, Request.Method.HEAD, url, null, listener, errorListener);
        makeRequest(request, requestTag);
    }


    /**
     * 如果传入的tag有效，则可以通过该tag取消请求的发送
     *
     * @param request    req
     * @param requestTag tag
     */
    private void makeRequest(VolleyRequest request, String requestTag) {
        if (!TextUtils.isEmpty(requestTag)) {
            request.setTag(requestTag);
        }
        // step4:放入请求队列
        RequestQueue requestQueue = VolleyManager.getInstance(mContext).getRequestQueue();
        int socketTimeout = 5000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);
    }


    @Override
    public void cancelRequest(String tag) {
        if (tag != null) {
            VolleyManager.getInstance(mContext).getRequestQueue().cancelAll(tag);
        }
    }

    @NonNull
    private <T> Response.Listener<T> getVolleyListener(final String url, final NetClientCallback<T> callback) {
        return new Response.Listener<T>() {
            @Override
            public void onResponse(T data) {
                LogUtils.v("API:" + url + "==onResponse:" + String.valueOf(data));
                if (callback != null) {
                    callback.onResponse(new ResponseBean(RESPONSE_OK), data);
                }
            }
        };
    }

    @NonNull
    private <T> Response.ErrorListener getVolleyErrorListener(final String url, final NetClientCallback<T> callback) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                int errorCode = -1;
                String str = "";
                if (volleyError != null) {
                    if (volleyError.networkResponse != null) {
                        errorCode = volleyError.networkResponse.statusCode;
                        NetworkResponse nwkRsp = volleyError.networkResponse;

                        if (nwkRsp.data != null && nwkRsp.data.length > 0) {
                            try {
                                str = new String(nwkRsp.data, HttpHeaderParser.parseCharset(nwkRsp.headers));
                                JSONObject errorObj = new JSONObject(str);
                                str = Utils.getStringFromJsonObj("msg", errorObj);
                            } catch (Exception e) {
                                LogUtils.e(e);
                            }
                        }
                    }
                }

                LogUtils.v("API:" + url + "==onErrorResponse: " + errorCode + "=" + String.valueOf(volleyError));

                if (callback != null) {
                    callback.onResponse(new ResponseBean(RESPONSE_ERROR, errorCode, str), null);
                }
            }

        };
    }
}

