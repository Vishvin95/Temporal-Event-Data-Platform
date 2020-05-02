package com.temporal.model;

import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;

import java.io.File;

public class ModelApp {
	public static void main(String... args) throws JAXBException, SAXException {

		try {
			File file = new File("Scenario1.xml");
			System.out.println("XML creation of scenario");
			System.out.println("------------------------");
			Scenario scenario = Scenario.loadFromXML(file);
			scenario.printScenario();
		} catch (UnmarshalException unmarshalException) {
			unmarshalException.printStackTrace();
		} catch (InvalidScenarioException invalidScenarioException) {
			invalidScenarioException.printStackTrace();
		}

		try {
			File validationFile = new File("Scenario2_validator.xml");
			System.out.println("Check for validation of scenario");
			System.out.println("--------------------------------");
			Scenario validationScenario = Scenario.loadFromXML(validationFile);
			validationScenario.printScenario();
		} catch (UnmarshalException unmarshalException) {
			/*
			 * Validation cases handled by XSD: 
			 * [1] At least one domain required 
			 * [2] Unique domain name 
			 * [3] Duplicate event in same domain 
			 * [4] Relationships specified, but no relationship inside
			 */
			unmarshalException.printStackTrace();
		} catch (InvalidScenarioException invalidScenarioException) {
			/*
			 * Validation cases handled by validate method:
			 * [1] Duplicate Relationships throws DuplicateRelationshipException
			 *		A B 11 and A B 11			
			 * 		A B 11 and B A 11  
			 * 		A B nn and B A nn
			 */
			invalidScenarioException.printStackTrace();
		}

//		Event e1 = new Event("salary", EventDataType.DECIMAL,true,false,true);
//		Event e2 = new Event("empName", EventDataType.STRING,true,false,false);
//		Domain d1 = new Domain.DomainBuilder("employee", "empName").addEvent(e1).addEvent(e2).build();
//
//		Event e3 = new Event("dob", EventDataType.DECIMAL,false,false,false);
//		Event e4 = new Event("dependentName", EventDataType.STRING,true,false,false);
//		Domain d2 = new Domain.DomainBuilder("dependent","dependentName").addEvent(e3).addEvent(e4).build();
//
//		Event e5 = new Event("cost", EventDataType.DECIMAL,true,false,true);
//		Event e6 = new Event("projectName", EventDataType.STRING,true,false,false);
//		Domain d3 = new Domain.DomainBuilder("project","projectName").addEvent(e5).addEvent(e6).build();
//
//		Relationship r1 = new Relationship("works_on", RelationshipType.MANY_TO_MANY, d1, d3);
//		Relationship r2 = new Relationship("has", RelationshipType.ONE_TO_MANY, d1, d2);
//		
//		try {
//			Scenario scenario1 = new Scenario.ScenarioBuilder("Company").addDomain(d1).addDomain(d2).addDomain(d3)
//					.addRelationship(r1).addRelationship(r2).build();
//			System.out.println();
//			System.out.println("Manual Creation of Scenario");
//			System.out.println("---------------------------");
//			scenario1.printScenario();
//		} catch (InvalidScenarioException invalidScenarioException) {
//			invalidScenarioException.printStackTrace();
//		}
		
		RawData data = RawData.loadFromXML(new File("RawData.xml"));
		System.out.println();
	}
}
