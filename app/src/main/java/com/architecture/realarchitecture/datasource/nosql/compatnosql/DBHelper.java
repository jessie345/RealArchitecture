package com.architecture.realarchitecture.datasource.nosql.compatnosql;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by liushuo on 16/1/12.
 */
class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";

    private static final String DB_NAME = "compat_nosql.db";
    private static final int DB_VERSION = 1;

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
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        createCompatibleCouchBaseTable(db);
    }


    // TODO: 16/5/9 sqlite实现的couchbase ,兼容 pre api 15
    private void createCompatibleCouchBaseTable(SQLiteDatabase db) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TBCouchBase.DB_TABLE);
            db.execSQL("CREATE TABLE " + TBCouchBase.DB_TABLE + "(" + TBCouchBase.Column.COLUMN_ID
                    + " TEXT PRIMARY KEY,"
                    + TBCouchBase.Column.COLUMN_DOCUMENT_TYPE + " TEXT, "
                    + TBCouchBase.Column.COLUMN_CONTENT + " TEXT); ");
        } catch (SQLException ex) {
            Log.e(TAG,
                    String.format("couldn't create table in %s database", DB_NAME));
            throw ex;
        }
    }

}
