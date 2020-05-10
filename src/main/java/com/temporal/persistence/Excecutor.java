package com.temporal.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class Excecutor {
    private ArrayDeque<AbstractSqlBuilder> statements;

    public static Logger logger = LogManager.getLogger(Excecutor.class);

    public Excecutor(){
        this.statements = new ArrayDeque<>();

    }
    public Excecutor addSqlQuery(AbstractSqlBuilder sqlBuilder){
        this.statements.addLast(sqlBuilder);
        return this;
    }

    public List<ResultSet> execute() throws SQLException {
        List<ResultSet> resultSets = new ArrayList<>();
        for(AbstractSqlBuilder abstractSqlBuilder : statements){
            try {
                Statement statement = GlobalConnection.getConnection().createStatement();
                String query[] = abstractSqlBuilder.toString().split(" ");
                if(query[0].toLowerCase().equals("create")
                        ||query[0].toLowerCase().equals("update")
                        ||query[0].toLowerCase().equals("alter")
                        ||query[0].toLowerCase().equals("use")
                        ||query[0].toLowerCase().equals("insert")
                        ||query[0].toLowerCase().equals("drop")
                )
                statement.executeUpdate(abstractSqlBuilder.toString());
                else{
                    ResultSet resultSet = statement.executeQuery(abstractSqlBuilder.toString());
                    resultSets.add(resultSet);
                }
                logger.info("[Success] : "+abstractSqlBuilder.toString());
            } catch (SQLException e) {
                logger.error("[Fail] "+e+" [QUERY] : "+abstractSqlBuilder);
                throw e;
            }
        }
        return resultSets;
    }
    
    public void clear()
    {
    	statements.clear();
    }
}
