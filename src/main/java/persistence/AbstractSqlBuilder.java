package persistence;

import java.util.List;

abstract class AbstractSqlBuilder {
    public void appendData(StringBuilder sb, List<?> objects,String first,String sep){
        boolean start = true;
        for(Object ob : objects) {
            if (start) sb.append(first);
            else sb.append(sep);

            sb.append(ob);
            start = false;
        }
    }
}
