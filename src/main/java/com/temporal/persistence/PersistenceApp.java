package com.temporal.persistence;

import com.temporal.model.InvalidScenarioException;
import com.temporal.model.Scenario;
import com.temporal.query.CreateQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

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

        Excecutor excecutor = new Excecutor();
        CreateQuery query = new CreateQuery();
        File file = new File("Scenario1.xml");
        Scenario scenario = null;
        try {
            scenario = Scenario.loadFromXML(file);
            scenario.printScenario();
            CreateQuery q=new CreateQuery();
            String s=q.CreateScenario(scenario);
            String[] queries = s.split(";");
            Arrays.stream(queries)
                    .map(GenericSqlBuilder::new)
                    .forEach(excecutor::addSqlQuery);
            excecutor.execute();
        } catch (SAXException | JAXBException | InvalidScenarioException e) {
            e.printStackTrace();
        }








    }
}
