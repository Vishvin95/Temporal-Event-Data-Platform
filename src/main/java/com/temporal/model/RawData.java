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
public class RawData {
	private Table table;

	@XmlElement(name="table")
	public Table getRawReadings() {
		return table;
	}

	public void setRawReadings(Table table) {
		this.table = table;
	}

	public static RawData loadFromXML(File file) throws SAXException, JAXBException {
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema scenarioSchema = sf
				.newSchema(Thread.currentThread().getContextClassLoader().getResource("RawData.xsd"));
		JAXBContext jaxbContext = JAXBContext.newInstance(RawData.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		unmarshaller.setSchema(scenarioSchema);
		RawData data = (RawData) unmarshaller.unmarshal(file);
		return data;
	}
}
