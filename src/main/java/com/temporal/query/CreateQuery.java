package com.temporal.query;

import com.temporal.model.Domain;
import com.temporal.model.Event;
import com.temporal.model.Scenario;
import com.temporal.persistence.builder.SelectBuilder;
import com.temporal.persistence.builder.ViewBuilder;
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
         CREATES THE QUERY FOR THE WHOLE SCENARIO
     */
    public String CreateScenario(Scenario scenario){

        ArrayList<Domain> domains=scenario.getDomains();

        String query="";
        String temporalQuery="";
        String EventConfigQuery="create table event_config(domain_name varchar(50),event_name varchar(50),datatype varchar(50)," +
                "temporal boolean,overlap boolean,primary key(domain_name,event_name));";
        String DomainConfigQuery="create table domain_config(domain_name varchar(50),primaryKey varchar(50),datatype varchar(50));";
        String ForeignKayConfig="create table fk_config(domain_name varchar(50),ForeignKey varchar(50));";

        ArrayList<Relationship> relationships=scenario.getRelationships();

        HashMap<String,String> dataType_Resolver=CreateQuery.DataTypeResolver();
        HashMap<String,HashMap<String,String>> Relationship_Names=new HashMap<>();
        HashMap<String,ArrayList<String>> Relationships_NxN= CreateQuery.get_Relationships_NxN(relationships,Relationship_Names);
        HashMap<String,ArrayList<String>> Relationships_Nx1= CreateQuery.get_Relationships_Nx1(relationships);
        HashMap<String,ArrayList<String>> Relationships_1x1= CreateQuery.get_Relationships_1x1(relationships);
        HashMap<String,Pair<String,String>> PrimaryKey_Resolver=CreateQuery.PrimaryKeyResolver(domains);
        HashMap<String,SelectBuilder> views = new HashMap<String, SelectBuilder>();
        
        for (Domain domain:domains)
        {
        	// Preparing select for the current domain
        	SelectBuilder select = new SelectBuilder();
        	select.from(domain.getName());        	
        	
            query=query+"create table "+ domain.getName()+"(";
            ArrayList<Event> events= domain.getEvents();
            for(Event event:events)
            {
                EventConfigQuery=EventConfigQuery+"insert into event_config values("+'"'+domain.getName()+'"'+","+'"'+event.getName()+'"'+","+
                        '"'+dataType_Resolver.get(event.getDataType())+'"'+",";


                if(event.getMoe()!=null)
                {
                    temporalQuery=temporalQuery+"create table "+domain.getName()+"_"+event.getName()+"(id int not null unique auto_increment,value "+
                                  dataType_Resolver.get(event.getDataType())+","+ PrimaryKey_Resolver.get(domain.getName()).getKey()+
                                  " "+dataType_Resolver.get(PrimaryKey_Resolver.get(domain.getName()).getValue())+
                                  ",valid_from datetime,valid_to datetime,transaction_enter datetime,transaction_delete datetime);";

                    if(event.getMoe().isOverlap())
                        EventConfigQuery=EventConfigQuery+"true,true);";
                    else
                        EventConfigQuery=EventConfigQuery+"true,false);";
                    
                    select.column(domain.getName()+"_"+event.getName() + ".value as " + event.getName());                    
                    select.leftJoin(domain.getName()+"_"+event.getName() + " on " + domain.getName()+"."+PrimaryKey_Resolver.get(domain.getName()).getKey() 
                    				+ " = " + domain.getName()+"_"+event.getName() + "." + PrimaryKey_Resolver.get(domain.getName()).getKey());
                    select.where("(" + domain.getName()+"_"+event.getName()+".valid_from <= now() and now() < "
                    				+ domain.getName()+"_"+event.getName()+".valid_to or " 
                    		        + domain.getName()+"_"+event.getName()+"." + PrimaryKey_Resolver.get(domain.getName()).getKey() +" is null)");                                        
                }
                else
                {
                    EventConfigQuery=EventConfigQuery+"false,false);";
                    query=query+event.getName()+" "+dataType_Resolver.get(event.getDataType())+" ";
                    if (event.isNotNull())
                    {
                        query=query+"NOT NULL ";
                    }
                    if (event.isUnique())
                    {
                        query=query+"UNIQUE";
                    }

                    query=query+",";
                    
                    select.column(domain.getName()+"."+event.getName() + " as " + event.getName());
                }


            }
            query=query+"PRIMARY KEY("+PrimaryKey_Resolver.get(domain.getName()).getKey()+")";
            query=query+");";

            DomainConfigQuery=DomainConfigQuery+"insert into domain_config values("+'"'+domain.getName()+'"'+
                    ","+'"'+PrimaryKey_Resolver.get(domain.getName()).getKey()+'"'+","+'"'+dataType_Resolver.get(PrimaryKey_Resolver.get(domain.getName()).getValue())+'"'+");";
            views.put(domain.getName(), select);
        }

        for(Domain domain:domains)
        {
            if(Relationships_1x1.containsKey(domain.getName()))
            {
                ArrayList<String> foreignKeys=Relationships_1x1.get(domain.getName());
                for(String foreignKey:foreignKeys)
                {
                    query=query+"alter table "+domain.getName()+" add "+PrimaryKey_Resolver.get(foreignKey).getKey()+
                            " "+dataType_Resolver.get(PrimaryKey_Resolver.get(foreignKey).getValue())+";";
                    query=query+"alter table "+domain.getName()+" add constraint foreign key("+
                            PrimaryKey_Resolver.get(foreignKey).getKey()+") references "+foreignKey+"("+PrimaryKey_Resolver.get(foreignKey).getKey()+");";

                    query=query+"create table "+domain.getName()+"_"+PrimaryKey_Resolver.get(foreignKey).getKey()+"("+"id int not null unique auto_increment,"+
                                PrimaryKey_Resolver.get(foreignKey).getKey()+" "+dataType_Resolver.get(PrimaryKey_Resolver.get(foreignKey).getValue())+
                                ","+PrimaryKey_Resolver.get(domain.getName()).getKey()+" "+dataType_Resolver.get(PrimaryKey_Resolver.get(domain.getName()).getValue())+
                                 ",valid_from datetime,valid_to datetime,transaction_enter datetime,transaction_delete datetime);";

                    ForeignKayConfig=ForeignKayConfig+"insert into fk_config values("+'"'+domain.getName()+'"'+","+'"'+PrimaryKey_Resolver.get(foreignKey).getKey()+'"'+");";

                    views.get(domain.getName())
                    		.column(domain.getName()+"_"+PrimaryKey_Resolver.get(foreignKey).getKey() + "." + PrimaryKey_Resolver.get(foreignKey).getKey() + " as "+ PrimaryKey_Resolver.get(foreignKey).getKey())
                    		.where("("+domain.getName()+"_"+PrimaryKey_Resolver.get(foreignKey).getKey()+".valid_from <= now() and now() < "
                    				+domain.getName()+"_"+PrimaryKey_Resolver.get(foreignKey).getKey()+".valid_to or " + domain.getName()+"_"+PrimaryKey_Resolver.get(foreignKey).getKey()
                    				+"."+PrimaryKey_Resolver.get(domain.getName()).getKey() + " is null)")                    		
                    		.leftJoin(domain.getName()+"_"+PrimaryKey_Resolver.get(foreignKey).getKey()+" on " + domain.getName()+"."+ 
                    				PrimaryKey_Resolver.get(domain.getName()).getKey() + 
                    				" = " + domain.getName()+"_"+PrimaryKey_Resolver.get(foreignKey).getKey()+"." + PrimaryKey_Resolver.get(domain.getName()).getKey());                    		
                    				
                }
            }

            if(Relationships_Nx1.containsKey(domain.getName()))
            {
                ArrayList<String> foreignKeys=Relationships_Nx1.get(domain.getName());
                for(String foreignKey:foreignKeys)
                {
                    query=query+"alter table "+domain.getName()+" add "+PrimaryKey_Resolver.get(foreignKey).getKey()+
                            " "+dataType_Resolver.get(PrimaryKey_Resolver.get(foreignKey).getValue())+";";
                    query=query+"alter table "+domain.getName()+" add constraint foreign key("+
                            PrimaryKey_Resolver.get(foreignKey).getKey()+") references "+foreignKey+"("+PrimaryKey_Resolver.get(foreignKey).getKey()+");";

                    query=query+"create table "+domain.getName()+"_"+PrimaryKey_Resolver.get(foreignKey).getKey()+"("+"id int not null unique auto_increment,"+

                            PrimaryKey_Resolver.get(foreignKey).getKey()+" "+dataType_Resolver.get(PrimaryKey_Resolver.get(foreignKey).getValue())+
                            ","+PrimaryKey_Resolver.get(domain.getName()).getKey()+" "+dataType_Resolver.get(PrimaryKey_Resolver.get(domain.getName()).getValue())+
                            ",valid_from datetime,valid_to datetime,transaction_enter datetime,transaction_delete datetime);";

                    ForeignKayConfig=ForeignKayConfig+"insert into fk_config values("+'"'+domain.getName()+'"'+","+'"'+PrimaryKey_Resolver.get(foreignKey).getKey()+'"'+");";

                    views.get(domain.getName())
                    .column(domain.getName()+"_"+PrimaryKey_Resolver.get(foreignKey).getKey() + "." + PrimaryKey_Resolver.get(foreignKey).getKey() + " as "+ PrimaryKey_Resolver.get(foreignKey).getKey())
                    .where("("+domain.getName()+"_"+PrimaryKey_Resolver.get(foreignKey).getKey()+".valid_from <= now() and now() < "
            				+domain.getName()+"_"+PrimaryKey_Resolver.get(foreignKey).getKey()+".valid_to or " + domain.getName()+"_"+PrimaryKey_Resolver.get(foreignKey).getKey()
            				+"."+PrimaryKey_Resolver.get(domain.getName()).getKey() + " is null)")   
            		.leftJoin(domain.getName()+"_"+PrimaryKey_Resolver.get(foreignKey).getKey()+" on " + domain.getName()+"."+ 
            				PrimaryKey_Resolver.get(domain.getName()).getKey() +
            				" = " + domain.getName()+"_"+PrimaryKey_Resolver.get(foreignKey).getKey()+"." + PrimaryKey_Resolver.get(domain.getName()).getKey()); 

                }
            }
        }
        Iterator<Map.Entry<String, HashMap<String,String>>> parent = Relationship_Names.entrySet().iterator();
        
        // NN-Relationship generator
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

                        "PRIMARY KEY ("+parentPair.getKey()+"_"+PrimaryKey_Resolver.get(parentPair.getKey()).getKey()+","+
                        childPair.getKey()+"_"+PrimaryKey_Resolver.get(childPair.getKey()).getKey()+")"+

                        ",valid_from datetime,valid_to datetime,transaction_enter datetime,transaction_delete datetime);";
            }
        }

        // Adding view creation queries to final query set
        StringBuilder viewQuery = new StringBuilder();
        for(Map.Entry<String, SelectBuilder> select: views.entrySet())
        {
        	ViewBuilder view = new ViewBuilder(select.getKey()+"_v", select.getValue());
        	viewQuery.append(view.toString()+";");
        }        
        
        return query+temporalQuery+EventConfigQuery+DomainConfigQuery+viewQuery+ForeignKayConfig;
    }
}
