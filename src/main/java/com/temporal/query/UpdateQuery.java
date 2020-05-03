package com.temporal.query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.temporal.persistence.GlobalConnection;

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
	 */
	public static boolean update(String query)
	{	
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
				
		try
		{
			Connection con = GlobalConnection.getConnection();
			Statement statement = con.createStatement();			
			
			StringBuilder soeQuery = new StringBuilder("update "+table+" set ");
			
			// Getting all events of this domain
			ResultSet events = statement.executeQuery("select * from event_config where tableName = "+table);  
			while(events.next())
			{
				String event = events.getString("event");
				
				// If temporal, then form the queries for corresponding temporal tables
				if(events.getBoolean("temporal"))
				{
					String moeTable = table + "_" + event;							
					int lastRecordId = 0;
					ResultSet lastRecord = statement.executeQuery("select id from "+moeTable + " where " + table+ "_"+ keyCondition +" order by valid_from desc limit 1");
					while(lastRecord.next())
						lastRecordId = lastRecord.getInt(1);
					
					// Case where some column was inserted as null initially, but later updated
					if(lastRecordId!=0)
					{
						query = "update " + moeTable + " set valid_to = now() where " + table+ "_"+ keyCondition +" and id = " + lastRecordId;   								
						statement.addBatch(query);		
					}	
					
					query = "insert into " + moeTable + "(value,"+table+"_" + keyColumn+",valid_from,valid_to,transaction_enter) values(" +
								updateValueMap.get(event) + ","+keyValue+",now(),\"9999-12-31 23:59:59\",current_timestamp())";
					statement.addBatch(query);					
					updateValueMap.remove(event);					
				}
				else
				{
					soeQuery.append(event + " = " + updateValueMap.get(event) + ",");
				}				
			}
			
			// Completing SOE Query
			soeQuery.deleteCharAt(soeQuery.length()-1);
			soeQuery.append(" where " + keyCondition);
			statement.addBatch(soeQuery.toString());
			
			// Completing MOE Queries for remaining foreign key attributes in this table			
			for(Map.Entry<String, String> foreignKeyEvent : updateValueMap.entrySet())
			{ 
				String moeTable = table + "_" + foreignKeyEvent.getKey();
				int lastRecordId = 0;
				ResultSet lastRecord = statement.executeQuery("select id from "+moeTable + " where " + table+ "_"+ keyCondition +" order by valid_from desc limit 1");
				while(lastRecord.next())
					lastRecordId = lastRecord.getInt(1);
				
				if(lastRecordId!=0)
				{
					query = "update " + moeTable + " set valid_to = now() where " + table+ "_"+ keyCondition +" and id = " + lastRecordId;   								
					statement.addBatch(query);		
				}	
				
				query = "insert into " + moeTable + "(value,"+table+"_" + keyColumn+",valid_from,valid_to,transaction_enter) values(" +
							foreignKeyEvent.getValue() + ","+keyValue+",now(),\"9999-12-31 23:59:59\",current_timestamp())";
				statement.addBatch(query);									
			}
			
			// Batch execute 
			int count[] = statement.executeBatch();
			for(int i=0;i<count.length;i++)
			{
				if(count[i]<0)
				{
					con.rollback();
					con.close();
					return false;
				}
			}
			con.close();
		}catch (SQLException e) {
			e.printStackTrace();
		}		
		return true;
	}
}
