package persistence;

import java.util.ArrayList;
import java.util.List;

public class Excecutor {
    private List<AbstractSqlBuilder> statements;
    Excecutor(){
        this.statements = new ArrayList<AbstractSqlBuilder>();
    }
}
