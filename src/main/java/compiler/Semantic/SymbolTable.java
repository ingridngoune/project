package compiler.Semanctic;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final SymbolTable previousTable;
    private final Map<String,SymbolInfo> entries;

    public SymbolTable(SymbolTable previousTable) {
        this.previousTable = previousTable;
        this.entries = new HashMap<>();
    }

    public void addSymbol(String name,SymbolInfo info ){
        if(entries.containsKey(name)){
            throw new RuntimeException("Scope error :" + name + "already declare in this scope");
        }
        entries.put(name,info);
    }

    public SymbolInfo getSymbol(String name){
        if(entries.containsKey(name)){
            return entries.get(name);
        }
        if(previousTable!=null){
            return previousTable.getSymbol(name);
        }
        throw new RuntimeException("ScopeError: unknown identifier " + name);
    }
}
