package com.architecture.realarchitecture.datasource.nosql;

import android.content.Context;
import android.text.TextUtils;

import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.View;
import com.couchbase.lite.android.AndroidContext;
import com.architecture.realarchitecture.datasource.base.BaseLocalStorage;
import com.architecture.realarchitecture.utils.LogUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Copyright (c) 2015 Guanghe.tv All right reserved.
 * --------------------<-.->-----------------------
 * Author:      liushuo
 * CreateDate:  16/3/20
 */
public class CouchBaseStorage implements BaseLocalStorage {

    // 文档type key
    private static final String DATA_TYPE = "doc_type";

    private Database mDatabase;

    private static CouchBaseStorage mInstance;

    /**
     * 调用这需要进行判空处理（如果数据库无法初始化，则返回的实例为null）
     *
     * @param context
     * @return
     */
    public static CouchBaseStorage getInstance(Context context) {
        synchronized (CouchBaseStorage.class) {
            if (mInstance == null) {
                mInstance = new CouchBaseStorage();
                boolean success = mInstance.initDatabase(context);
                if (!success) {
                    mInstance = null;
                }
            }
        }
        return mInstance;
    }

    private CouchBaseStorage() {
    }


    /**
     * 建立view索引
     *
     * @param view
     * @param type
     */
    private void indexView(View view, String type) {
        if (view != null && view.getMap() == null) {
            view.setMap(new MyMapper(type), "1");
        }
    }


    @Override
    public synchronized void clearDataBase() {
        Query query = mDatabase.createAllDocumentsQuery();
        query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
        try {
            QueryEnumerator result = query.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document doc = row.getDocument();
                try {
                    if (doc != null) {
                        doc.delete();
                    }
                } catch (Exception e) {
                    LogUtils.e(e);
                }
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    /**
     * 初始化数据库manager和数据库实例，保证全局唯
     *
     * @param context 上下文
     * @return mDatabase
     */
    private synchronized boolean initDatabase(Context context) {

        if (context == null) {
            return false;
        }

        try {
            if (mDatabase == null) {
                Manager manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);
                mDatabase = manager.getDatabase("guanghetv");
            }
        } catch (Exception e) {
            LogUtils.d(e.getMessage());
            mDatabase = null;
        }

        if (mDatabase != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param dataType
     * @param datas
     */
    @Override
    public long saveDatas(String dataType, Map<String, Object>... datas) {
        if (TextUtils.isEmpty(dataType) || datas == null) {
            return 0;
        }

        int count = datas.length;
        for (int i = 0; i < count && datas[i] != null; i++) {
            doSaveData(dataType, datas[i]);
        }

        compatDatabase();

        return datas.length;
    }

    /**
     * 将json类型的字符串数据保存到数据库
     *
     * @param dataType 指定数据类型，必须传入
     * @param data
     */
    private synchronized void doSaveData(String dataType, final Map<String, Object> data) {

        String _id = (String) data.get("_id");

        if (TextUtils.isEmpty(_id)) {
            LogUtils.d("需要保存的数据没有id，无法执行保存操作:" + data.toString());
            return;
        }

        data.remove("__v");
        data.remove("_template");

        // get exist document or create one With the _id
        Document document = mDatabase.getDocument(_id);
//        //检测id是否存在
        if (document != null) {
            try {
                //设置 doc type的类型
                data.put(DATA_TYPE, dataType);
                // Save the properties to the document
                document.update(new Document.DocumentUpdater() {
                    @Override
                    public boolean update(UnsavedRevision unsavedRevision) {
                        unsavedRevision.setProperties(data);
                        return true;
                    }
                });

            } catch (Exception e) {
                LogUtils.e(e);
            }

        }
    }

    /**
     * 压缩数据库,删除无用的数据(old reversions)
     */
    private void compatDatabase() {
        if (mDatabase != null) {
            try {
                mDatabase.compact();
            } catch (Exception e) {
                LogUtils.e(e);
            }
        }
    }


    /**
     * 查询指定的json对象，以字符串形式返回
     * view 查询的version 为1
     *
     * @param ids
     * @param dataType
     * @return
     */


    @Override
    public List<Map<String, Object>> queryItemsByIds(final String dataType, Object... ids) {

        List<Map<String, Object>> datas = new ArrayList<>();

        View phoneView = mDatabase.getView(dataType);

        if (phoneView != null) {
            //建立索引，只建立一次
            indexView(phoneView, dataType);

            Query query = phoneView.createQuery();
            query.setKeys(Arrays.<Object>asList(ids));
            query.setLimit(ids.length);
            try {
                QueryEnumerator qe = query.run();
                for (Iterator<QueryRow> it = qe; it.hasNext(); ) {
                    QueryRow row = it.next();
                    Document doc = row.getDocument();
                    Map<String, Object> map = doc.getProperties();
                    if (map != null && map.size() > 0) {
                        datas.add(map);
                    }
                }


            } catch (Exception e) {
                LogUtils.e(e);
            }
        }


        return datas;
    }

    @Override
    public List<Map<String, Object>> queryItemsByTypes(final String dataType) {

        List<Map<String, Object>> datas = new ArrayList<>();

        View phoneView = mDatabase.getView(dataType);

        if (phoneView != null) {
            //建立索引，只建立一次
            indexView(phoneView, dataType);

            Query query = phoneView.createQuery();
            try {
                QueryEnumerator qe = query.run();
                for (Iterator<QueryRow> it = qe; it.hasNext(); ) {
                    QueryRow row = it.next();
                    Document doc = row.getDocument();
                    Map<String, Object> map = doc.getProperties();
                    if (map != null && map.size() > 0) {
                        datas.add(map);
                    }
                }


            } catch (Exception e) {
                LogUtils.e(e);
                String s = e.getMessage();
            }
        }


        return datas;
    }


    /**
     * 删除指定类型中的指定document
     *
     * @param ids
     * @param dataType
     */
    @Override
    public synchronized void deleteDatasById(final String dataType, Object... ids) {
        if (TextUtils.isEmpty(dataType) || ids == null) {
            LogUtils.d("非法的查询操作,dataType:" + dataType + ",ids:" + ids);
            return;
        }

        List<Document> docs = new ArrayList<Document>();
        View phoneView = mDatabase.getView(dataType);

        if (phoneView != null) {
            //建立索引，只建立一次
            indexView(phoneView, dataType);

            Query query = phoneView.createQuery();
            query.setKeys(Arrays.<Object>asList(ids));
            query.setLimit(ids.length);
            try {
                QueryEnumerator qe = query.run();
                for (Iterator<QueryRow> it = qe; it.hasNext(); ) {
                    QueryRow row = it.next();
                    Document doc = row.getDocument();
                    if (doc != null) {
                        //document有效
                        docs.add(doc);
                    }
                }
            } catch (Exception e) {
                LogUtils.e(e);
            }

            for (Document doc : docs) {
                try {
                    doc.delete();
                } catch (Exception e) {
                    LogUtils.e(e);
                }
            }

        }

        docs.clear();

    }

    @Override
    public void clearDatasOfType(String dataType) {
        List<Map<String, Object>> datas = queryItemsByTypes(dataType);
        for (Map<String, Object> data : datas) {
            if (data != null && data.containsKey("_id")) {
                String id = (String) data.get("_id");
                deleteDatasById(dataType, id);
            }
        }
    }

    private class MyMapper implements Mapper {
        private String mType;

        public MyMapper(String type) {
            mType = type;
        }


        @Override
        public void map(Map<String, Object> document, Emitter emitter) {
            String id = (String) document.get("_id");
            String type = (String) document.get(CouchBaseStorage.DATA_TYPE);
            if (!TextUtils.isEmpty(id) && TextUtils.equals(mType, type)) {
                emitter.emit(id, null);
            }
        }
    }
}
