package com.temporal.persistence.builder;

import java.util.ArrayList;
import java.util.List;

public class UpdateBuilder extends AbstractSqlBuilder {

    private String table;
    private List<String> sets;
    private List<String> wheres;

    public UpdateBuilder(String table) {
    	this.table = table;
		sets = new ArrayList<String>();
		wheres = new ArrayList<String>();
	}
    
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
