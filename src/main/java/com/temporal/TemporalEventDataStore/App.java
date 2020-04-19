package com.temporal.TemporalEventDataStore;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import com.temporal.persistence.InsertBuilder;
import com.temporal.persistence.SelectBuilder;
import com.temporal.persistence.SubSelectBuilder;
import org.xml.sax.SAXException;

import com.temporal.model.Domain;
import com.temporal.model.Event;
import com.temporal.model.EventDataType;
import com.temporal.model.EventType;
import com.temporal.model.Relationship;
import com.temporal.model.RelationshipType;
import com.temporal.model.Scenario;

public class App {
	public static void main(String[] args) throws FileNotFoundException, SAXException, JAXBException {

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
