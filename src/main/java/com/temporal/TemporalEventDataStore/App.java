package com.temporal.TemporalEventDataStore;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import com.temporal.persistence.SelectBuilder;
import com.temporal.persistence.SubSelectBuilder;
import org.xml.sax.SAXException;

import com.temporal.model.Scenario;

public class App {
	public static void main(String[] args) throws FileNotFoundException, SAXException, JAXBException {

		File file = new File("Scenario1.xml");

		Scenario scenario = Scenario.loadFromXML(file);
		scenario.printScenario();



		
	}
}
