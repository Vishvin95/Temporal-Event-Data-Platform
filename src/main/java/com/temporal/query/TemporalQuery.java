package com.temporal.query;

import com.temporal.model.Table;
import com.temporal.persistence.AbstractSqlBuilder;
import com.temporal.persistence.Excecutor;
import com.temporal.persistence.SelectBuilder;
import com.temporal.persistence.SubSelectBuilder;
import org.hibernate.sql.Select;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TemporalQuery {
    private static final String DOMAIN = "domain";
    private static final String EVENT = "event";
    private static final String DOMAIN_NAME = DOMAIN+"_name";
    private static final String EVENT_NAME = EVENT+"_name";
    private static final String DATA_TYPE = "datatype";
    private static final String DOMAIN_CONFIG_TABLE = DOMAIN+"_config";
    private static final String EVENT_CONFIG_TABLE = EVENT+"_config";
    private static final String TEMPORAL = "temporal";
    private static final String LAST = "last";
    private static final String FIRST = "first";
    private static final String VALID_FROM = "valid_from";
    private static final String VALID_TO = "valid_to";
    private static final String TRANSACTION_ENTER = "transaction_enter ";
    private static final String TRANSACTION_DELETE = "transaction_delete";
    private static final String VALUE = "value";
    private static final String ID = "id";
    private static HashMap<String,ArrayList<String>> tablePrimaryKey;
    private static HashMap<String,ArrayList<String[]>> tableTemporalAttribute;
    static {
        try {
            tablePrimaryKey = createTablePrimaryKey();
            tableTemporalAttribute = createTableTemporalAttribute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public static HashMap<String,ArrayList<String>> createTablePrimaryKey() throws SQLException {
        HashMap<String,ArrayList<String>> send = new HashMap<>();
        ResultSet resultSet= new Excecutor()
                .addSqlQuery(new SelectBuilder().from(DOMAIN_CONFIG_TABLE))
                .execute()
                .get(0);
        while (resultSet.next()){
            if(!send.containsKey(resultSet.getString(1)))
                send.put(resultSet.getString(1),new ArrayList<>());
            send.get(resultSet.getString(1)).add(resultSet.getString(2));
        }
        return send;
    }
    public static HashMap<String,ArrayList<String[]>> createTableTemporalAttribute() throws SQLException {
        HashMap<String,ArrayList<String[]>> send = new HashMap<>();
        ResultSet resultSet = new Excecutor()
                .addSqlQuery(new SelectBuilder()
                        .column(DOMAIN_NAME)
                        .column(EVENT_NAME)
                        .column(DATA_TYPE)
                        .from(EVENT_CONFIG_TABLE)
                        .where(TEMPORAL+" = 1"))
                .execute()
                .get(0);
        while (resultSet.next()){
            if(!send.containsKey(resultSet.getString(1)))
                send.put(resultSet.getString(1),new ArrayList<>());
            send.get(resultSet.getString(1)).add(new String[]{resultSet.getString(2),resultSet.getString(3)});
        }
        return send;
    }
    public static void getTables() throws SQLException {
        createTablePrimaryKey();
        createTableTemporalAttribute();

        System.out.println(tablePrimaryKey);
        System.out.println(tableTemporalAttribute);

//        createFirstView(tablePrimaryKey,tableTemporalAttribute).forEach((key,value) -> {
//            System.out.println(key+" "+value);
//        });
//        createLastView(tablePrimaryKey,tableTemporalAttribute).forEach((key,value)->{
//            System.out.println(key+" "+value);
//        });
        System.out.println(createFirstView("boiler","pressure"));
    }
    public static HashMap<String,String> createFirstView(HashMap<String,ArrayList<String>> tablePrimaryKey,HashMap<String,ArrayList<String[]>> tableTemporalAttribute){
        HashMap<String,String> send = new HashMap<>();
        tablePrimaryKey.forEach((table,primaryKey) -> {
            if(tableTemporalAttribute.containsKey(table)){
                 tableTemporalAttribute.get(table).forEach(temporalAtrribute -> {
                     send.put(table+"_"+temporalAtrribute[0]+"_first",createFirstView(table,temporalAtrribute[0]));
                 });
             }
        });
        return send;
    }
    public static HashMap<String,String> createLastView(HashMap<String,ArrayList<String>> tablePrimaryKey,HashMap<String,ArrayList<String[]>> tableTemporalAttribute){
        HashMap<String,String> send = new HashMap<>();
        tablePrimaryKey.forEach((table, primaryKey) -> {
            if (tableTemporalAttribute.containsKey(table)) {
                tableTemporalAttribute.get(table).forEach(temporalAtrribute -> {
                    send.put(table+"_"+temporalAtrribute[0]+"_last",createLastView(table,temporalAtrribute[0]));
                });
            }
        });
        return send;
    }
    public static String createFirstView(String table,String temporalAttribute){
        return createFirstView(new SelectBuilder().from(table+"_"+temporalAttribute),table).toString();
    }
    public static String createLastView(String table,String temporalAttribute){
        return createLastView(new SelectBuilder().from(table+"_"+temporalAttribute),table).toString();
    }
    public static SelectBuilder createFirstView(SelectBuilder selectBuilder,String table){
        SelectBuilder view = new SelectBuilder();
        SelectBuilder temp_view = new SelectBuilder();

        ArrayList<String> primaryKey = tablePrimaryKey.get(table);

        //Build select query for FIRST
        primaryKey.forEach(temp_view::column);
        temp_view.column("MIN("+VALID_FROM+") as "+VALID_FROM)
                .from(new SubSelectBuilder(selectBuilder,"T"));
        primaryKey.forEach(temp_view::groupBy);

        //Aliasing the queries
        String first_alias = "Q1";

        //Creating first
        view.column("T.id as "+ID)
                .from(new SubSelectBuilder(selectBuilder,"T"))
                .join(new SubSelectBuilder(temp_view,first_alias));

        primaryKey.forEach(s -> {
            view.where("T."+s+"="+first_alias+"."+s);
            view.column("T."+s);
        });

        view.column("T."+VALUE)
                .column("T."+VALID_FROM)
                .column("T."+VALID_TO)
                .column("T."+TRANSACTION_ENTER)
                .column("T."+TRANSACTION_DELETE);

        view.where("T."+VALID_FROM+"="+first_alias+"."+VALID_FROM);
        return view;
    }
    public static SelectBuilder createLastView(SelectBuilder selectBuilder,String table){
        SelectBuilder view = new SelectBuilder();
        SelectBuilder temp_view = new SelectBuilder();

        ArrayList<String> primaryKey = tablePrimaryKey.get(table);

        //Build select query for FIRST
        primaryKey.forEach(temp_view::column);
        temp_view.column("MIN(T."+VALID_FROM+") as "+VALID_FROM)
                .from(new SubSelectBuilder(selectBuilder,"T"));
        primaryKey.forEach(temp_view::groupBy);

        //Aliasing the queries
        String first_alias = "Q1";

        //Creating first
        view.column("T.id as "+ID)
                .from(new SubSelectBuilder(selectBuilder,"T"))
                .join(new SubSelectBuilder(temp_view,first_alias));

        primaryKey.forEach(s -> {
            view.where("T."+s+"="+first_alias+"."+s);
            view.column("T."+s);
        });

        view.column("T."+VALUE)
                .column("T."+VALID_FROM)
                .column("T."+VALID_TO)
                .column("T."+TRANSACTION_ENTER)
                .column("T."+TRANSACTION_DELETE);

        view.where("T."+VALID_FROM+"="+first_alias+"."+VALID_FROM);
        return view;
    }
    public static SelectBuilder createEvolutionView(SelectBuilder selectBuilder,String table){
        StringBuilder chain_primary_key  = new StringBuilder();
        ArrayList<String> primaryKey = tablePrimaryKey.get(table);
        chain_primary_key.append(primaryKey.get(0));
        for(int i = 1,h=primaryKey.size();i<h;i++) chain_primary_key.append(",").append(primaryKey.get(i));
        SelectBuilder temp_evolution = new SelectBuilder().column("T.*")
                .column("(CASE WHEN LAG(T."+VALUE+",1) over ( partition by "+chain_primary_key+" order by "+VALID_FROM+") = T."+VALUE+" THEN 1 ELSE 0 END) as result")
                .from(new SubSelectBuilder(selectBuilder,"T"));
        SelectBuilder evolution = new SelectBuilder()
                .from(new SubSelectBuilder(temp_evolution,"T"))
                .where("T.result = 0");
        evolution.column("T."+ID);
        primaryKey.forEach(s -> evolution.column("T."+s));
        evolution.column("T."+VALUE);
        evolution.column("T."+VALID_FROM);
        evolution.column("T."+VALID_TO);
        evolution.column("T."+TRANSACTION_ENTER);
        evolution.column("T."+TRANSACTION_DELETE);
        return evolution;
    }
    public static SelectBuilder createPreviousView(SelectBuilder selectBuilder,String table,String temporalAttribute){
        tableTemporalAttribute.forEach((s, strings) -> {
            strings.forEach(strings1 -> {
                System.out.println(Arrays.toString(strings1));
            });
        });
        return null;
    }


}
