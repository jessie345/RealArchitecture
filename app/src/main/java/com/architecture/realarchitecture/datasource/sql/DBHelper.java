package com.architecture.realarchitecture.datasource.sql;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.architecture.realarchitecture.datasource.sql.table.TBTest;

/**
 * Created by liushuo on 16/1/12.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";

    private static final String DB_NAME = "Test_DB.db";
    private static final int DB_VERSION = 2;

    private static DBHelper mDBHelper;

    public static DBHelper getInstance(Context context) {
        synchronized (DBHelper.class) {
            if (mDBHelper == null) {
                mDBHelper = new DBHelper(context);
            }
            return mDBHelper;
        }
    }


    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        onUpgrade(db, 0, 1);
    }

    /**
     * 数据库升级，老数据清空
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        createTestTable(db);//视频，知识点关系表，同时记录视频的缓存状态
    }


    private void createTestTable(SQLiteDatabase db) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TBTest.DB_TABLE);
            db.execSQL("CREATE TABLE " + TBTest.DB_TABLE + "(" + TBTest.Column.COLUMN_ID
                    + " TEXT PRIMARY KEY,"
                    + TBTest.Column.COLUMN_CHILD_ID + " TEXT, "
                    + TBTest.Column.COLUMN_PARENT_ID + " TEXT); ");
        } catch (SQLException ex) {
            Log.e(TAG,
                    String.format("couldn't create table in %s database", DB_NAME));
            throw ex;
        }
    }


}
