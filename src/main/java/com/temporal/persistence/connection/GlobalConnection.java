package com.temporal.persistence.connection;

import java.sql.Connection;

public class GlobalConnection {
    public static final Connection connection = JDBCFactory.getConnection();

    public static Connection getConnection() {
        return connection;
    }
}
