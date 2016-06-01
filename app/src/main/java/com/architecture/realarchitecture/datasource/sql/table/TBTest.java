package com.architecture.realarchitecture.datasource.sql.table;

import com.architecture.realarchitecture.datasource.sql.BaseColumn;

/**
 * Created by liushuo on 16/1/18.
 */
public class TBTest {
    public static final String DB_TABLE = "test";

    public interface Column extends BaseColumn {
        String COLUMN_PARENT_ID = "parent_id";
        String COLUMN_CHILD_ID = "child_id";

    }
}
