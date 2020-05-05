package com.temporal.query;


import com.temporal.model.*;
import com.temporal.persistence.Excecutor;
import com.temporal.persistence.GenericSqlBuilder;
import javafx.util.Pair;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.util.*;

public class InsertQuery  {

    public static HashMap<String,String> keyResolver() throws SQLException {
        HashMap<String,String> map=new HashMap<>();
        Excecutor excecutor = new Excecutor();
        excecutor.addSqlQuery(new GenericSqlBuilder("select * from domain_config"));
        List<ResultSet> resultSets = excecutor.execute();

        for(ResultSet rs:resultSets)
        {
            while (rs.next())
            {
                map.put(rs.getString(2),rs.getString(1));
            }
        }

        return map;
    }

    public static HashMap<String,Pair<String,String>> temporalResolver() throws SQLException {
        HashMap<String,Pair<String,String>> map=new HashMap<>();
        Excecutor excecutor = new Excecutor();
        excecutor.addSqlQuery(new GenericSqlBuilder("select * from event_config"));
        List<ResultSet> resultSets = excecutor.execute();

        for(ResultSet rs:resultSets)
        {
            while (rs.next())
            {
                Pair<String,String> pair =new Pair<>(rs.getString(3),rs.getString(4));
                map.put(rs.getString(2),pair);
            }
        }

        return map;
    }

    public static String valueMaker(String name,String value,HashMap<String,Pair<String,String>> temporal_resolver)
    {
        if(temporal_resolver.get(name).getKey().charAt(0)=='V')
            return '"'+value+'"';
        return value;

    }

    public static void queryExecution(String sql) throws SQLException
    {
        if(sql.compareTo("")!=0)
        {
            String queries[]=sql.split(";");
            Excecutor excecutor=new Excecutor();
            for(String query:queries)
            {
                excecutor.addSqlQuery(new GenericSqlBuilder(query+";"));
            }
            excecutor.execute();
        }

    }

    public static String getValidFromTimestamp(Date date)
    {
        return date == null ? '"'+"now()"+'"' : '"'+new Timestamp(date.getTime()).toString()+'"';
    }

    public static String getValidToTimestamp(Date date)
    {
        return date == null ? '"'+"9999-12-31 23:59:59"+'"' : '"'+new Timestamp(date.getTime()).toString()+'"';
    }

    public static void insert(Table table) throws SQLException {

        String HistoryInsert="";
        String TemporalInsert="";
        String pk="";
        String pkValue="";

        HashMap<String,String> key_resolver=keyResolver();
        HashMap<String,Pair<String,String>> temporal_resolver=temporalResolver();

        ArrayList<Column> columns=table.getRawReadings();

        for(Column column:columns)
        {
            if(key_resolver.get(column.getKey())!=null&&key_resolver.get(column.getKey()).compareTo(table.getName())==0)
            {
                 pk=column.getKey();
                 pkValue=column.getValue();
                 break;
            }
        }

        for (Column column:columns)
        {
            if(Integer.parseInt(temporal_resolver.get(column.getKey()).getValue())==1)
            {
                TemporalInsert=TemporalInsert+"insert into "+table.getName()+"_"+column.getKey()+"("
                        		+pk+",value,valid_from,valid_to,transaction_enter) values("+valueMaker(pk,pkValue,temporal_resolver)+","+
                valueMaker(column.getKey(),column.getValue(),temporal_resolver)+","+getValidFromTimestamp(column.getValidFrom())+
                        ","+getValidToTimestamp(column.getValidTo())+",now()"+");";
            }
        }

        String MainInsert="insert into "+table.getName()+"("+pk;
        String MainInsertHelper=" values("+valueMaker(pk,pkValue,temporal_resolver);
        for(Column column:columns)
        {
            if(Integer.parseInt(temporal_resolver.get(column.getKey()).getValue())==0&&column.getKey().compareTo(pk)!=0)
            {
                MainInsert=MainInsert+","+column.getKey();
                MainInsertHelper=MainInsertHelper+","+valueMaker(column.getKey(),column.getValue(),temporal_resolver);
                if(key_resolver.containsKey(column.getKey()))
                {
                    HistoryInsert=HistoryInsert+"insert into "+table.getName()+"_"+column.getKey()+"("+pk+","+column.getKey()+
                            ",valid_from,valid_to,transaction_enter) "+"values("+
                            valueMaker(pk,pkValue,temporal_resolver)+","+valueMaker(column.getKey(),column.getValue(),temporal_resolver)+","+
                            getValidFromTimestamp(column.getValidFrom())+","+getValidToTimestamp(column.getValidTo())+",now()"+
                            ");";
                }
            }
        }
        MainInsert=MainInsert+")"+MainInsertHelper+");";
        System.out.println(MainInsert);
        System.out.println(TemporalInsert);
        boolean success=true;
        try
        {
            queryExecution(MainInsert);
        }
       catch (SQLIntegrityConstraintViolationException e)
       {
           success=false;
           System.out.println(e);
       }
        if(success==true)
        {
            queryExecution(TemporalInsert);
            queryExecution(HistoryInsert);
        }

    }
}
