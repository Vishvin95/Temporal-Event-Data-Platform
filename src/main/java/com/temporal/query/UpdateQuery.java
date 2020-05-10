package com.temporal.query;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;


import java.util.ArrayList;
import java.sql.SQLSyntaxErrorException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.temporal.model.Column;
import com.temporal.model.Table;
import com.temporal.persistence.Excecutor;
import com.temporal.persistence.GenericSqlBuilder;
import javafx.util.Pair;

public class UpdateQuery extends InsertQuery {

	public static String GetResultSet(String sql) throws SQLException{
		if(sql.compareTo("")!=0)
		{
			String queries[]=sql.split(";");
			Excecutor excecutor=new Excecutor();
			for(String query:queries)
			{
				excecutor.addSqlQuery(new GenericSqlBuilder(query+";"));
			}
			ArrayList<ResultSet> rs = (ArrayList<ResultSet>) excecutor.execute();
			return rs.get(0).toString();
		}
		return null;
	}

	public static String GetValue(String table,String pk,String pkvalue,String fk) throws SQLException{
		String sql="select "+fk+" from "+table+" where "+pk+"="+pkvalue+";";
		Excecutor excecutor=new Excecutor();
		excecutor.addSqlQuery(new GenericSqlBuilder(sql));
		ArrayList<ResultSet> rs = (ArrayList<ResultSet>) excecutor.execute();
		return rs.get(0).toString();
	}

	public static void update(Table table) throws SQLException  {

		ArrayList<Column> columns=table.getRawReadings();
		HashMap<String,String> key_resolver=keyResolver();
		HashMap<String, Pair<String,String>> temporal_resolver=temporalResolver();

		String HistoryUpdate="";
		String TemporalUpdate="";
		String pk="";
		String pkValue="";
		String temp="update "+table.getName()+" set ";
		StringBuilder MainUpdate=new StringBuilder(temp);
		StringBuilder MainUpdateHelper=new StringBuilder();



		for(Column column:columns)
		{
			if(key_resolver.get(column.getKey())!=null&&key_resolver.get(column.getKey()).compareTo(table.getName())==0)
			{
				pk=column.getKey();
				pkValue=column.getValue();
				break;
			}
		}

		String Check="select * from "+table.getName()+" where "+pk+" ="+valueMaker(pk,pkValue,temporal_resolver)+";";

		if(!GetResultSet(Check).isEmpty())
		{
			for (Column column:columns)
			{
				if(Integer.parseInt(temporal_resolver.get(column.getKey()).getValue())==1)
				{
					TemporalUpdate=TemporalUpdate+"insert into "+table.getName()+"_"+column.getKey()+"("
							+pk+",value,valid_from,valid_to,transaction_enter) values("+valueMaker(pk,pkValue,temporal_resolver)+","+
							valueMaker(column.getKey(),column.getValue(),temporal_resolver)+","+getValidFromTimestamp(column.getValidFrom())+
							","+getValidToTimestamp(column.getValidTo())+",now()"+");";
				}
			}
			for(Column column:columns)
			{
				if(Integer.parseInt(temporal_resolver.get(column.getKey()).getValue())==0&&column.getKey().compareTo(pk)!=0)
				{
					MainUpdateHelper=MainUpdateHelper.append(column.getKey()+"="+valueMaker(column.getKey(),column.getValue(),temporal_resolver)+",");
					if(key_resolver.containsKey(column.getKey()))
					{
                        if(GetValue(table.getName(),pk,valueMaker(pk,pkValue,temporal_resolver),column.getKey()).compareTo(column.getValue())!=0)
						{
							HistoryUpdate=HistoryUpdate+"insert into "+table.getName()+"_"+column.getKey()+"("+pk+","+column.getKey()+
									",valid_from,valid_to,transaction_enter) "+"values("+
									valueMaker(pk,pkValue,temporal_resolver)+","+valueMaker(column.getKey(),column.getValue(),temporal_resolver)+","+
									getValidFromTimestamp(column.getValidFrom())+","+getValidToTimestamp(column.getValidTo())+",now()"+
									");";
						}

					}
				}
			}
            MainUpdate=MainUpdate.append(MainUpdateHelper);
			MainUpdate=MainUpdate.deleteCharAt(MainUpdate.length()-1);
			temp = " where "+pk+"="+valueMaker(pk,pkValue,temporal_resolver)+";";
			MainUpdate.append(temp);

			System.out.println(MainUpdate);
			System.out.println(TemporalUpdate);
			System.out.println(HistoryUpdate);

			boolean success=true;
			try
			{
				queryExecution(HistoryUpdate);
			}
			catch (SQLException e)
			{
				success=false;
				System.out.println(e);
			}
			if(success==true)
			{
				if(!MainUpdateHelper.toString().isEmpty())
					queryExecution(MainUpdate.toString());
				queryExecution(TemporalUpdate);
			}
		}

        else
		{
			System.out.println("the primary key does not exists");
		}


	}

	/**
	 * Initial version ( Supports only queries with condition as primaryKey = <value> )
	 * --------------------------------------------------------------------------------
	 * This method updates in general way, considering no overlap. Use overloaded
	 * method to send validity of the events.
	 * Usage:
 	 * update <table> set <column>=<value>, ... where <primaryKey> = <value> 
 	 *  
	 * @param query : SQL query
	 * @return status : success
	 * @throws SQLSyntaxErrorException 
	 */
	public static boolean update(String query) throws SQLSyntaxErrorException
	{	
		try
		{				
			Excecutor excecutor = new Excecutor();
			Excecutor batchExcecutor = new Excecutor();
					
			String q[] = query.trim().split("update |set |where ");		
			String table = q[1].trim();
			String updateColumns = q[2].trim();
			String keyCondition = q[3].trim();
					
			// Loading values to be updated
			HashMap<String,String> updateValueMap = new HashMap<>();
			StringTokenizer st = new StringTokenizer(updateColumns, ",");
			while(st.hasMoreTokens())
			{
				String pair = st.nextToken();
				String pairSplit[] = pair.trim().split("=");
				updateValueMap.put(pairSplit[0].trim(), pairSplit[1].trim());
			}
			
			// Getting key column and value
			String keyColumnValue[] = keyCondition.trim().split("=");
			String keyColumn = keyColumnValue[0].trim();
			String keyValue = keyColumnValue[1].trim();
											
			StringBuilder soeQuery = new StringBuilder("update "+table+" set ");
			
			// Getting all events of this domain
			HashMap<String,Boolean> eventResolver = new HashMap<String, Boolean>();
			boolean soeQueryPresent = false;
			
			excecutor.addSqlQuery(new GenericSqlBuilder("select * from event_config where domain_name = '"+table+"' and event_name != '"+keyColumn+"'"));
			ResultSet events = excecutor.execute().get(0);
			while(events.next())
				eventResolver.put(events.getString("event_name"), events.getBoolean("temporal"));
			
			
			for(Map.Entry<String, Boolean> curEvent: eventResolver.entrySet())
			{
				String event = curEvent.getKey();
				
				if(updateValueMap.containsKey(event))
				{
					// If temporal, then form the queries for corresponding temporal tables
					if(curEvent.getValue())
					{
						String moeTable = table + "_" + event;							
						int lastRecordId = 0;
						excecutor.clear();
						excecutor.addSqlQuery(new GenericSqlBuilder("select id from "+moeTable + " where " + keyCondition +" order by valid_from desc limit 1"));
						ResultSet lastRecord = excecutor.execute().get(0);
						while(lastRecord.next())
							lastRecordId = lastRecord.getInt(1);
						
						// If an old entry exists
						if(lastRecordId!=0)
						{
							query = "update " + moeTable + " set valid_to = now() where " + keyCondition +" and id = " + lastRecordId;   
							batchExcecutor.addSqlQuery(new GenericSqlBuilder(query));							
							
							query = "insert into " + moeTable + "(value," + keyColumn+",valid_from,valid_to,transaction_enter) values(" +
									updateValueMap.get(event) + ","+keyValue+",now(),\"9999-12-31 23:59:59\",current_timestamp())";
							batchExcecutor.addSqlQuery(new GenericSqlBuilder(query));
						} // Two cases: either this value was inserted NULL initially or their doesn't have this entry in base table as well	
						else
						{
							excecutor.clear();
							excecutor.addSqlQuery(new GenericSqlBuilder("select * from "+ table +" where "+keyCondition));
							ResultSet record = excecutor.execute().get(0);
							if(record.next())							
							{
								query = "insert into " + moeTable + "(value," + keyColumn+",valid_from,valid_to,transaction_enter) values(" +
										updateValueMap.get(event) + ","+keyValue+",now(),\"9999-12-31 23:59:59\",current_timestamp())";
								batchExcecutor.addSqlQuery(new GenericSqlBuilder(query));
							}
						}
											
						updateValueMap.remove(event);					
					}
					else
					{
						soeQuery.append(event + " = " + updateValueMap.get(event) + ",");
						soeQueryPresent = true;
					}		
				}
			}					
			
			// Completing MOE Queries for remaining foreign key attributes in this table			
			for(Map.Entry<String, String> foreignKeyEvent : updateValueMap.entrySet())
			{ 
				String fkTableField[] = foreignKeyEvent.getKey().trim().split("_");
				String moeTable = table + "_" + fkTableField[1];
				int lastRecordId = 0;
				
				excecutor.clear();
				excecutor.addSqlQuery(new GenericSqlBuilder("select id from "+moeTable + " where " + keyCondition +" order by valid_from desc limit 1"));
				ResultSet lastRecord = excecutor.execute().get(0);
				while(lastRecord.next())
					lastRecordId = lastRecord.getInt(1);
				
				if(lastRecordId!=0)
				{
					query = "update " + moeTable + " set valid_to = now() where " + keyCondition +" and id = " + lastRecordId;   								
					batchExcecutor.addSqlQuery(new GenericSqlBuilder(query));
					
					query = "insert into " + moeTable + "("+fkTableField[1]+","+ keyColumn+",valid_from,valid_to,transaction_enter) values(" +
							foreignKeyEvent.getValue() + ","+keyValue+",now(),\"9999-12-31 23:59:59\",current_timestamp())";
					batchExcecutor.addSqlQuery(new GenericSqlBuilder(query));
				}	
				else
				{
					excecutor.clear();
					excecutor.addSqlQuery(new GenericSqlBuilder("select * from "+ table +" where "+keyCondition));
					ResultSet record = excecutor.execute().get(0);
					if(record.next())							
					{
						query = "insert into " + moeTable + "("+fkTableField[1]+","+ keyColumn+",valid_from,valid_to,transaction_enter) values(" +
								foreignKeyEvent.getValue() + ","+keyValue+",now(),\"9999-12-31 23:59:59\",current_timestamp())";
						batchExcecutor.addSqlQuery(new GenericSqlBuilder(query));
					}
				}				
				
				soeQuery.append(foreignKeyEvent.getKey() + " = " + foreignKeyEvent.getValue() + ",");
				soeQueryPresent = true;
			}
			
			if(soeQueryPresent)
			{
				soeQuery.deleteCharAt(soeQuery.length()-1);
				soeQuery.append(" where " + keyCondition);
				batchExcecutor.addSqlQuery(new GenericSqlBuilder(soeQuery.toString()));
			}
			
			// Batch execute 
			batchExcecutor.execute();			
			return true;
		}catch(IndexOutOfBoundsException e) {
			throw new SQLSyntaxErrorException(query);			
		}		
		catch(SQLException e) {
			e.printStackTrace();
			return false;
		}				
	}
}
