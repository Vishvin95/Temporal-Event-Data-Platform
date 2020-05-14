package com.temporal.persistence.util;

import com.temporal.persistence.builder.CreateBuilder;
import com.temporal.persistence.builder.GenericSqlBuilder;
import com.temporal.persistence.builder.SelectBuilder;
import com.temporal.persistence.builder.UseBuilder;
import com.temporal.persistence.connection.Excecutor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class State {
    private String SCENARIO_FILE_PATH;

    private String CURRENT_DATABASE;

    private boolean DATABASE_SELECTED;

    public State() throws SQLException {
        this.SCENARIO_FILE_PATH = null;
        this.CURRENT_DATABASE = currentDatabase();
        this.DATABASE_SELECTED = false;
    }

    public String getSCENARIO_FILE_PATH() {
        return SCENARIO_FILE_PATH;
    }

    public void setSCENARIO_FILE_PATH(String SCENARIO_FILE_PATH) {
        this.SCENARIO_FILE_PATH = SCENARIO_FILE_PATH;
    }

    public String getCURRENT_DATABASE() {
        return CURRENT_DATABASE;
    }

    public boolean isDATABASE_SELECTED() { return this.DATABASE_SELECTED; }

    public void setCURRENT_DATABASE(String CURRENT_DATABASE) throws SQLException {
        useDatabase(CURRENT_DATABASE);
        this.CURRENT_DATABASE = CURRENT_DATABASE;
        this.DATABASE_SELECTED = true;
    }

    public void createCURRENT_DATABASE(String CURRENT_DATABASE) throws SQLException {
        createDatabase(CURRENT_DATABASE);
        setCURRENT_DATABASE(CURRENT_DATABASE);
    }

    public void dropCURRENT_DATABASE() throws SQLException {
        if(DATABASE_SELECTED) {
            new Excecutor()
                    .addSqlQuery(new GenericSqlBuilder("drop database "+CURRENT_DATABASE))
                    .execute();
            this.DATABASE_SELECTED = false;
            this.CURRENT_DATABASE = currentDatabase();
        }
    }

    private static void useDatabase(String databaseName) throws SQLException {
         new Excecutor()
                 .addSqlQuery(new UseBuilder(databaseName))
                 .execute();
    }

    private static String currentDatabase() throws SQLException {
        ResultSet resultSet = new Excecutor()
                .addSqlQuery(new GenericSqlBuilder("select database()"))
                .execute()
                .get(0);
        String current = null;
        while (resultSet.next()) current = resultSet.getString(1);
        return current;
    }

    private static void createDatabase(String CURRENT_DATABASE) throws SQLException {
        new Excecutor()
                .addSqlQuery(new CreateBuilder().getDatabaseBuilder(CURRENT_DATABASE))
                .execute();
    }

    private static boolean containsDatabase(String databaseName) throws SQLException {
        ResultSet resultSet = new Excecutor()
                .addSqlQuery(new SelectBuilder()
                        .column("SCHEMA_NAME")
                        .from("INFORMATION_SCHEMA.SCHEMATA")
                        .where(" SCHEMA_NAME = '"+databaseName+"'"))
                .execute()
                .get(0);
        String current = null;
        while (resultSet.next()) current = resultSet.getString(1);
        return current != null;
    }


}
