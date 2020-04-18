package persistence;

public class SubSelectBuilder extends SelectBuilder {

    private String alias;

    public SubSelectBuilder(String alias){
        this.alias = alias;
    }
    protected SubSelectBuilder(SubSelectBuilder subSelectBuilder){
        super(subSelectBuilder);
        this.alias = subSelectBuilder.alias;
    }

    @Override
    public SubSelectBuilder clone(){
        return new SubSelectBuilder(this);
    }

    public String toString(){
        return "(" +
                super.toString() +
                ") as" +
                alias;
    }
}
