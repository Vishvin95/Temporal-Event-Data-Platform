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
import com.temporal.query.UpdateQuery;
import jdk.nashorn.internal.runtime.regexp.joni.CodeRangeBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.cache.spi.entry.CacheEntryStructure;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.awt.*;
import java.awt.image.AreaAveragingScaleFilter;
import java.io.File;
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
            //makeSchema(s);
            Arrays.stream(s).forEach(System.out::println);
            ArrayList<ArrayList<ArrayList<Table>>> database = RandomDatabase.getDatabase();
            RandomDatabase.applyDatabase(database);


        } catch (Exception throwables) {

            throwables.printStackTrace();
        }
    }
}