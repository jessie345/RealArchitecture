package com.architecture.realarchitecture.datasource.net;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/5/20 0020.
 */
public class ResponseBean implements Serializable {

    public ResponseBean(int responseCode) {
        this.responseCode = responseCode;
    }

    public ResponseBean(int responseCode, int mErrorCode, String mErrorMessage) {
        setResponseCode(responseCode);
        this.mErrorCode = mErrorCode;
        this.mErrorMessage = mErrorMessage;
    }

    int responseCode; //
    int mErrorCode;//
    String mErrorMessage;

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public void setErrorCode(int mErrorCode) {
        this.mErrorCode = mErrorCode;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public void setErrorMessage(String mErrorMessage) {
        this.mErrorMessage = mErrorMessage;
    }
}
