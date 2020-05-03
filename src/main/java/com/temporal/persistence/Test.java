package com.temporal.persistence;
/* 
    Apricot Management Suite
    Copyright (C) 2020 Tarang Parikh
    
    Email : tp0265@gmail.com
    Project Home : https://github.com/tarangparikh/apricot
    
    Original Author : @author Tarang Parikh <tp0265@gmail.com>
    
*/

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

public class Test {
    public static void main(String...args){
        String[] query = new String[]{"use Factory","show tables","describe boiler","describe uses"};
        Excecutor excecutor = new Excecutor();
        Arrays.stream(query).map(GenericSqlBuilder::new).forEach(excecutor::addSqlQuery);
        List<ResultSet> execute = excecutor.execute();
        execute.forEach(DBTablePrinter::printResultSet);
    }
}
