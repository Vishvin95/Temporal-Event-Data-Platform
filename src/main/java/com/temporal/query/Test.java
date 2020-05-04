package com.temporal.query;

import com.temporal.model.InputData;
import com.temporal.model.InvalidScenarioException;
import com.temporal.model.Scenario;
import com.temporal.persistence.Excecutor;
import com.temporal.persistence.GenericSqlBuilder;
import com.temporal.persistence.DBTablePrinter;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Test {

    public static void main(String... args) throws JAXBException, SAXException, InvalidScenarioException, SQLException {
//        File file = new File("Scenario1.xml");
//        Scenario scenario = Scenario.loadFromXML(file);
//        scenario.printScenario();
//        CreateQuery q=new CreateQuery();
//        String s=q.CreateScenario(scenario);
//        System.out.println(s);
//        Excecutor excecutor = new Excecutor();
//        excecutor.addSqlQuery(new GenericSqlBuilder("select * from boiler"));
//        List<ResultSzet> abc = excecutor.execute();
//        for(ResultSet r:abc)
//        {
//            DBTablePrinter.printResultSet(r);
//        }
        File file1 = new File("Insert.xml");
        InputData rawdata = InputData.loadFromXML(file1);
        InsertQuery.insert(rawdata.getTable());


    }
}



