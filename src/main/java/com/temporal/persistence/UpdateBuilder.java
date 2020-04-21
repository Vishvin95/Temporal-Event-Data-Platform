package com.temporal.persistence;

import java.util.ArrayList;
import java.util.List;

public class UpdateBuilder extends AbstractSqlBuilder {

    private String table;

    private List<String> sets = new ArrayList<>();

    private List<String> wheres = new ArrayList<>();

    public UpdateBuilder set(String expression){
        sets.add(expression);
        return this;
    }

    public UpdateBuilder where(String expression){
        wheres.add(expression);
        return this;
    }

    @Override
    public String toString(){
        StringBuilder sqlBuilder = new StringBuilder("UPDATE ").append(table);
        appendData(sqlBuilder,sets,"SET ",", ");
        appendData(sqlBuilder,wheres,"WHERE "," AND ");
        return sqlBuilder.toString();
    }
}
