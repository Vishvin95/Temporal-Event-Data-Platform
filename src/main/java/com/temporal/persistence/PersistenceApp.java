package com.temporal.persistence;

import com.sun.javafx.util.Logging;
import com.temporal.model.InvalidScenarioException;
import com.temporal.model.Scenario;
import com.temporal.query.CreateQuery;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;
import sun.security.x509.X500Name;

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


        //Use Builder
        UseBuilder ub = new UseBuilder("factory");
        System.out.println(ub);
        
        //CreateDatabase
        CreateBuilder cd = new CreateBuilder().getDatabaseBuilder("factory");
        System.out.println(cd);




        //final Connection connection = GlobalConnection.getConnection();

        Excecutor excecutor = new Excecutor();

        CreateQuery query = new CreateQuery();
        File file = new File("Scenario1.xml");
        Scenario scenario = null;
        try {
            //Load scenario xml
            scenario = Scenario.loadFromXML(file);
            scenario.printScenario();
            CreateQuery q=new CreateQuery();

            //Creating DDL for the table
            String s=q.CreateScenario(scenario);
            String[] queries = s.split(";");

            //Executing the results
            Arrays.stream(queries)
                    .map(GenericSqlBuilder::new)
                    .forEach(excecutor::addSqlQuery);
            List<ResultSet> execute = null;
            try {
                execute = excecutor.execute();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            execute.forEach(DBTablePrinter::printResultSet);

        } catch (SAXException | JAXBException | InvalidScenarioException e) {
            e.printStackTrace();
        }




    }
}
