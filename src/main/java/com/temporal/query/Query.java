package com.temporal.query;

import com.temporal.model.Scenario;
import com.temporal.model.Relationship;

import java.util.ArrayList;
import java.util.HashMap;

public class Query {

    public static HashMap<String,ArrayList<String>> get_Nx1_relationships(ArrayList<Relationship> relationships){
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

    public String CreateScenario(Scenario scenario){
        String query="";
        String database_name=scenario.getName();
        //
        ArrayList<Relationship> relationships=scenario.getRelationships();
        HashMap<String,ArrayList<String>> Nx1_Relationships= Query.get_Nx1_relationships(relationships);

        for(String s:Nx1_Relationships.keySet())
            System.out.print(s);

        return query;
    }
}
