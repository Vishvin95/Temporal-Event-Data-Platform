package com.temporal.query;

import com.temporal.model.Domain;
import com.temporal.model.Event;
import com.temporal.model.Scenario;
import com.temporal.model.Relationship;

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

    /*

     */
    public String CreateScenario(Scenario scenario){
        /*
          HASHMAP FOR DATATYPE RESOLVING
        */
        HashMap<String,String> dataType_Resolver=new HashMap<>();
        dataType_Resolver.put("string","VARCHAR(50)");
        dataType_Resolver.put("integer","INT");
        dataType_Resolver.put("long","BIGINT");
        dataType_Resolver.put("date","DATE");
        dataType_Resolver.put("datetime","DATETIME");
        dataType_Resolver.put("decimal","DECIMAL(16,4)");
        dataType_Resolver.put("boolean","BOOLEAN");

        ArrayList<Domain> domains=scenario.getDomains();


        HashMap<String,Boolean> temporal_Resolver=new HashMap<>();
        for (Domain domain:domains)
        {
            if(domain.isTemporal())
            {
                temporal_Resolver.put(domain.getName(),true);
            }
            else
            {
                temporal_Resolver.put(domain.getName(),false);
            }
        }

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



        for (Domain domain:domains)
        {
            String temp1="";
            String temp2="";
            String temp="";
            temp1=temp1+"create table "+ domain.getName()+"("+"id INT NOT NULL AUTO_INCREMENT,PRIMARY KEY (id)";
            temp2=temp2+"create table "+ domain.getName()+"_hist("+domain.getName()+"_id INT";
            ArrayList<Event> events= domain.getEvents();
            int events_len=events.size();
            for(Event event:events)
            {
                temp=temp+","+event.getName()+" "+dataType_Resolver.get(event.getDataType())+" ";
                if (event.isNotNull())
                {
                    temp=temp+"NOT NULL ";
                }
                if (event.isUnique())
                {
                    temp=temp+"UNIQUE";
                }
            }

            if(domain.isTemporal())
            {
                temporalQuery=temporalQuery+temp2+temp+",valid_from DATETIME,valid_to DATETIME,trans_enter DATETIME,trans_delete DATETIME,"+
                        "PRIMARY KEY("+domain.getName()+"_id,valid_from,trans_enter));";
            }
            query=query+temp1+temp+");";
        }
        query=query+temporalQuery;
        for(Domain domain:domains)
        {
            if(Relationships_1x1.containsKey(domain.getName()))
            {
                ArrayList<String> foreignKeys=Relationships_1x1.get(domain.getName());
                for(String foreignKey:foreignKeys)
                {
                    helperQuery=helperQuery+"alter table "+domain.getName()+" add "+foreignKey+"_id int;";
                    helperQuery=helperQuery+"alter table "+domain.getName()+" add constraint foreign key("+foreignKey+"_id) references "+foreignKey+"(id);";
                    if(domain.isTemporal())
                    {
                        helperQuery=helperQuery+"alter table "+domain.getName()+"_hist add "+foreignKey+"_id int;";
                        helperQuery=helperQuery+"alter table "+domain.getName()+"_hist add constraint foreign key("+foreignKey+"_id) references "+foreignKey+"(id);";
                    }
                }
            }

            if(Relationships_Nx1.containsKey(domain.getName()))
            {
                ArrayList<String> foreignKeys=Relationships_Nx1.get(domain.getName());
                for(String foreignKey:foreignKeys)
                {
                    helperQuery=helperQuery+"alter table "+domain.getName()+" add "+foreignKey+"_id int;";
                    helperQuery=helperQuery+"alter table "+domain.getName()+" add constraint foreign key("+foreignKey+"_id) references "+foreignKey+"(id);";
                    if(domain.isTemporal())
                    {
                        helperQuery=helperQuery+"alter table "+domain.getName()+"_hist add "+foreignKey+"_id int;";
                        helperQuery=helperQuery+"alter table "+domain.getName()+"_hist add constraint foreign key("+foreignKey+"_id) references "+foreignKey+"(id);";
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
                        parentPair.getKey()+"_id INT,"+childPair.getKey()+"_id INT,"+
                        "foreign key ("+parentPair.getKey()+"_id) references "+parentPair.getKey()+"(id),"+
                        "foreign key ("+childPair.getKey()+"_id) references "+childPair.getKey()+"(id),"+
                        "PRIMARY KEY ("+parentPair.getKey()+"_id,"+childPair.getKey()+"_id)"+");";
                //child.remove(); // avoids a ConcurrentModificationException
            }
        }

        return query;
    }
}
