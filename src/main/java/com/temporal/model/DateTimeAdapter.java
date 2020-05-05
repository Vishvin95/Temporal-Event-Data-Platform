package com.temporal.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateTimeAdapter extends XmlAdapter<String, Date>{
	private final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");	
	
	@Override
	public Date unmarshal(String date) throws Exception {
		Calendar calendar = DatatypeConverter.parseDateTime(date);
		Date d = new Date(calendar.getTimeInMillis());		
		return d;
	}

	@Override
	public String marshal(Date date) throws Exception {		
		return dateFormat.format(date);
	}
	
}
