package com.temporal.query;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.temporal.persistence.Excecutor;
import com.temporal.persistence.GenericSqlBuilder;

public class UpdateQuery {

	public static void update() {
//        ArrayList<Column> columns=table.getRawReadings();
//        for(Column column:columns)
//        {
//            if(PrimaryKey_Resolver.get(table.getName()).getKey().compareTo(column.getKey())==0)
//            {
//                Excecutor excecutor = new Excecutor();
//                String sql="select * from "+table.getName()+" where "+column.getKey()+"=";
//                if(PrimaryKey_Resolver.get(table.getName()).getValue().compareTo("string")==0)
//                    sql=sql+'"'+column.getValue()+'"'+";";
//                else
//                    sql=sql+column.getValue()+";";
//                excecutor.addSqlQuery(new GenericSqlBuilder(sql));
//                List<ResultSet> resultSets = excecutor.execute();
//
//                if(resultSets.isEmpty())
//                {
//
//                }
//                else
//                {
//
//                }
//            }
//        }
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
