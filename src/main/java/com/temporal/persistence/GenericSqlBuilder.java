package com.temporal.persistence;

public class GenericSqlBuilder extends AbstractSqlBuilder {
    String sqlQuery;
    public GenericSqlBuilder(String sqlQuery){
        this.sqlQuery = sqlQuery;
    }

    public String getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    @Override
    public String toString() {
        return sqlQuery;
    }
}
