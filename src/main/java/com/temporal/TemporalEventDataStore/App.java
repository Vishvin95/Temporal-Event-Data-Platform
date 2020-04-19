package com.temporal.TemporalEventDataStore;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import com.temporal.model.*;
import com.temporal.persistence.InsertBuilder;
import com.temporal.persistence.PersistenceApp;
import com.temporal.persistence.SelectBuilder;
import com.temporal.persistence.SubSelectBuilder;
import org.xml.sax.SAXException;

public class App {
	public static void main(String[] args) throws FileNotFoundException, SAXException, JAXBException {

		//#5 : Tarang to avoid merge conflicts app file created for each module

		ModelApp.main();
		PersistenceApp.main();





	}
}
