package compiler.Parser.AST;

import java.util.List;

public class BlockNode extends StatementNode {
    private final List<StatementNode> statements;

    public BlockNode(List<StatementNode> statements) {
        this.statements = statements;
    }

    public List<StatementNode> getStatements() {
        return statements;
    }

    @Override
    public String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent).append("Block\n");
        for (StatementNode stmt : statements) {
            sb.append(stmt.toString(indent + "  "));
        }
        return sb.toString();
    }
}
