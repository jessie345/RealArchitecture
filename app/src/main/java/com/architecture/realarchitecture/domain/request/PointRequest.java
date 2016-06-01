package com.architecture.realarchitecture.domain.request;

import com.architecture.realarchitecture.datasource.base.AdvanceLocalStorage;
import com.architecture.realarchitecture.datasource.net.ResponseBean;
import com.architecture.realarchitecture.datasource.sql.table.TBPoint;
import com.architecture.realarchitecture.datasource.DALFactory;
import com.architecture.realarchitecture.domain.ApiHead;
import com.architecture.realarchitecture.domain.DataFrom;
import com.architecture.realarchitecture.domain.Request;
import com.architecture.realarchitecture.domain.strategy.httppost.PostDatas;
import com.architecture.realarchitecture.utils.LogUtils;
import com.architecture.realarchitecture.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liushuo on 16/3/21.
 */
public class PointRequest extends Request<Map<String, Object>, Map<String, Object>> {

    private static final int MAX_BEFORE_SEND = 2;//最小发送数量为20

    private Map<String, Object>[] mDatas;
    private AdvanceLocalStorage mSqlStorage;

    public PointRequest(Map<String, Object>... data) {
        super(TBPoint.DB_TABLE, Utils.buildUrl(ApiHead.POINTS));
        setRequestTag(null);//埋点请求不能取消，tag设置为null
        mDatas = data;
        mSqlStorage = DALFactory.getAdvanceStorage();
    }

    @Override
    protected Map<String, Object> transformForUiLayer(Map<String, Object> stringObjectMap) {
        return stringObjectMap;
    }


    @Override
    public void preNetRequest() {
    }

    @Override
    public void perform() {

        long count = 0;
        if (mDatas != null) {
            int length = mDatas.length;
            for (int i = 0; i < length && mDatas[i] != null; i++) {
                Map<String, Object> data = new HashMap<>();
                // TODO: 16/4/23 save content as String !!!!!!
                data.put(TBPoint.Column.COLUMN_CONTENT, Utils.map2JsonString(mDatas[i]));
                count = mSqlStorage.saveDatas(mDataType, data);
            }
        }
        if (count >= MAX_BEFORE_SEND) {
            //取出所有埋点，并发送
            List<Map<String, Object>> datas = mSqlStorage.queryItemsByTypes(mDataType);
            List<Map<String, Object>> points = new ArrayList<>();
            for (int i = 0; i < datas.size(); i++) {
                // TODO: 16/4/23 get content as String !!!!!!
                Map<String, Object> point = Utils.json2Map(Utils.getStringFromMap(TBPoint.Column.COLUMN_CONTENT, datas.get(i)));
                points.add(point);
            }


            PostDatas<List<Map<String, Object>>, Map<String, Object>> mPostStrategy = new PostDatas(null, mUrl, mRequestTag, this, points);
            mPostStrategy.postData();
            LogUtils.d("埋点数量:" + count + ",直接发送");
//            //埋点发送成功，删除缓存埋点
//            mSqlStorage.clearDatasOfType(mDataType);
//            //重置自增id
//            mSqlStorage.resetInternalRowId(mDataType);
        }
    }

    @Override
    protected void cacheNetResponse(Map<String, Object> data) {

    }

    @Override
    public void onResponse(DataFrom from, Map<String, Object> maps, boolean isDone) {
        super.onResponse(from, maps, isDone);
//        //埋点发送成功，删除缓存埋点
        mSqlStorage.clearDatasOfType(mDataType);
        //重置自增id
        mSqlStorage.resetInternalRowId(mDataType);

    }

    @Override
    public void onNetError(ResponseBean responseBean) {
    }
}
