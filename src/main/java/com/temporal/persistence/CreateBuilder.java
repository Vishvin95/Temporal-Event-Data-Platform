package com.temporal.persistence;

import java.util.ArrayList;
import java.util.List;

public class CreateBuilder extends AbstractSqlBuilder {
    public class DatabaseBuilder extends CreateBuilder{
        private String entityName;
        DatabaseBuilder(String database){
            this.entityName = database;
        }
        @Override
        public String toString(){
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("CREATE DATABASE ").append(entityName);
            return sqlBuilder.toString();
        }
    }
    public class TableBuilder extends CreateBuilder{
        private String entityName;
        List<Field> fields;
        TableBuilder(String table){
            this.entityName = table;
            this.fields = new ArrayList<>();
        }

        public TableBuilder addField(Field field){
            fields.add(field);
            return this;
        }

        @Override
        public String toString(){
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("CREATE TABLE ").append(entityName);
            appendData(sqlBuilder,fields," (\n",",\n");
            sqlBuilder.append("\n)");
            return sqlBuilder.toString();
        }
    }

    public DatabaseBuilder getDatabaseBuilder(String database){
        return new DatabaseBuilder(database);
    }

    public TableBuilder getTableBuilder(String table){
        return new TableBuilder(table);
    }


}
