package persistence;

import java.util.ArrayList;
import java.util.List;

public class SelectBuilder extends AbstractSqlBuilder {

    private boolean distinct;

    private List<String> tables = new ArrayList<>();

    private List<String> joins = new ArrayList<>();

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

        for(Object column : selectBuilder.columns){
            if(column instanceof SubSelectBuilder){
                this.columns.add(((SubSelectBuilder) column).clone());
            }else{
                this.columns.add(column);
            }
        }

        this.tables.addAll(selectBuilder.tables);
        this.joins.addAll(selectBuilder.joins);
        this.leftJoins.addAll(selectBuilder.leftJoins);
        this.wheres.addAll(selectBuilder.wheres);
        this.groupBys.addAll(selectBuilder.groupBys);
        this.havings.addAll(selectBuilder.havings);
        this.orderBys.addAll(selectBuilder.orderBys);

        for (SelectBuilder sb : selectBuilder.unions) {
            this.unions.add(sb.clone());
        }
    }

//    public SelectBuilder add(String expr){
//        return where(expr);
//    }

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

    public SelectBuilder leftJoin(String join){
        leftJoins.add(join);
        return this;
    }
}
