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

public class Query {

    public static HashMap<String,ArrayList<String>> get_Relationships_Nx1(ArrayList<Relationship> relationships){
        HashMap<String,ArrayList<String>> Nx1_relationships=new HashMap<>();
        for(Relationship relationship:relationships)
        {
            if(relationship.getType().compareTo("n1")==0)
            {
                if(Nx1_relationships.containsKey(relationship.getFrom()))
                {
                    Nx1_relationships.get(relationship.getFrom()).add(relationship.getTo());
                }
                else
                {
                    ArrayList<String> temp=new ArrayList<>();
                    temp.add(relationship.getTo());
                    Nx1_relationships.put(relationship.getFrom(),temp);
                }
            }
            else if(relationship.getType().compareTo("1n")==0)
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

    public String CreateScenario(Scenario scenario){

        HashMap<String,String> dataType_Resolver=new HashMap<>();
        dataType_Resolver.put("string","VARCHAR(50)");
        dataType_Resolver.put("integer","INT");
        dataType_Resolver.put("long","BIGINT");
        dataType_Resolver.put("date","DATE");
        dataType_Resolver.put("datetime","DATETIME");
        dataType_Resolver.put("decimal","DECIMAL(16,4)");
        dataType_Resolver.put("boolean","BOOLEAN");

        String query="";
        String helperQuery="";
        String temporalQuery="create table temporal_info(moe_name VARCHAR(50) ,base_store VARCHAR(50),id INT NOT NULL AUTO_INCEMENT,PRIMARY KEY (id));";
        String database_name=scenario.getName();
        query=query+"create database "+database_name+";"+"use "+database_name+";";

        ArrayList<Relationship> relationships=scenario.getRelationships();
        HashMap<String,HashMap<String,String>> Relationship_Names=new HashMap<>();

        HashMap<String,ArrayList<String>> Relationships_Nx1=Query.get_Relationships_Nx1(relationships);
        HashMap<String,ArrayList<String>> Relationships_1x1=Query.get_Relationships_1x1(relationships);
        HashMap<String,ArrayList<String>> Relationships_NxN=Query.get_Relationships_NxN(relationships,Relationship_Names);


//        Iterator<Map.Entry<String, ArrayList<String>>> itr = Relationships_NxN.entrySet().iterator();
//        while(itr.hasNext())
//        {
//            Map.Entry<String,ArrayList<String>> entry=itr.next();
//            System.out.println(entry.getKey());
//            ArrayList<String> temp=entry.getValue();
//            for(String s:temp)
//            {
//                System.out.print(s);
//            }
//            System.out.println("------------");
//        }

        ArrayList<Domain> domains=scenario.getDomains();
        for(Domain domain:domains)
        {
            ArrayList<Event> events= domain.getEvents();
            int events_len=events.size();
            query=query+"create table "+ domain.getname()+"("+"id INT NOT NULL AUTO_INCREMENT,PRIMARY KEY (id),";
            for(int j=0;j<events_len-1;j++)
            {
                query = query+events.get(j).getName()+ " "+ dataType_Resolver.get(events.get(j).getDataType())+ " "+ ",";
                if(events.get(j).getType().compareTo("MOE")==0)
                {
                   helperQuery=helperQuery+"create table "+ events.get(j).getName()+ "("+"value "+dataType_Resolver.get(events.get(j).getDataType())+","+
                           "valid_from DATETIME,valid_to DATETIME,trans_enter DATETIME,trans_delete DATETIME,"+
                           "id int AUTO_INCREMENT,PRIMARY KEY(id),"+
                           domain.getname()+"_id INT,"+"foreign key ("+domain.getname()+"_id) references "+
                           domain.getname()+"(id)"+")"+ ";";
                   temporalQuery=temporalQuery+"INSERT INTO temporal_info(moe_name,base_store) VALUES("+
                           '"'+events.get(j).getName()+'"'+","+'"'+domain.getname()+'"'+");";

                }
            }
            query=query+events.get(events_len-1).getName()+ " "+ dataType_Resolver.get(events.get(events_len-1).getDataType())+ " ";

            if(events.get(events_len-1).getType().compareTo("MOE")==0)
            {
                helperQuery=helperQuery+"create table "+ events.get(events_len-1).getName()+ "("+"value "+dataType_Resolver.get(events.get(events_len-1).getDataType())+","+
                        "valid_from DATETIME,valid_to DATETIME,trans_enter DATETIME,trans_delete DATETIME,"+
                        "id int AUTO_INCREMENT,PRIMARY KEY(id),"+
                        domain.getname()+"_id INT,"+"foreign key ("+domain.getname()+"_id) references "+
                        domain.getname()+"(id)"+")"+ ";";

                temporalQuery=temporalQuery+"INSERT INTO temporal_info(moe_name,base_store) VALUES("+
                        events.get(events_len-1).getName()+","+domain.getname()+");";
            }

            if(Relationships_1x1.containsKey(domain.getname()))
            {
                query=query+",";
                ArrayList<String> foreignKeys=Relationships_1x1.get(domain.getname());
                int foreignKeys_len=foreignKeys.size();
                for(int k=0;k<foreignKeys_len-1;k++)
                {
                    query=query+foreignKeys.get(k)+"_id INT,foreign key ("+foreignKeys.get(k)+"_id) references "+foreignKeys.get(k)+"(id),";
                }
                query=query+foreignKeys.get(foreignKeys_len-1)+"_id INT,foreign key ("+foreignKeys.get(foreignKeys_len-1)+"_id) references "+foreignKeys.get(foreignKeys_len-1)+"(id)";
            }

            if(Relationships_Nx1.containsKey(domain.getname()))
            {
                query=query+",";
                ArrayList<String> foreignKeys=Relationships_Nx1.get(domain.getname());
                int foreignKeys_len=foreignKeys.size();
                for(int k=0;k<foreignKeys_len-1;k++)
                {
                    query=query+foreignKeys.get(k)+"_id INT,foreign key ("+foreignKeys.get(k)+"_id) references "+foreignKeys.get(k)+"(id),";
                }
                query=query+foreignKeys.get(foreignKeys_len-1)+"_id INT,foreign key ("+foreignKeys.get(foreignKeys_len-1)+"_id) references "+foreignKeys.get(foreignKeys_len-1)+"(id)";
            }
            query = query+");";
        }

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

        System.out.println(temporalQuery);
        System.out.println(helperQuery);
        // hashcodes,1xN:Nx1,atrributes

        return query+helperQuery+temporalQuery;
    }
}
