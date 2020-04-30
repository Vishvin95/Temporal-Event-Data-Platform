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

    Excecutor(){
        this.statements = new ArrayDeque<>();

    }
    public void addSqlQuery(AbstractSqlBuilder sqlBuilder){
        this.statements.addLast(sqlBuilder);
    }

    public List<ResultSet> execute(){
        List<ResultSet> resultSets = new ArrayList<>();
        for(AbstractSqlBuilder abstractSqlBuilder : statements){
            try {
                Statement statement = GlobalConnection.getConnection().createStatement();
                ResultSet resultSet = statement.executeQuery(abstractSqlBuilder.toString());
                logger.info("[Success] : "+abstractSqlBuilder.toString());
                resultSets.add(resultSet);
            } catch (SQLException e) {
                logger.error("[Fail] "+e+" [QUERY] : "+abstractSqlBuilder);
            }
        }
        return resultSets;
    }
}
