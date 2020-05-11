package com.temporal.persistence;

public class SubSelectBuilder extends SelectBuilder {

    private String alias;

    public SubSelectBuilder(SelectBuilder selectBuilder,String alias){
        super(selectBuilder);
        this.alias = alias;
    }

    public SubSelectBuilder(SubSelectBuilder subSelectBuilder){
        super(subSelectBuilder);
        this.alias = subSelectBuilder.alias;
    }

    @Override
    public SubSelectBuilder clone(){
        return new SubSelectBuilder(this);
    }

    public String toString(){
        return "(" +
                super.toString() +
                ") as " +
                alias;
    }
}
