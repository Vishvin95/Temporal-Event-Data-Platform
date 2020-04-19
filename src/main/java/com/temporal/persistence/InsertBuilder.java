package com.temporal.persistence;

import java.util.ArrayList;
import java.util.List;

public class InsertBuilder extends AbstractSqlBuilder {

    private String table;

    private List<String> coloums;

    private List<String> values;

    public InsertBuilder(String table){
        this.table = table;
        this.coloums = new ArrayList<>();
        this.values = new ArrayList<>();
    }

    public InsertBuilder set(String column,String value){
        coloums.add(column);
        values.add(value);
        return this;
    }

    @Override
    public String toString(){
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ").append(table).append(" (");
        appendData(sqlBuilder,coloums,"",", ");
        sqlBuilder.append(") values (");
        appendData(sqlBuilder,values,"",", ");
        sqlBuilder.append(")");
        return sqlBuilder.toString();
    }
}
