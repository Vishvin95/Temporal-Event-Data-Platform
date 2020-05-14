package com.temporal.persistence.builder;

public final class Field{
    public enum Field_Type{
        INT("INT"),
        VARCAHR("VARCHAR(255)"),
        BIGINT("BIGINT"),
        FLOAT("FLOAT"),
        DOUBLE("DOUBLE"),
        DECIMAL("DECIMAL"),
        DATE("DATE"),
        DATETIME("DATETIME"),
        TIMESTAMP("TIMESTAMP"),
        TIME("TIME"),
        ENUM("ENUM"),
        SET("SET"),
        BOOLEAN("BOOLEAN")
        ;
        String value;
        Field_Type(String value){ this.value = value; }
        @Override
        public String toString(){
            return this.value;
        }

    }
    private String name;
    private Field_Type fieldType;
    private boolean AUTO_INCREMENT = false;
    private boolean NOT_NULL = false;
    private boolean PRIMARY_KEY = false;
    private boolean UNIQUE = false;
    private String DEFAULT = null;

    public Field(String name, Field_Type fieldType){
        this.name = name;
        this.fieldType = fieldType;
    }

    public Field autoIncrement(){
        this.AUTO_INCREMENT = true;
        return this;
    }
    public Field notNull(){
        this.NOT_NULL = true;
        return this;
    }
    public Field primaryKey(){
        this.PRIMARY_KEY = true;
        return this;
    }
    public Field isUnique(){
        this.UNIQUE = true;
        return this;
    }
    public Field defaultValue(String value){
        this.DEFAULT = value;
        return this;
    }

    @Override
    public String toString(){
        StringBuilder fieldBuilder = new StringBuilder();
        fieldBuilder.append(name).append(" ").append(fieldType);
        if(NOT_NULL) fieldBuilder.append(" NOT NULL");
        if(UNIQUE) fieldBuilder.append(" UNIQUE");
        if(PRIMARY_KEY) fieldBuilder.append(" PRIMARY KEY");
        if(DEFAULT!=null) fieldBuilder.append(" DEFAULT ").append(DEFAULT);
        if(AUTO_INCREMENT) fieldBuilder.append(" AUTO_INCREMENT");
        return fieldBuilder.toString();
    }
}
