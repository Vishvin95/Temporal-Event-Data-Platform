package com.temporal.persistence.builder;

import java.util.ArrayList;
import java.util.List;

public class SelectBuilder extends AbstractSqlBuilder {

    private boolean distinct;

    private List<Object> tables = new ArrayList<>();

    private List<Object> joins = new ArrayList<>();

    private List<String> leftJoins = new ArrayList<>();

    private List<String> groupBys = new ArrayList<>();

    private List<String> havings = new ArrayList<>();

    private List<SelectBuilder> unions = new ArrayList<>();

    private List<String> orderBys = new ArrayList<>();

    private List<Object> columns = new ArrayList<>();

    private List<String> wheres = new ArrayList<>();

    private int offset = 0;

    private int limit = 0;

    private boolean forUpdate;

    private boolean noWait;

    public SelectBuilder() {
    }

    protected SelectBuilder(SelectBuilder selectBuilder){
        this.distinct = selectBuilder.distinct;
        this.forUpdate = selectBuilder.forUpdate;
        this.noWait = selectBuilder.noWait;

        this.columns.addAll(selectBuilder.columns);

        for(Object table : selectBuilder.tables){
            if(table instanceof SubSelectBuilder){
                this.tables.add(((SubSelectBuilder) table).clone());
            }else{
                this.tables.add(table);
            }
        }

        for(Object table : selectBuilder.joins){
            if(table instanceof SubSelectBuilder){
                this.joins.add(((SubSelectBuilder) table).clone());
            }else{
                this.joins.add(table);
            }
        }

        //this.tables.addAll(selectBuilder.tables);
        //this.joins.addAll(selectBuilder.joins);
        this.leftJoins.addAll(selectBuilder.leftJoins);
        this.wheres.addAll(selectBuilder.wheres);
        this.groupBys.addAll(selectBuilder.groupBys);
        this.havings.addAll(selectBuilder.havings);
        this.orderBys.addAll(selectBuilder.orderBys);

        for (SelectBuilder sb : selectBuilder.unions) {
            this.unions.add(sb.clone());
        }
    }

    public SelectBuilder add(String expr){
        return where(expr);
    }

    public SelectBuilder column(String name){
        columns.add(name);
        return this;
    }

    public SelectBuilder column(SubSelectBuilder subSelectBuilder){
        columns.add(subSelectBuilder);
        return this;
    }

    public SelectBuilder column(String name,boolean groupBy){
        columns.add(name);
        if(groupBy){
            groupBys.add(name);
        }
        return this;
    }

    public SelectBuilder limit(int limit,int offset){
        this.limit = limit;
        this.offset = offset;
        return this;
    }

    public SelectBuilder limit(int limit){
        return limit(limit,0);
    }

    @Override
    public SelectBuilder clone() {return new SelectBuilder(this);}


    public SelectBuilder distinct(){
        this.distinct = true;
        return this;
    }

    public SelectBuilder forUpdate(){
        this.distinct = true;
        return this;
    }

    public SelectBuilder from(String table){
        tables.add(table);
        return this;
    }

    public SelectBuilder from(SubSelectBuilder subSelectBuilder){
        tables.add(subSelectBuilder);
        return this;
    }



    public List<SelectBuilder> getUnions() {return unions;}

    public SelectBuilder groupBy(String expr){
        groupBys.add(expr);
        return this;
    }

    public SelectBuilder having(String expr){
        havings.add(expr);
        return this;
    }

    public SelectBuilder join(String join){
        joins.add(join);
        return this;
    }

    public SelectBuilder join(SubSelectBuilder subSelectBuilder){
        joins.add(subSelectBuilder);
        return this;
    }

    public SelectBuilder leftJoin(String join){
        leftJoins.add(join);
        return this;
    }

    public SelectBuilder noWait(){
        if(!forUpdate){
            throw new RuntimeException("ERROR : FOR UPDATE not Set");
        }
        noWait = true;
        return this;
    }

    public SelectBuilder orderBy(String name){
        orderBys.add(name);
        return this;
    }

    public SelectBuilder orderBy(String name,boolean ascending){
        if(ascending) orderBys.add(name + " asc");
        else orderBys.add(name+" desc");
        return this;
    }

    public String toString(){
        StringBuilder sqlBuilder = new StringBuilder("SELECT ");

        if(distinct){
            sqlBuilder.append("DISTINCT ");
        }

        if(columns.size() == 0){
            sqlBuilder.append("*");
        }else{
            appendData(sqlBuilder,columns,"",", ");
        }

        appendData(sqlBuilder,tables," FROM ",", ");
        appendData(sqlBuilder,joins," JOIN "," JOIN ");
        appendData(sqlBuilder,leftJoins," LEFT JOIN "," LEFT JOIN ");
        appendData(sqlBuilder,wheres," WHERE "," AND ");
        appendData(sqlBuilder,groupBys," GROUP BY ",", ");
        appendData(sqlBuilder,havings," HAVING "," AND ");
        appendData(sqlBuilder,unions," UNION ", " UNION ");
        appendData(sqlBuilder,orderBys," ORDER BY ",", ");

        if(forUpdate){
            sqlBuilder.append(" FOR UPDATE ");
            if(noWait){
                sqlBuilder.append(" NOWAIT ");
            }
        }

        if(limit>0){
            sqlBuilder.append(" LIMIT "+limit);
        }

        if(offset > 0){
            sqlBuilder.append(", "+offset);
        }
        return sqlBuilder.toString();
    }

    public SelectBuilder union(SelectBuilder union){
        unions.add(union);
        return this;
    }
    public SelectBuilder where(String expr){
        wheres.add(expr);
        return this;
    }
}
