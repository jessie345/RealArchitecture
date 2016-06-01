package com.architecture.realarchitecture.datasource.nosql.compatnosql;

/**
 * Created by liushuo on 16/3/21.
 */
class TBCouchBase {
    public static final String DB_TABLE = "compatibleCouchBase";

    public interface Column {
        String COLUMN_ID = "_id";
        String COLUMN_DOCUMENT_TYPE = "docType";
        String COLUMN_CONTENT = "content";
    }
}
