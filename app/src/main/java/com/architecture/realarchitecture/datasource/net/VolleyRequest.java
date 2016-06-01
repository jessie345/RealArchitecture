/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.architecture.realarchitecture.datasource.net;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * A canned request for retrieving the response body at a given URL as a String.
 */
public class VolleyRequest<T> extends Request<T> {

    protected static final String PROTOCOL_CHARSET = "utf-8";
    private static final String PROTOCOL_CONTENT_TYPE = String.format("application/json; charset=%s", new Object[]{"utf-8"});

    private Listener<T> mListener;
    private Gson gson = new Gson();
    private Context mContext;
    private String mRequestBody;


    /**
     * Creates a new request with the given method.
     *
     * @param method        the request {@link Method} to use
     * @param url           URL to fetch the string at
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public VolleyRequest(Context context, int method, String url, String requestBody, Listener<T> listener,
                         ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
        mContext = context;
        mRequestBody = requestBody;
    }

    /**
     * Creates a new GET request.
     *
     * @param url           URL to fetch the string at
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public VolleyRequest(Context context, String url, String requestBody, Listener<T> listener, ErrorListener errorListener) {
        this(context, Method.GET, url, requestBody, listener, errorListener);
    }

    @Override
    protected void deliverResponse(T response) {
        if (mListener != null) {
            mListener.onResponse(response);
        }
    }

    /**
     * @deprecated
     */
    public String getPostBodyContentType() {
        return this.getBodyContentType();
    }

    /**
     * @deprecated
     */
    public byte[] getPostBody() {
        return this.getBody();
    }

    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    public byte[] getBody() {
        try {
            return this.mRequestBody == null ? null : this.mRequestBody.getBytes("utf-8");
        } catch (UnsupportedEncodingException var2) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", new Object[]{this.mRequestBody, "utf-8"});
            return null;
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();
        Map<String, String> revisedHeaders = new HashMap<String, String>();
        revisedHeaders.putAll(headers);
        // TODO: 16/6/1 可以在这里添加自定义请求头

        return revisedHeaders;
    }


    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {

        try {
            Map<String, String> headers = response.headers;
            // TODO: 16/6/1 可以处理网络返回头信息

            String parsed = "";
            if (response.data != null && response.data.length > 0) {
                parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
            }
            T map = gson.fromJson(parsed,
                    new TypeToken<T>() {
                    }.getType());

            return Response.success(map, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception je) {
            return Response.error(new ParseError(je));
        }

    }


}
