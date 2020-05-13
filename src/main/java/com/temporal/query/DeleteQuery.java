package com.temporal.query;

import com.temporal.persistence.Excecutor;
import com.temporal.persistence.GenericSqlBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class DeleteQuery extends InsertQuery{

    public static boolean isExist(String table)throws SQLException{
        String sql="select * from event_config where domain_name="+'"'+table+'"'+";";
        Excecutor excecutor=new Excecutor();
        excecutor.addSqlQuery(new GenericSqlBuilder(sql));
        ArrayList<ResultSet> rs = (ArrayList<ResultSet>) excecutor.execute();
        if(rs.isEmpty())
            return false;
        return true;
    }

    public static ArrayList<String> getPk(String table,String condition) throws SQLException{
        ArrayList<String> send=new ArrayList<>();
        String pk=getPk(table)[0];
        String sql="select "+pk+" from "+table+" where "+condition;
        Excecutor excecutor=new Excecutor();
        excecutor.addSqlQuery(new GenericSqlBuilder(sql));
        ArrayList<ResultSet> rs = (ArrayList<ResultSet>) excecutor.execute();
        for(ResultSet r:rs)
        {
            while(r.next())
            {
                send.add(r.getString(1));
            }
        }
        return send;
    }

    public static String[] getPk(String table) throws SQLException{
        String []send=new String[2];
        String sql="select primaryKey,datatype from domain_config where domain_name="+'"'+table+'"'+";";
        Excecutor excecutor=new Excecutor();
        excecutor.addSqlQuery(new GenericSqlBuilder(sql));
        ArrayList<ResultSet> rs = (ArrayList<ResultSet>) excecutor.execute();
        while(rs.get(0).next())
        {
            send[0]=rs.get(0).getString(1);
            send[1]=rs.get(0).getString(2);
        }
        return send;
    }

    public static  ArrayList<String> getfk(String table) throws SQLException{
        ArrayList<String> send=new ArrayList<>();
        String sql="select ForeignKey from fk_config where domain_name="+'"'+table+'"'+";";
        Excecutor excecutor=new Excecutor();
        excecutor.addSqlQuery(new GenericSqlBuilder(sql));
        ArrayList<ResultSet> rs = (ArrayList<ResultSet>) excecutor.execute();
        for(ResultSet r:rs)
        {
            while(r.next())
            {
                send.add(r.getString(1));
            }
        }
        return send;
    }

    public static String valueMaker(String value, String datatype)
    {
        if(datatype.charAt(0)=='V')
            return '"'+value+'"';
        return value;

    }

    public static ArrayList<String> getMoe(String table) throws SQLException{
        ArrayList<String> send=new ArrayList<>();
        String sql="select event_name from event_config where temporal=1 and domain_name="+'"'+table+'"'+";";
        Excecutor excecutor=new Excecutor();
        excecutor.addSqlQuery(new GenericSqlBuilder(sql));
        ArrayList<ResultSet> rs = (ArrayList<ResultSet>) excecutor.execute();
        for(ResultSet r:rs)
        {
            while(r.next())
            {
                send.add(r.getString(1));
            }
        }
        return send;
    }

    public static void delete(String query) throws SQLException{
        String TableName=query.trim().split("from|where")[1].trim();
        String condition=query.trim().split("from|where",0)[2].trim();
        ArrayList<String> foreignKey=getfk(TableName);
        if(isExist(TableName)&&!foreignKey.isEmpty())
        {
                ArrayList<String> primaryKey=getPk(TableName,condition);
                System.out.println(primaryKey);
                String historyCondition="";
                String pk[]= getPk(TableName);
                String pkName=pk[0];
                String pkdatatype=pk[1];
                int size=primaryKey.size();

                for(int i=0;i<size-1;i++)
                {
                    historyCondition=historyCondition+pkName+"="+valueMaker(primaryKey.get(i),pkdatatype)+" or ";
                }
                if(size-1>=0)
                {
                    historyCondition=historyCondition+pkName+"="+valueMaker(primaryKey.get(size-1),pkdatatype);
                }

                String deleteQuery="";
                for(String fk:foreignKey)
                {
                    deleteQuery=deleteQuery+"update "+TableName+"_"+fk+" set transaction_delete=now() where transaction_delete is null and ("+historyCondition+");";
                }

                ArrayList<String> events=getMoe(TableName);
                for(String event:events)
                {
                    deleteQuery=deleteQuery+"update "+TableName+"_"+event+" set transaction_delete=now() where transaction_delete is null and ("+historyCondition+");";
                }
                boolean success=true;
            try
            {
                Excecutor excecutor=new Excecutor();
                excecutor.addSqlQuery(new GenericSqlBuilder(query));
                excecutor.execute();
            }
            catch (SQLException e)
            {
                success=false;
                System.out.println(e);
            }
            if(success==true&&!historyCondition.isEmpty())
            {
                queryExecution(deleteQuery);
            }

        }
        else
            {
                Excecutor excecutor=new Excecutor();
                excecutor.addSqlQuery(new GenericSqlBuilder(query));
                excecutor.execute();
            }
    }

}
