package com.temporal.TemporalEventDataStore;

import javax.xml.bind.JAXBException;

import com.temporal.model.*;
import com.temporal.persistence.PersistenceApp;
import org.xml.sax.SAXException;


public class App {
	public static void main(String[] args) throws SAXException, JAXBException {

		//#5 : to avoid merge conflicts app file created for each module

		ModelApp.main();
		PersistenceApp.main();






	}
}