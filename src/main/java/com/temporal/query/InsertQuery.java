package com.temporal.query;


import com.temporal.model.*;
import com.temporal.persistence.Excecutor;
import com.temporal.persistence.GenericSqlBuilder;
import javafx.util.Pair;

import java.sql.ResultSet;
import java.util.*;

public class InsertQuery extends CreateQuery {



    public static void insert(Table table)  {

        HashMap<String,String> dataType_Resolver=CreateQuery.DataTypeResolver();

        ArrayList<Column> columns=table.getRawReadings();
        for(Column column:columns)
        {

        }

    }
}
