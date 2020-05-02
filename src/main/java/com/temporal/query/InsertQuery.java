package com.temporal.query;


import com.temporal.model.*;
import com.temporal.persistence.Excecutor;
import com.temporal.persistence.GenericSqlBuilder;
import javafx.util.Pair;

import java.sql.ResultSet;
import java.util.*;

public class InsertQuery extends CreateQuery {

    public static HashMap<String,HashMap<String,String>> columnResovler(ArrayList<Domain> domains)
    {
        HashMap<String,HashMap<String,String>> map=new HashMap<>();
        for(Domain domain:domains)
        {
            HashMap<String,String> temp=new HashMap<>();
            ArrayList<Event> events=domain.getEvents();
            for (Event event:events)
            {
                if(!map.containsKey(domain.getName()))
                {
                    map.put(domain.getName(),temp);
                }
                map.get(domain.getName()).put(event.getName(),event.getDataType());

            }

        }
        return map;
    }

    public static void insert(Table table, Scenario scenario)  {

        ArrayList<Domain> domains=scenario.getDomains();
        HashMap<String,String> dataType_Resolver=CreateQuery.DataTypeResolver();
        HashMap<String, Pair<String,String>> PrimaryKey_Resolver=CreateQuery.PrimaryKeyResolver(domains);
        HashMap<String,HashMap<String,String>> column_resolver=InsertQuery.columnResovler(domains);

        ArrayList<Column> columns=table.getRawReadings();
        for(Column column:columns)
        {
            if(PrimaryKey_Resolver.get(table.getName()).getKey().compareTo(column.getKey())==0)
            {
                Excecutor excecutor = new Excecutor();
                String sql="select * from "+table.getName()+" where "+column.getKey()+"=";
                if(PrimaryKey_Resolver.get(table.getName()).getValue().compareTo("string")==0)
                    sql=sql+'"'+column.getValue()+'"'+";";
                else
                    sql=sql+column.getValue()+";";
                excecutor.addSqlQuery(new GenericSqlBuilder(sql));
                List<ResultSet> resultSets = excecutor.execute();

                if(resultSets.isEmpty())
                {

                }
                else
                {

                }
            }
        }

    }
}
