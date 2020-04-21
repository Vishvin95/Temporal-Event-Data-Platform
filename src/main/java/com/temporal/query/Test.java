package com.temporal.query;

import com.temporal.model.Scenario;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.File;

public class Test {

    public static void main(String... args) throws JAXBException, SAXException {
        File file = new File("Scenario1.xml");
        Scenario scenario = Scenario.loadFromXML(file);
        scenario.printScenario();
        //System.out.println("------");
        Query q=new Query();
        String s=q.CreateScenario(scenario);
        System.out.println(s);
    }
}



