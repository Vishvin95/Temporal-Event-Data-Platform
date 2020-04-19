package com.temporal.model;

import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.File;

public class ModelApp {
    public static void main(String... args) throws JAXBException, SAXException {
        File file = new File("Scenario1.xml");

        System.out.println("XML creation of scenario");
        System.out.println("------------------------");
        Scenario scenario = Scenario.loadFromXML(file);
        scenario.printScenario();

        Event e1 = new Event("salary", EventDataType.DECIMAL, EventType.MOE);
        Event e2 = new Event("empName", EventDataType.STRING, EventType.SOE);
        Domain d1 = new Domain.DomainBuilder("employee").addEvent(e1).addEvent(e2).build();

        Event e3 = new Event("dob", EventDataType.DECIMAL, EventType.SOE);
        Event e4 = new Event("dependentName", EventDataType.STRING, EventType.SOE);
        Domain d2 = new Domain.DomainBuilder("dependent").addEvent(e3).addEvent(e4).build();

        Event e5 = new Event("cost", EventDataType.DECIMAL, EventType.MOE);
        Event e6 = new Event("projectName", EventDataType.STRING, EventType.SOE);
        Domain d3 = new Domain.DomainBuilder("project").addEvent(e5).addEvent(e6).build();

        Relationship r1 = new Relationship("works_on", RelationshipType.MANY_TO_MANY, d1, d3);
        Relationship r2 = new Relationship("has", RelationshipType.ONE_TO_MANY, d1, d2);

        Scenario scenario1 = new Scenario.ScenarioBuilder("Company").addDomain(d1).addDomain(d2).addDomain(d3)
                .addRelationship(r1).addRelationship(r2).build();

        System.out.println();
        System.out.println("Manual Creation of Scenario");
        System.out.println("---------------------------");
        scenario1.printScenario();
    }
}
