package com.temporal.persistence;

public class PersistenceApp {
    public static void main(String... args){
        //Sql builder example

        SelectBuilder sb = new SelectBuilder()
                .column("name")
                .from("employee")
                .where("age > 20")
                .distinct();


        SelectBuilder sb2 = new SelectBuilder()
                .column("T.name")
                .from(new SubSelectBuilder(sb,"T"));

        System.out.println(sb2);


        //Insert Builder
        InsertBuilder ib = new InsertBuilder("employee").set("name","'Tarang'").set("age","4");

        System.out.println(ib);
    }
}
