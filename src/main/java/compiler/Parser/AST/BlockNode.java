package compiler.Parser.AST;

import java.util.List;

public class BlockNode extends Statement{
    private final List<Statement> statements;

    public BlockNode(List<Statement> statements) {
        this.statements = statements;
    }

    public List<Statement> getStatements() {
        return statements;
    }
}
