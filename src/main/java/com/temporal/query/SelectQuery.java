package com.temporal.query;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.temporal.persistence.Excecutor;
import com.temporal.persistence.GenericSqlBuilder;

public class SelectQuery {	
	
	/**
	 * @param query: Accepts query in form of string like:
	 * 				 1. select boilerCode from boiler;
	 * 				 2. select boiler.boilerCode, boiler.temperature, boiler.supId, supervisor.name 
	 * 				 		from boiler 
	 * 						inner join supervisor on boiler.supId = supervisor.supId;
	 * @return: Returns ResultSet object containing the results obtained from query execution
	 * @throws SQLException
	 */
	public static ResultSet select(String query) throws SQLException
	{
		GenericSqlBuilder showTables = new GenericSqlBuilder("show full tables where "
															+ "Table_type = 'BASE TABLE' and "
															+ "Tables_in_Factory in (select distinct domain_name from event_config)");
		Excecutor excecutor = new Excecutor();
		excecutor.addSqlQuery(showTables);
		List<ResultSet> results = excecutor.execute();		
		ResultSet tables = results.get(0);
				
		while(tables.next())
		{
			String table = tables.getString(1);
			query = query.replaceAll(table+"[\\.]", table+"_v.");
			query = query.replaceAll(table+"$", table+"_v");
			query = query.replaceAll(table+"[\\s]", table+"_v ");
		}		
		
		Excecutor selectExcecutor = new Excecutor();
		GenericSqlBuilder selectQuery = new GenericSqlBuilder(query);
		selectExcecutor.addSqlQuery(selectQuery);
		ResultSet selectResult = selectExcecutor.execute().get(0);
		return selectResult;
	}
}
