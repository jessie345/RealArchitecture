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
package com.architecture.realarchitecture.domain.request;

import com.architecture.realarchitecture.datasource.net.ResponseBean;
import com.architecture.realarchitecture.datasource.nosql.NoSqlDataTypeDef;
import com.architecture.realarchitecture.datasource.DALFactory;
import com.architecture.realarchitecture.domain.ApiHead;
import com.architecture.realarchitecture.domain.CacheDispatcher;
import com.architecture.realarchitecture.domain.DataFrom;
import com.architecture.realarchitecture.domain.Request;
import com.architecture.realarchitecture.domain.eventbus.EventNetError;
import com.architecture.realarchitecture.domain.eventbus.EventPreNetRequest;
import com.architecture.realarchitecture.domain.eventbus.EventResponse;
import com.architecture.realarchitecture.domain.strategy.base.ObjectGetStrategy;
import com.architecture.realarchitecture.domain.strategy.httpget.ForceNetForObject;
import com.architecture.realarchitecture.domain.strategy.httpget.Level3CacheForObject;
import com.architecture.realarchitecture.utils.LogUtils;
import com.architecture.realarchitecture.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

/**
 * Created by liuzhi on 2016/3/24.
 * -------------------------------
 */
public class DemoRequest extends Request<Map<String, Object>, Map<String, Object>> {

    private ObjectGetStrategy mRequestStragety;
    private String mId;

    public DemoRequest(String id) {
        super(NoSqlDataTypeDef.DOC_TYPE_TEST_NOSQL);
        mId = id;
        mUrl = Utils.buildUrl(String.format(ApiHead.TEST_API,id));
    }

    @Override
    protected Map<String, Object> transformForUiLayer(Map<String, Object> stringObjectMap) {
        return stringObjectMap;
    }

    @Override
    protected void cacheNetResponse(Map<String, Object> data) {
        if (data.containsKey("_id")) {
            //1.检查是否需要缓存内存
            DALFactory.getMemoryStorage().cacheObjectDataInMemory(mDataType, (String) data.get("_id"), data);
            //2.数据缓存本地
            CacheDispatcher.getInstance().dispatchDataCache(mDataType, data);
            LogUtils.d("数据同步到缓存：" + mDataType);
        }


    }

    @Override
    public void onResponse(DataFrom from, Map<String, Object> data, boolean isDone) {
        super.onResponse(from, data, isDone);
        EventBus.getDefault().post(new EventResponse(this, from));

        LogUtils.d("数据返回成功：" + mDataType);

    }

    @Override
    public void preNetRequest() {
        LogUtils.d("即将执行网络请求：" + mDataType);

        EventBus.getDefault().post(new EventPreNetRequest(this));
    }

    @Override
    public void perform() {
        super.perform();

        if (isResponseValid()) {
            mRequestStragety = new Level3CacheForObject(mDataType, mUrl, mId, mRequestTag, this);
        } else {
            mRequestStragety = new ForceNetForObject(mDataType, mUrl, mId, mRequestTag, this);
        }
        mRequestStragety.fetchData();
    }

    @Override
    public void onNetError(ResponseBean responseBean) {
        EventBus.getDefault().post(new EventNetError(this, responseBean));

    }

    @Override
    public String toString() {
        return this.getClass().toString() + ",isDone:" + isDone();
    }
}
