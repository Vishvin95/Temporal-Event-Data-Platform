package com.temporal.persistence;
/* 
    Apricot Management Suite
    Copyright (C) 2020 Tarang Parikh
    
    Email : tp0265@gmail.com
    Project Home : https://github.com/tarangparikh/apricot
    
    Original Author : @author Tarang Parikh <tp0265@gmail.com>
    
*/


import com.temporal.model.*;
import com.temporal.query.CreateQuery;
import com.temporal.query.InsertQuery;
import com.temporal.query.TemporalQuery;
import com.temporal.query.UpdateQuery;
import jdk.nashorn.internal.runtime.regexp.joni.CodeRangeBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.cache.spi.entry.CacheEntryStructure;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.awt.*;
import java.awt.image.AreaAveragingScaleFilter;
import java.io.File;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


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
//            ArrayList<ArrayList<ArrayList<Table>>> database = RandomDatabase.getDatabase();
//            RandomDatabase.applyDatabase(database);
            String table = "boiler";
            String attr = "pressure";
//            SelectBuilder lastView = TemporalQuery
//                    .createLastView(new SelectBuilder().from(table+"_"+attr), table, attr);

//            SelectBuilder selectBuilder = new SelectBuilder()
//                    .column("@rn:=@rn+1 as no")
//                    .column("T.*")
//                    .from(new SubSelectBuilder(new SelectBuilder().from(table+"_"+attr),"T"))
//                    .from("(SELECT @rn:=0) as r")
//                    .orderBy("T.boilerCode")
//                    .orderBy("T.valid_from");

             SelectBuilder selectBuilder = new SelectBuilder()

                     .column("LAG(value,1) over ( partition by boilerCode order by valid_from) as prev_value")
                     .from("boiler_pressure")
                     .where("id < 5");

             TemporalQuery.createPreviousView(null,null,null);

        } catch (Exception throwables) {

            throwables.printStackTrace();
        }
    }
}