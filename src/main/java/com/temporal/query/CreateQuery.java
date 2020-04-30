package com.temporal.query;

import com.temporal.model.Domain;
import com.temporal.model.Event;
import com.temporal.model.Scenario;
import com.temporal.model.Relationship;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CreateQuery {

    /*
    RETURNS THE HASHMAP OF 1:N RELATIONSHIPS
     */
    public static HashMap<String,ArrayList<String>> get_Relationships_Nx1(ArrayList<Relationship> relationships){
        HashMap<String,ArrayList<String>> Nx1_relationships=new HashMap<>();
        for(Relationship relationship:relationships)
        {
            if(relationship.getType().compareTo("1n")==0)
            {
                if(Nx1_relationships.containsKey(relationship.getTo()))
                {
                    Nx1_relationships.get(relationship.getTo()).add(relationship.getFrom());
                }
                else
                {
                    ArrayList<String> temp=new ArrayList<>();
                    temp.add(relationship.getFrom());
                    Nx1_relationships.put(relationship.getTo(),temp);
                }
            }
        }
        return Nx1_relationships;
    }

    /*HashMap<String,ArrayList<String>> Relationships_NxN
     RETURNS THE HASHMAP OF 1:1 RELATIONSHIPS
     */
    public static HashMap<String,ArrayList<String>> get_Relationships_1x1(ArrayList<Relationship> relationships){
        HashMap<String,ArrayList<String>> relationships_1x1=new HashMap<>();
        for(Relationship relationship:relationships)
        {
            if(relationship.getType().compareTo("11")==0)
            {

                if(relationships_1x1.containsKey(relationship.getFrom()))
                {
                    relationships_1x1.get(relationship.getFrom()).add(relationship.getTo());
                }
                else
                {
                    ArrayList<String> temp=new ArrayList<>();
                    temp.add(relationship.getTo());
                    relationships_1x1.put(relationship.getFrom(),temp);
                }
            }
        }
        return relationships_1x1;
    }

    /*
     RETURNS THE HASHMAP OF N:N RELATIONSHIPS
     */
    public static HashMap<String,ArrayList<String>> get_Relationships_NxN(ArrayList<Relationship> relationships,HashMap<String,HashMap<String,String>> relationship_Names){
        HashMap<String,ArrayList<String>> relationships_NxN=new HashMap<>();
        for(Relationship relationship:relationships)
        {
            if(relationship.getType().compareTo("nn")==0)
            {
                if(relationships_NxN.containsKey(relationship.getFrom()))
                {
                    relationships_NxN.get(relationship.getFrom()).add(relationship.getTo());
                }
                else
                {
                    ArrayList<String> temp=new ArrayList<>();
                    temp.add(relationship.getTo());
                    relationships_NxN.put(relationship.getFrom(),temp);
                }
                HashMap<String,String> temp =new HashMap<>();
                temp.put(relationship.getTo(),relationship.getName());
                relationship_Names.put(relationship.getFrom(),temp);
            }
        }
        return relationships_NxN;
    }

    public static HashMap<String,Domain> getDomainByName(ArrayList<Domain> domains)
    {
        HashMap<String,Domain> map=new HashMap<>();
        for(Domain domain:domains)
        {
            map.put(domain.getName(),domain);
        }
        return map;
    }

       /*
         RETURNS HASHMAP FOR DATATYPE RESOLVING
       */
    public static HashMap<String,String> DataTypeResolver()
    {
        HashMap<String,String> map=new HashMap<>();
        map.put("string","VARCHAR(50)");
        map.put("integer","INT");
        map.put("long","BIGINT");
        map.put("date","DATE");
        map.put("datetime","DATETIME");
        map.put("decimal","DECIMAL(16,4)");
        map.put("boolean","BOOLEAN");
        return map;
    }

       /*
         RETURNS HASHMAP FOR TEMPORAL RESOLVING
       */
    public static HashMap<String,Boolean> TemporalResolver(ArrayList<Domain> domains)
    {
        HashMap<String,Boolean> map=new HashMap<>();
        for (Domain domain:domains)
        {
            if(domain.isTemporal())
            {
                map.put(domain.getName(),true);
            }
            else
            {
                map.put(domain.getName(),false);
            }
        }
        return map;
    }

    public static HashMap<String,Pair<String,String>> PrimaryKeyResolver(ArrayList<Domain> domains)
    {
        HashMap<String, Pair<String,String>> map=new HashMap<>();
        for (Domain domain:domains)
        {
            if(domain.getPrimaryKey().compareTo("")==0)
            {
                map.put(domain.getName(),new Pair<>("id","int"));
            }
            else
            {
                ArrayList<Event> events=domain.getEvents();
                for(Event event:events)
                {
                    if(domain.getPrimaryKey().compareTo(event.getName())==0)
                    {
                        map.put(domain.getName(),new Pair<>(domain.getPrimaryKey(),event.getDataType()));
                    }
                }
            }
        }
        return map;
    }

    /*
         CREATES THE QUERY FOR THE WHOLR SCENARIO
     */
    public String CreateScenario(Scenario scenario){

        ArrayList<Domain> domains=scenario.getDomains();

        HashMap<String,String> dataType_Resolver=CreateQuery.DataTypeResolver();
        HashMap<String,Boolean> temporal_Resolver=CreateQuery.TemporalResolver(domains);

        String query="";
        String helperQuery="";
        String temporalQuery="";
        String database_name=scenario.getName();

        query=query+"create database "+database_name+";"+"use "+database_name+";";

        ArrayList<Relationship> relationships=scenario.getRelationships();

        HashMap<String,HashMap<String,String>> Relationship_Names=new HashMap<>();
        HashMap<String,ArrayList<String>> Relationships_NxN= CreateQuery.get_Relationships_NxN(relationships,Relationship_Names);
        HashMap<String,ArrayList<String>> Relationships_Nx1= CreateQuery.get_Relationships_Nx1(relationships);
        HashMap<String,ArrayList<String>> Relationships_1x1= CreateQuery.get_Relationships_1x1(relationships);
        HashMap<String,Pair<String,String>> PrimaryKey_Resolver=CreateQuery.PrimaryKeyResolver(domains);

        for (Domain domain:domains)
        {
            String temp1="";
            String temp2="";

            temp1=temp1+"create table "+ domain.getName()+"(";
            temp2=temp2+"create table "+ domain.getName()+"_hist(";

            ArrayList<Event> events= domain.getEvents();
            for(Event event:events)
            {
                temp1=temp1+event.getName()+" "+dataType_Resolver.get(event.getDataType())+" ";
                temp2=temp2+event.getName()+" "+dataType_Resolver.get(event.getDataType())+" ";
                if (event.isNotNull())
                {
                    temp1=temp1+"NOT NULL ";
                }
                if (event.isUnique())
                {
                    temp1=temp1+"UNIQUE";
                }
                temp1=temp1+",";
                temp2=temp2+",";
            }
            temp1=temp1+"PRIMARY KEY("+PrimaryKey_Resolver.get(domain.getName()).getKey()+")";
            temp2=temp2+domain.getName()+"_"+PrimaryKey_Resolver.get(domain.getName()).getKey()+" "+
                    dataType_Resolver.get(PrimaryKey_Resolver.get(domain.getName()).getValue());
            if(domain.isTemporal())
            {
                temp1=temp1+",valid_from DATETIME,valid_to DATETIME,trans_enter DATETIME,trans_delete DATETIME";
                temporalQuery=temporalQuery+temp2+",valid_from DATETIME,valid_to DATETIME,trans_enter DATETIME,trans_delete DATETIME,"+
                        "id int not null unique auto_increment,PRIMARY KEY(id));";
            }
            query=query+temp1+");";
        }
        query=query+temporalQuery;
        for(Domain domain:domains)
        {
            if(Relationships_1x1.containsKey(domain.getName()))
            {
                ArrayList<String> foreignKeys=Relationships_1x1.get(domain.getName());
                for(String foreignKey:foreignKeys)
                {
                    helperQuery=helperQuery+"alter table "+domain.getName()+" add "+foreignKey+"_"+PrimaryKey_Resolver.get(foreignKey).getKey()+
                            " "+dataType_Resolver.get(PrimaryKey_Resolver.get(foreignKey).getValue())+";";
                    helperQuery=helperQuery+"alter table "+domain.getName()+" add constraint foreign key("+
                            foreignKey+"_"+PrimaryKey_Resolver.get(foreignKey).getKey()+") references "+foreignKey+"("+PrimaryKey_Resolver.get(foreignKey).getKey()+");";
                    if(domain.isTemporal())
                    {
                        helperQuery=helperQuery+"alter table "+domain.getName()+"_hist add "+foreignKey+"_"+PrimaryKey_Resolver.get(foreignKey).getKey()+
                                " "+dataType_Resolver.get(PrimaryKey_Resolver.get(foreignKey).getValue())+";";
                    }
                }
            }

            if(Relationships_Nx1.containsKey(domain.getName()))
            {
                ArrayList<String> foreignKeys=Relationships_Nx1.get(domain.getName());
                for(String foreignKey:foreignKeys)
                {
                    helperQuery=helperQuery+"alter table "+domain.getName()+" add "+foreignKey+"_"+PrimaryKey_Resolver.get(foreignKey).getKey()+
                            " "+dataType_Resolver.get(PrimaryKey_Resolver.get(foreignKey).getValue())+";";
                    helperQuery=helperQuery+"alter table "+domain.getName()+" add constraint foreign key("+
                            foreignKey+"_"+PrimaryKey_Resolver.get(foreignKey).getKey()+") references "+foreignKey+"("+PrimaryKey_Resolver.get(foreignKey).getKey()+");";
                    if(domain.isTemporal())
                    {
                        helperQuery=helperQuery+"alter table "+domain.getName()+"_hist add "+foreignKey+"_"+PrimaryKey_Resolver.get(foreignKey).getKey()+
                                " "+dataType_Resolver.get(PrimaryKey_Resolver.get(foreignKey).getValue())+";";
                    }
                }
            }
        }
        query=query+helperQuery;
        Iterator<Map.Entry<String, HashMap<String,String>>> parent = Relationship_Names.entrySet().iterator();

        while (parent.hasNext())
        {
            Map.Entry<String, HashMap<String, String>> parentPair = parent.next();
            Iterator<Map.Entry<String, String>> child = (parentPair.getValue()).entrySet().iterator();
            while (child.hasNext())
            {
                Map.Entry childPair = child.next();
                query=query+"create table "+childPair.getValue()+"("+

                        parentPair.getKey()+"_"+PrimaryKey_Resolver.get(parentPair.getKey()).getKey()+" "+
                        dataType_Resolver.get(PrimaryKey_Resolver.get(parentPair.getKey()).getValue())+","+

                        childPair.getKey()+"_"+PrimaryKey_Resolver.get(childPair.getKey()).getKey()+" "+
                        dataType_Resolver.get(PrimaryKey_Resolver.get(childPair.getKey()).getValue())+","+

                        "foreign key ("+parentPair.getKey()+"_"+PrimaryKey_Resolver.get(parentPair.getKey()).getKey()+") references "
                        +parentPair.getKey()+"("+PrimaryKey_Resolver.get(parentPair.getKey()).getKey()+"),"+

                        "foreign key ("+childPair.getKey()+"_"+PrimaryKey_Resolver.get(childPair.getKey()).getKey()+") references "
                        +childPair.getKey()+"("+PrimaryKey_Resolver.get(childPair.getKey()).getKey()+"),"+

                        "PRIMARY KEY ("+parentPair.getKey()+"_"+PrimaryKey_Resolver.get(parentPair.getKey()).getKey()+","+
                        childPair.getKey()+"_"+PrimaryKey_Resolver.get(childPair.getKey()).getKey()+")"+");";
                //child.remove(); // avoids a ConcurrentModificationException
            }
        }

        return query;
    }
}
