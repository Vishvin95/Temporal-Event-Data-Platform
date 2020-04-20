package com.temporal.persistence;

import java.util.ArrayList;
import java.util.List;

public class DeleteBuilder extends AbstractSqlBuilder {

    private String table;

    private List<String> wheres = new ArrayList<>();

    public DeleteBuilder(String table){
        this.table = table;
    }

    @Override
    public String toString(){
        StringBuilder sqlBuilder = new StringBuilder("DELETE FROM ").append(table);
        appendData(sqlBuilder,wheres,"WHERE "," AND ");
        return sqlBuilder.toString();
    }

    public DeleteBuilder where(String expression){
        wheres.add(expression);
        return this;
    }
}
