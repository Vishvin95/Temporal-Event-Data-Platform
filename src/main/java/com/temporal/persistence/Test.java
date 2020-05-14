package com.temporal.persistence;
/* 
    Apricot Management Suite
    Copyright (C) 2020 Tarang Parikh
    
    Email : tp0265@gmail.com
    Project Home : https://github.com/tarangparikh/apricot
    
    Original Author : @author Tarang Parikh <tp0265@gmail.com>
    
*/


import com.temporal.model.*;
import com.temporal.persistence.builder.GenericSqlBuilder;
import com.temporal.persistence.builder.SelectBuilder;
import com.temporal.persistence.connection.Excecutor;
import com.temporal.persistence.util.DBTablePrinter;
import com.temporal.persistence.util.RandomDatabase;
import com.temporal.persistence.util.State;
import com.temporal.query.CreateQuery;
import com.temporal.query.InsertQuery;
import com.temporal.query.TemporalQuery;
import com.temporal.query.UpdateQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class Test {
    public static String FILE_NAME = "Scenario1.xml";
    public static String INPUT_XML = "Insert.xml";
    public static String DATABASE = "Factory";
    public static String[] getTables() {
        return null;
    }
   public static void makeSchema(String[] query) throws SQLException {
        Excecutor excecutor = new Excecutor();
        Arrays.stream(query).map(GenericSqlBuilder::new)
                    .forEach(excecutor::addSqlQuery);
        excecutor.execute();
    }

    public static String[] getCreateQuery(String createXml) throws SAXException, JAXBException, InvalidScenarioException {
        CreateQuery createQuery = new CreateQuery();
        String s = createQuery.CreateScenario(Scenario.loadFromXML(new File(FILE_NAME)));
        return s.split(";");
    }
    public static InputData getData(String filepath) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(InputData.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (InputData) jaxbUnmarshaller.unmarshal(new File(filepath));
    }
    public static String makeData(InputData inputData) throws JAXBException, SAXException {
        System.out.println(inputData);
        StringWriter sw = new StringWriter();
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema scenarioSchema = sf
                .newSchema(Thread.currentThread().getContextClassLoader().getResource("Insert.xsd"));
        JAXBContext jaxbContext = JAXBContext.newInstance(InputData.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        //marshaller.setSchema(scenarioSchema);
        marshaller.marshal(inputData,sw);
        return sw.toString();
    }
    public static void makeFile(String path,InputData inputData,String name) throws IOException, JAXBException, SAXException {
        File file = new File(path,name);
        if(!file.exists()) file.createNewFile();

        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(makeData(inputData));
        fileWriter.flush();
        fileWriter.close();
    }

    public static Logger logger = LogManager.getLogger(Test.class);
    public static void main(String... args) {
        try {
            State state = new State();
            if(!state.isDATABASE_SELECTED()){
                state.setCURRENT_DATABASE(DATABASE);
            }
            //Make Schema;
            //makeSchema(new CreateQuery().CreateScenario(Scenario.loadFromXML(new File(FILE_NAME))).split(";"));
            String[] s = new CreateQuery().CreateScenario(Scenario.loadFromXML(new File(FILE_NAME))).split(";");
            //Arrays.stream(s).forEach(System.out::println);

//            makeSchema(s);
            ArrayList<ArrayList<ArrayList<Table>>> database = RandomDatabase.getDatabase();
//            for (ArrayList<ArrayList<Table>> arrayLists : database) {
//                int counter = 0;
//                for (ArrayList<Table> tables : arrayLists) {
//                    for (int i = 0, h = tables.size(); i < h; i++) {
//                        InputData inputData = new InputData();
//                        inputData.setTable(tables.get(i));
//                        makeFile("./data",inputData,inputData.getTable().getName()+"_"+counter+".xml");
//                        counter++;
//                        //Thread.sleep(100000);
//                    }
//                }
//            }
           // System.out.println(getData("./insertBoiler/boiler_0.xml"));


//            String table = "boiler";
//            String attr = "pressure";
//            SelectBuilder lastView = TemporalQuery
//                    .createLastView(new SelectBuilder().from(table+"_"+attr), table, attr);

//            SelectBuilder selectBuilder = new SelectBuilder()
//                    .column("@rn:=@rn+1 as no")
//                    .column("T.*")
//                    .from(new SubSelectBuilder(new SelectBuilder().from(table+"_"+attr),"T"))
//                    .from("(SELECT @rn:=0) as r")
//                    .orderBy("T.boilerCode")
//                    .orderBy("T.valid_from");

//             SelectBuilder selectBuilder = new SelectBuilder()
//
//                     .column("LAG(value,1) over ( partition by boilerCode order by valid_from) as prev_value")
//                     .from("boiler_pressure")
//                     .where("id < 5");
//
//             TemporalQuery.createNextScaleView(new SelectBuilder().from("supervisor_salary"),"supervisor","145",5);

            //System.out.println(TemporalQuery.createTemporalView("boiler"));
//            String path = "Insert.xml";
//            String file = "./data/boiler_1.xml";
//            System.out.println(InputData.loadFromXML(new File(file)));
//            InputData inputData = new InputData();
//            inputData.setTable(database.get(0).get(0).get(0));
//            System.out.println(makeData(inputData));
//            SelectBuilder boiler = TemporalQuery.createTemporalJoinView("boiler","pump","T.pumpCode = B.pumpCode");
//            System.out.println(boiler);
            String ll = "tselect evolution pressure from boiler";
            String nq = "tselect evolution pressure,last pressure from boiler";
            String sq = "tselect evolution salary,last salary from supervisor";
            String q = "tselect previous salary 354 from supervisor";
            String qa= "tjoin boiler pump T.pumpCode=B.pumpCode";
            String nn = "tselect previous salary from supervisor";
            SelectBuilder nextView = TemporalQuery.createPreviousView(new SelectBuilder().from("supervisor_salary"), "supervisor", "225");
            System.out.println(nextView);




            ResultSet resultSet = TemporalQuery.resolveQuery(qa);
            DBTablePrinter.printResultSet(resultSet);
            //TemporalQuery.getTables();


        } catch (Exception throwables) {

            throwables.printStackTrace();
        }
    }
}