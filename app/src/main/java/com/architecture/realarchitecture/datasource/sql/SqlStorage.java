package com.architecture.realarchitecture.datasource.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Pair;

import com.architecture.realarchitecture.datasource.base.AdvanceLocalStorage;
import com.architecture.realarchitecture.utils.Utils;
import com.architecture.realarchitecture.utils.LogUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by liushuo on 16/3/21.
 */
public class SqlStorage implements AdvanceLocalStorage {

    private DBHelper mHelper;

    private static SqlStorage mInstance;

    private SqlStorage(Context context) {
        mHelper = DBHelper.getInstance(context);
    }

    public static SqlStorage getInstance(Context context) {
        synchronized (SqlStorage.class) {
            if (mInstance == null) {
                mInstance = new SqlStorage(context);
            }
        }
        return mInstance;
    }


    /**
     * 客户端如何确保存入的数据和指定的table的对应关系
     *
     * @param dataType
     * @param datas
     */
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
            ContentValues values = Utils.map2ContentValues(data);

            if (values.size() == 0) continue;

            try {
                count = db.insertWithOnConflict(dataType, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                LogUtils.v("插入sql数据，返回row id:" + count + "--type:" + dataType);
            } catch (Exception e) {
                LogUtils.e(e);
            }
        }

        return count;

    }

    @Override
    public List<Map<String, Object>> queryItemsByIds(String dataType, Object... ids) {

        return queryItemsByColumn(dataType, BaseColumn.COLUMN_ID, ids);
    }

    @Override
    public List<Map<String, Object>> queryItemsByColumn(String dataType, String column, Object... values) {
        List<Map<String, Object>> list = new ArrayList<>();

        if (TextUtils.isEmpty(dataType) || TextUtils.isEmpty(column) || values == null || values.length == 0) {
            return list;
        }

        StringBuilder sql = new StringBuilder("select * from %1$s where %2$s");
        Pair<String, String[]> pair = Utils.buildSqlInSegment(values);
        sql.append(pair.first);
        String[] args = pair.second;

        if (args.length == 0) return list;

        Cursor cursor = null;
        try {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            cursor = db.rawQuery(String.format(sql.toString(), dataType, column), args);

            if (cursor == null) return list;

            while (cursor.moveToNext()) {
                Map<String, Object> map = Utils.cursor2Map(cursor);
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
            cursor = db.query(dataType, null, null, null, null, null, null);

            if (cursor == null) return list;

            while (cursor.moveToNext()) {
                Map<String, Object> map = Utils.cursor2Map(cursor);
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

        StringBuilder sql = new StringBuilder("delete from %1$s where %2$s");
        Pair<String, String[]> pair = Utils.buildSqlInSegment(ids);
        sql.append(pair.first);
        String[] args = pair.second;

        if (args.length == 0) return;

        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            db.execSQL(String.format(sql.toString(), dataType, BaseColumn.COLUMN_ID), args);
        } catch (Exception e) {
            LogUtils.e(e);
        }

    }


    @Override
    public void clearDataBase() {
        Field[] fields = SqlDataTypeDef.class.getDeclaredFields();
        if (fields == null || fields.length == 0) return;

        SQLiteDatabase db = null;
        try {
            db = mHelper.getWritableDatabase();
        } catch (Exception e) {
            LogUtils.e(e);
        }

        if (db == null) return;

        for (Field field : fields) {
            try {
                String table = (String) field.get(SqlDataTypeDef.class);
                db.delete(table, null, null);
            } catch (Exception e) {
                LogUtils.e(e);
            }
        }
    }

    @Override
    public void clearDatasOfType(String dataType) {
        if (TextUtils.isEmpty(dataType)) return;

        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            db.delete(dataType, null, null);
        } catch (Exception e) {
            LogUtils.e(e);
        }

    }

    @Override
    public void resetInternalRowId(String dataType) {
        if (TextUtils.isEmpty(dataType)) return;

        String sql = String.format("UPDATE sqlite_sequence SET seq = 0 WHERE name='%s'", dataType);
        try {
            SQLiteDatabase db = mHelper.getWritableDatabase();
            db.execSQL(sql);
        } catch (Exception e) {
            LogUtils.e(e);
        }

    }
}
