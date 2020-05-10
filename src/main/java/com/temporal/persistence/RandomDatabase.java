package com.temporal.persistence;
/* 
    Apricot Management Suite
    Copyright (C) 2020 Tarang Parikh
    
    Email : tp0265@gmail.com
    Project Home : https://github.com/tarangparikh/apricot
    
    Original Author : @author Tarang Parikh <tp0265@gmail.com>
    
*/

import com.temporal.model.Column;
import com.temporal.model.Table;
import com.temporal.query.InsertQuery;
import com.temporal.query.UpdateQuery;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class RandomDatabase {
    private static final int PRIMARY_KEY_BOILER = 1;
    private static final int TEMPORAL_VARIATION_BOILER = 1;
    private static final int PRIMARY_KEY_PUMP = 1;
    private static final int TEMPORAL_VARIATION_PUMP = 1;
    private static final int PRIMARY_KEY_SUPERVISOR = 1;
    private static final int TEMPORAL_VARIATION_SUPERVISOR = 1;
    private static Float randomFloat(){
        return ThreadLocalRandom.current().nextFloat() * 100 + 1;
    }
    private static Integer randomInteger(){
        return ThreadLocalRandom.current().nextInt(5,500);
    }
    private static String randomString(int len){
        int range = 26;
        StringBuilder sb  = new StringBuilder();
        while (len-->0){
            sb.append((char)(ThreadLocalRandom.current().nextInt(26)+'a'));
        }
        return sb.toString();
    }
    private static String randomString(ArrayList<String> array){
        return array.get(ThreadLocalRandom.current().nextInt(array.size()));
    }
    private static String[][] random_valid_to_from_for_per_day(int number_of_days) {
        String format = "yyyy-MM-dd\'T\'HH:mm:ss";
        String startDate = "2020-01-01T00:00:00";
        String endDate = "2020-01-02T00:00:00";
        DateTimeFormatter formatter = DateTimeFormat.forPattern(format);
        long start = formatter.parseDateTime(startDate).getMillis();
        long end = formatter.parseDateTime(endDate).getMillis();
        long range = end - start;

        String[][] send = new String[number_of_days][2];
        int i = 0;
        while (number_of_days-->0){
            long orgin = ThreadLocalRandom.current().nextLong(start,start+range);
            long bound = ThreadLocalRandom.current().nextLong(start,start+range);
            if(orgin>bound){
                long temp = orgin;
                orgin = bound;
                bound = temp;
            }else if(orgin == bound){
                bound++;
            }
            send[i] = new String[2];
            send[i][0] = formatter.print(new DateTime(orgin));
            send[i++][1] = formatter.print(new DateTime(bound));

            //Update range
            start+=range;
        }
        return send;
    }
    private static Table[] random_pump(int number_of_days, ArrayList<Column> def_column) throws ParseException {
        String[][][] validToFrom = new String[2][number_of_days][2];
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss");
        for(int i = 0;i<2;i++) validToFrom[i] = random_valid_to_from_for_per_day(number_of_days);
        Table[] tables = new Table[number_of_days];
        int i = 0;
        while (number_of_days-->0){
            Table table = new Table();
            table.setName("pump");
            ArrayList<Column> list = new ArrayList<>();
            for(Column c : def_column){
                Column add = new Column();
                add.setKey(c.getKey());
                add.setValue(c.getValue());
                add.setValidTo(c.getValidTo());
                add.setValidFrom(c.getValidFrom());
                list.add(c);
            }
            Column[] columns = new Column[2];
            columns[0] = new Column();
            columns[1] = new Column();
            columns[0].setKey("inflow");
            columns[0].setValue(Float.toString(randomFloat()));
            columns[0].setValidFrom(dateFormat.parse(validToFrom[0][i][0]));
            columns[0].setValidTo(dateFormat.parse(validToFrom[0][i][1]));
            columns[1].setKey("outflow");
            columns[1].setValue(Float.toString(randomFloat()));
            columns[1].setValidFrom(dateFormat.parse(validToFrom[1][i][0]));
            columns[1].setValidTo(dateFormat.parse(validToFrom[1][i][1]));

            list.addAll(Arrays.asList(columns));
            table.setRawReadings(list);
            tables[i] = table;
            i++;
        }
        return tables;
    }
    private static Table[] random_supervisor(int number_of_days,ArrayList<Column> def_column) throws ParseException {
        String[][][] validToFrom = new String[1][number_of_days][2];
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss");
        for(int i = 0;i<1;i++) validToFrom[i] = random_valid_to_from_for_per_day(number_of_days);
        Table[] tables = new Table[number_of_days];
        int i = 0;
        while (number_of_days-->0){
            Table table = new Table();
            table.setName("supervisor");
            ArrayList<Column> list = new ArrayList<>();
            for(Column c : def_column){
                Column add = new Column();
                add.setKey(c.getKey());
                add.setValue(c.getValue());
                add.setValidTo(c.getValidTo());
                add.setValidFrom(c.getValidFrom());
                list.add(c);
            }
            Column[] columns = new Column[1];
            columns[0] = new Column();
            columns[0].setKey("salary");
            columns[0].setValue(Float.toString(randomInteger()));
            columns[0].setValidFrom(dateFormat.parse(validToFrom[0][i][0]));
            columns[0].setValidTo(dateFormat.parse(validToFrom[0][i][1]));


            list.addAll(Arrays.asList(columns));
            table.setRawReadings(list);
            tables[i] = table;
            i++;
        }
        return tables;
    }
    private static Table[] random_boiler(int number_of_days,ArrayList<Column> def_column) throws ParseException {
        String[][][] validToFrom = new String[2][number_of_days][2];
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss");
        for(int i = 0;i<2;i++) validToFrom[i] = random_valid_to_from_for_per_day(number_of_days);
        Table[] tables = new Table[number_of_days];
        int i = 0;
        while (number_of_days-->0){
            Table table = new Table();
            table.setName("boiler");
            ArrayList<Column> list = new ArrayList<>();
            for(Column c : def_column){
                Column add = new Column();
                add.setKey(c.getKey());
                add.setValue(c.getValue());
                add.setValidTo(c.getValidTo());
                add.setValidFrom(c.getValidFrom());
                list.add(c);
            }
            Column[] columns = new Column[2];
            columns[0] = new Column();
            columns[0].setKey("pressure");
            columns[0].setValue(Float.toString(randomFloat()));
            columns[0].setValidFrom(dateFormat.parse(validToFrom[0][i][0]));
            columns[0].setValidTo(dateFormat.parse(validToFrom[0][i][1]));
            columns[1] = new Column();
            columns[1].setKey("temperature");
            columns[1].setValue(Float.toString(randomFloat()));
            columns[1].setValidFrom(dateFormat.parse(validToFrom[1][i][0]));
            columns[1].setValidTo(dateFormat.parse(validToFrom[1][i][1]));

            list.addAll(Arrays.asList(columns));
            table.setRawReadings(list);
            tables[i] = table;
            i++;
        }
        return tables;
    }
    private static Table[][] random_pump(int quant_primary_key,int for_each_primary_key) throws ParseException {
        String primary_key = "P";
        int i = 0;
        Table[][] tables = new Table[quant_primary_key][];
        while (quant_primary_key-->0){
            String current_key = primary_key + i;
            ArrayList<Column> def_column = new ArrayList<>();
            Column column = new Column();
            column.setKey("pumpCode");
            column.setValue(current_key);
            def_column.add(column);
            tables[i] = random_pump(for_each_primary_key,def_column);
            i++;
        }
        return tables;
    }
    private static Table[][] random_supervisor(int quant_primary_key,int for_each_primary_key) throws ParseException {
        String primary_key = "S";
        int i = 0;
        Table[][]  tables = new Table[quant_primary_key][];
        while (quant_primary_key-->0){
            String current_key = primary_key + i;
            ArrayList<Column> def_column = new ArrayList<>();
            Column column = new Column();
            column.setKey("supId");
            column.setValue(current_key);
            def_column.add(column);
            column = new Column();
            column.setKey("name");
            column.setValue(randomString(10));
            def_column.add(column);
            column = new Column();
            column.setKey("age");
            column.setValue(Integer.toString(randomInteger()));
            def_column.add(column);
            tables[i] = random_supervisor(for_each_primary_key,def_column);
            i++;
        }
        return tables;
    }
    private static Table[][] random_boiler(int quant_primary_key,int for_each_primary_key,ArrayList<String> pump_id,ArrayList<String> sup_id) throws ParseException {
        String primary_key = "B";
        int i = 0;
        Table[][] tables = new Table[quant_primary_key][];
        while (quant_primary_key-->0){
            String current_key = primary_key + i;
            ArrayList<Column> def_column = new ArrayList<>();
            Column column = new Column();
            column.setKey("boilerCode");
            column.setValue(current_key);
            def_column.add(column);
            column = new Column();
            column.setKey("pump_pumpCode");
            column.setValue(randomString(pump_id));
            def_column.add(column);
            column = new Column();
            column.setKey("supervisor_supId");
            column.setValue(randomString(sup_id));
            def_column.add(column);
            tables[i] = random_boiler(for_each_primary_key,def_column);
            i++;
        }
        return tables;
    }
    private static ArrayList<ArrayList<Table>> convert(Table[][] data){
        return Arrays.stream(data)
                .collect(Collectors.toCollection(ArrayList::new))
                .stream()
                .map(tables -> Arrays.stream(tables).collect(Collectors.toCollection(ArrayList::new)))
                .collect(Collectors.toCollection(ArrayList::new));
    }
    public static ArrayList<ArrayList<ArrayList<Table>>> getDatabase() throws ParseException {
        Table[][] supervisor = random_supervisor(PRIMARY_KEY_SUPERVISOR,TEMPORAL_VARIATION_SUPERVISOR);
        Table[][] pump = random_pump(PRIMARY_KEY_PUMP,TEMPORAL_VARIATION_PUMP);
        HashSet<String> pump_unique = new HashSet<>();
        HashSet<String> sup_unique = new HashSet<>();
        Arrays.stream(supervisor)
                .forEach(tables -> sup_unique.add(tables[0].getRawReadings().stream().filter(column -> column.getKey().equals("supId")).collect(Collectors.toCollection(ArrayList::new)).get(0).getValue()));
        Arrays.stream(pump)
                .forEach(tables -> pump_unique.add(tables[0].getRawReadings().stream().filter(column -> column.getKey().equals("pumpCode")).collect(Collectors.toCollection(ArrayList::new)).get(0).getValue()));
        ArrayList<String> pump_primary_key = new ArrayList<>(pump_unique);
        ArrayList<String> sup_primary_key = new ArrayList<>(sup_unique);
        Table[][] boiler = random_boiler(PRIMARY_KEY_BOILER,TEMPORAL_VARIATION_BOILER,pump_primary_key,sup_primary_key);
        ArrayList<ArrayList<ArrayList<Table>>> send = new ArrayList<>();
        send.add(convert(boiler));
        send.add(convert(pump));
        send.add(convert(supervisor));
        return send;
    }
    public static void applyDatabase(ArrayList<ArrayList<ArrayList<Table>>> randomDatabase) throws SQLException {
        for (ArrayList<ArrayList<Table>> arrayLists : randomDatabase) {
            for (ArrayList<Table> tables : arrayLists) {
                for (int i = 0, h = tables.size(); i < h; i++) {
                    if (i == 0) {
                        InsertQuery.insert(tables.get(i));
                    }else{
                        UpdateQuery.update(tables.get(i));
                    }
                }
            }
        }
    }
}
