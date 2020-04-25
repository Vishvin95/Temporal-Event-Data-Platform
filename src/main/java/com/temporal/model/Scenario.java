package com.temporal.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

@XmlRootElement
public class Scenario {

	private ArrayList<Domain> domains;
	private ArrayList<Relationship> relationships;
	private String name;

	public Scenario() {

	}

	private Scenario(ScenarioBuilder scenarioBuilder) {
		this.name = scenarioBuilder.name;
		this.domains = scenarioBuilder.domains;
		this.relationships = scenarioBuilder.relationships;
	}

	@XmlElementWrapper(name = "domains")
	@XmlElement(name = "domain")
	public ArrayList<Domain> getDomains() {
		return domains;
	}

	public void setDomains(ArrayList<Domain> domains) {
		this.domains = domains;
	}

	@XmlElementWrapper(name = "relationships")
	@XmlElement(name = "relationship")
	public ArrayList<Relationship> getRelationships() {
		return relationships;
	}

	public void setRelationships(ArrayList<Relationship> relationships) {
		this.relationships = relationships;
	}

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Method that takes XML file as input and returns Scenario object
	 * 
	 * @param file
	 * @return Scenario
	 * @throws SAXException
	 * @throws JAXBException
	 */
	public static Scenario loadFromXML(File file) throws SAXException, JAXBException, InvalidScenarioException {
// 		Setting Scenario XML schema 		
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		// --Vvin 
		// Schema scenarioSchema = sf.newSchema(new File("ScenarioSchema.xsd"));

		// Tarang : Alternate way of getting xsd
		Schema scenarioSchema = sf
				.newSchema(Thread.currentThread().getContextClassLoader().getResource("ScenarioSchema.xsd"));

// 		Unmarshal from XML to Object		
		JAXBContext jaxbContext = JAXBContext.newInstance(Scenario.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		unmarshaller.setSchema(scenarioSchema);
		Scenario scenario = (Scenario) unmarshaller.unmarshal(file);

		// ++Vvin
		try {
			Scenario.validateScenario(scenario);
		}catch (DuplicateRelationshipException e) {
			e.printStackTrace();
			throw new InvalidScenarioException("Invalid Scenario: " + scenario.getName());
		}
		return scenario;
	}

	// ++Vvin
	private static void validateScenario(Scenario scenario) throws DuplicateRelationshipException{
		HashSet<Relationship> relationshipSet = new HashSet<Relationship>();
		
		for(Relationship relationship: scenario.getRelationships())
		{	
			if(!relationshipSet.add(relationship))
			{
				throw new DuplicateRelationshipException("Duplicate relationship: " + relationship);
			}
		}
	}

	/**
	 * Debug method used to check, if scenario is loaded correctly
	 */
	public void printScenario() {
		System.out.println("Database:" + this.getName());

		System.out.println();
		System.out.println("Tables:");
		for (Domain domain : this.getDomains()) {
			System.out.println("  " + domain.getName());
			System.out.println("  Temporal: " + domain.isTemporal());
			ArrayList<Event> el = domain.getEvents();

			for (Event event : el) {
				System.out.println(event);
			}
		}

		System.out.println();
		System.out.println("Relationships:");
		for (Relationship relationship : this.getRelationships()) {
			System.out.println(relationship.getName());
			System.out.println("  " + relationship);
		}
	}

	public static class ScenarioBuilder {
		private ArrayList<Domain> domains;
		private ArrayList<Relationship> relationships;
		private final String name;

		public ScenarioBuilder(String name) {
			this.name = name;
			this.domains = new ArrayList<Domain>();
			this.relationships = new ArrayList<Relationship>();
		}

		public ScenarioBuilder addDomain(Domain domain) {
			domains.add(domain);
			return this;
		}

		public ScenarioBuilder addRelationship(Relationship relationship) {
			relationships.add(relationship);
			return this;
		}

		public Scenario build() throws InvalidScenarioException {
			Scenario scenario = new Scenario(this);
			
			// ++Vvin
			try {
				Scenario.validateScenario(scenario);
			}catch (DuplicateRelationshipException e) {
				e.printStackTrace();
				throw new InvalidScenarioException("Invalid scenario: " + scenario.getName());
			}
			return scenario;
		}
	}
}
