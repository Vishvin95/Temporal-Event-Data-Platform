package com.temporal.model;

import javax.xml.bind.annotation.XmlAttribute;

public class MOE {
	private boolean overlap;
	
	public MOE() {
		
	}
	
	public MOE(boolean overlap) {
		this.overlap = overlap;
	}

	@XmlAttribute(name = "overlap")
	public boolean isOverlap() {
		return overlap;
	}

	public void setOverlap(boolean overlap) {
		this.overlap = overlap;
	}
}
