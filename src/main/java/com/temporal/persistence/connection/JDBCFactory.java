package com.temporal.persistence.connection;

import com.temporal.persistence.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCFactory {
    public Connection connection;
    public static Logger logger = LogManager.getLogger(JDBCFactory.class);

    private Connection makeConnection(){
        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(Constants.DATABASE_PROPERTIES_FILE));
            String jdbcURL = properties.get(Constants.JDBC_URL).toString();
            properties.remove(Constants.JDBC_URL);
            Connection connection = DriverManager.getConnection(jdbcURL,properties.getProperty(Constants.USERNAME),properties.getProperty(Constants.PASSWORD));
            return connection;
        } catch (IOException | SQLException e) {
            logger.error(e);
            return null;
        }
    }

    public static Connection getConnection() {
        return new JDBCFactory().connection;
    }
    private JDBCFactory(){
        connection = makeConnection();
    }

}
