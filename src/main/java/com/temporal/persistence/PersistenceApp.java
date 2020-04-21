package com.temporal.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Properties;

public class PersistenceApp {
    public static Logger logger = LogManager.getLogger(PersistenceApp.class);
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

        //CreateBuilder
        CreateBuilder cb = new CreateBuilder().getDatabaseBuilder("zpro");

        CreateBuilder.TableBuilder cbt = new CreateBuilder().getTableBuilder("zpro")
                .addField(new Field("id", Field.Field_Type.VARCAHR).autoIncrement().primaryKey());




        final Connection connection = GlobalConnection.getConnection();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select *from ForeCast");
            DBTablePrinter.printResultSet(resultSet);
        } catch (SQLException e) {
            logger.error(e);
        }


    }
}
