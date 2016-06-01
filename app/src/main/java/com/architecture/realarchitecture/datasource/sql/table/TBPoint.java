package com.architecture.realarchitecture.datasource.sql.table;

import com.architecture.realarchitecture.datasource.sql.BaseColumn;

/**
 * Created by liushuo on 16/3/21.
 */
public class TBPoint {
    public static final String DB_TABLE = "point";

    public interface Column extends BaseColumn {
        String COLUMN_CONTENT = "content";

    }
}
