package com.temporal.model;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

@XmlRootElement(name = "inputData")
public class InputData {
	private Table table;
	
	public InputData() {
	
	}

	@Override
	public String toString() {
		return "InputData{" +
				"table=" + table +
				'}';
	}

	@XmlElement(name="table")
	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public static InputData loadFromXML(File file) throws SAXException, JAXBException {
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema scenarioSchema = sf
				.newSchema(Thread.currentThread().getContextClassLoader().getResource("Insert.xsd"));
		JAXBContext jaxbContext = JAXBContext.newInstance(InputData.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		unmarshaller.setSchema(scenarioSchema);
		InputData data = (InputData) unmarshaller.unmarshal(file);
		return data;
	}

}
