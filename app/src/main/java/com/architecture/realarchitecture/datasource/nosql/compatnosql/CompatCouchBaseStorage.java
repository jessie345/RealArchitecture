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
package com.architecture.realarchitecture.datasource.nosql.compatnosql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Pair;

import com.architecture.realarchitecture.datasource.base.BaseLocalStorage;
import com.architecture.realarchitecture.utils.Utils;
import com.architecture.realarchitecture.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Android Studio
 * User: killnono(陈凯)
 * Date: 16/5/9
 * Time: 下午2:02  兼容 api 16前,sql实现的couchbase兼容数据库
 * Version: 1.0
 */
public class CompatCouchBaseStorage implements BaseLocalStorage {

    private DBHelper mHelper;
    private static CompatCouchBaseStorage mInstance;

    private CompatCouchBaseStorage(Context context) {
        mHelper = DBHelper.getInstance(context);
    }

    public static CompatCouchBaseStorage getInstance(Context context) {
        synchronized (CompatCouchBaseStorage.class) {
            if (mInstance == null) {
                mInstance = new CompatCouchBaseStorage(context);
            }
        }
        return mInstance;
    }


    @Override
    public long saveDatas(String dataType, Map<String, Object>... datas) {
        long count = -1;

        if (TextUtils.isEmpty(dataType) || datas == null || datas.length == 0) return count;

        SQLiteDatabase db = null;
        try {
            db = mHelper.getWritableDatabase();
        } catch (Exception e) {
            LogUtils.e(e);
        }

        if (db == null) return count;

        int length = datas.length;
        for (int i = 0; i < length && datas[i] != null; i++) {
            Map<String, Object> data = datas[i];

            String id = (String) data.get("_id");
            if (TextUtils.isEmpty(id)) {
                LogUtils.d("需要保存的数据没有id，无法执行保存操作:" + data.toString());
                continue;
            }

            ContentValues values = new ContentValues();
            values.put(TBCouchBase.Column.COLUMN_ID, id);
            values.put(TBCouchBase.Column.COLUMN_DOCUMENT_TYPE, dataType);
            values.put(TBCouchBase.Column.COLUMN_CONTENT, Utils.map2JsonString(data));

            try {
                count = db.insertWithOnConflict(TBCouchBase.DB_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            } catch (Exception e) {
                LogUtils.e(e);
            }
        }
        return count;

    }

    @Override
    public List<Map<String, Object>> queryItemsByIds(String dataType, Object... ids) {
        List<Map<String, Object>> list = new ArrayList<>();

        if (TextUtils.isEmpty(dataType) || ids == null || ids.length == 0) return list;

        //select * from table where doctype =? and id in (?,...);
        StringBuilder sql = new StringBuilder("select * from %1$s where %2$s=? and %3$s");
        Pair<String, String[]> pair = Utils.buildSqlInSegment(ids);
        sql.append(pair.first);
        String[] args = pair.second;

        if (args.length == 0) return list;

        String[] arr = new String[args.length + 1];
        arr[0] = dataType;
        System.arraycopy(args, 0, arr, 1, args.length);

        Cursor cursor = null;
        try {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            cursor = db.rawQuery(String.format(sql.toString(), TBCouchBase.DB_TABLE, TBCouchBase.Column.COLUMN_DOCUMENT_TYPE, TBCouchBase.Column.COLUMN_ID), arr);

            if (cursor == null) return list;

            while (cursor.moveToNext()) {
                // TODO: 16/5/10  cursor --> contentValuesMap-->contentJsonString---->real map
                Map<String, Object> contentValuesMap = Utils.cursor2Map(cursor);
                String content = Utils.getStringFromMap(TBCouchBase.Column.COLUMN_CONTENT, contentValuesMap);
                Map<String, Object> map = Utils.json2Map(content);
                if (!map.isEmpty()) {
                    list.add(map);
                }
            }
        } catch (Exception e) {
            LogUtils.e(e);
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    LogUtils.e(e);
                }
            }
        }

        return list;
    }

    @Override
    public List<Map<String, Object>> queryItemsByTypes(String dataType) {
        List<Map<String, Object>> list = new ArrayList<>();

        if (TextUtils.isEmpty(dataType)) return list;

        Cursor cursor = null;
        try {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            cursor = db.query(TBCouchBase.DB_TABLE, null, TBCouchBase.Column.COLUMN_DOCUMENT_TYPE + "=?", new String[]{dataType}, null, null, null);

            if (cursor == null) return list;

            while (cursor.moveToNext()) {
                Map<String, Object> contentValuesMap = Utils.cursor2Map(cursor);
                String content = Utils.getStringFromMap(TBCouchBase.Column.COLUMN_CONTENT, contentValuesMap);
                Map<String, Object> map = Utils.json2Map(content);
                if (!map.isEmpty()) {
                    list.add(map);
                }
            }
        } catch (Exception e) {
            LogUtils.e(e);
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    LogUtils.e(e);
                }
            }
        }
        return list;
    }

    @Override
    public void deleteDatasById(String dataType, Object... ids) {
        if (TextUtils.isEmpty(dataType) || ids == null || ids.length == 0) return;

        //delete from table where doctype =? and id in (?,...);
        StringBuilder sql = new StringBuilder("delete from %1$s where %2$s=? and %3$s");
        Pair<String, String[]> pair = Utils.buildSqlInSegment(ids);
        sql.append(pair.first);
        String[] args = pair.second;

        if (args.length == 0) return;

        String[] arr = new String[args.length + 1];
        arr[0] = dataType;
        System.arraycopy(args, 0, arr, 1, args.length);

        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            db.execSQL(String.format(sql.toString(), TBCouchBase.DB_TABLE, TBCouchBase.Column.COLUMN_DOCUMENT_TYPE, TBCouchBase.Column.COLUMN_ID), arr);

        } catch (Exception e) {
            LogUtils.e(e);
        }

    }

    @Override
    public void clearDatasOfType(String dataType) {
        if (TextUtils.isEmpty(dataType)) return;

        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            db.delete(TBCouchBase.DB_TABLE, TBCouchBase.Column.COLUMN_DOCUMENT_TYPE + "=?", new String[]{dataType});
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    @Override
    public void clearDataBase() {
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            db.delete(TBCouchBase.DB_TABLE, null, null);
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }
}
