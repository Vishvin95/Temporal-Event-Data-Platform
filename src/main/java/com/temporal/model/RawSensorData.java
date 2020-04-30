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

@XmlRootElement(name = "sensorData")
public class RawSensorData {
	private RawReadings rawReadings;

	@XmlElement(name="readings")
	public RawReadings getRawReadings() {
		return rawReadings;
	}

	public void setRawReadings(RawReadings rawReadings) {
		this.rawReadings = rawReadings;
	}

	public static RawSensorData loadFromXML(File file) throws SAXException, JAXBException {
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema scenarioSchema = sf
				.newSchema(Thread.currentThread().getContextClassLoader().getResource("RawSensorData.xsd"));
		JAXBContext jaxbContext = JAXBContext.newInstance(RawSensorData.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		unmarshaller.setSchema(scenarioSchema);
		RawSensorData data = (RawSensorData) unmarshaller.unmarshal(file);
		return data;
	}
}
