package compiler.Semantic;

import java.util.Objects;

public class SemanticType {
    private final String name;
    private final boolean array;

    public SemanticType(String name, boolean array){
        this.name=name;
        this.array=array;
    }

    public String getName(){
        return name;
    }

    public boolean isArray(){
        return array;
    }

    public boolean isNumeric(){
        return !array && (name.equals("INT") || name.equals("FLOAT"));

    }

    public boolean isBool(){
        return !array && name.equals("BOOL");
    }

    public boolean isString(){
        return !array && name.equals("STRING");
    }

    @Override
    public boolean equals(Object o){
        if(this==o) return true;
        if(!(o instanceof SemanticType o2)) return false;
        return array == o2.array && Objects.equals(name,o2.name);
    }

    @Override
    public int hashCode(){
        return Objects.hash(name,array);
    }

    @Override
    public String toString() {
        return  array?name + "[]":name;
    }
}
