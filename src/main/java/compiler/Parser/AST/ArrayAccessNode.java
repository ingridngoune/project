package compiler.Parser.AST;

public class ArrayAccessNode extends Expression {

    private final Expression array;
    private final Expression index;

    public ArrayAccessNode(Expression array, Expression index) {
        this.array = array;
        this.index = index;
    }

    public Expression getArray() {
        return array;
    }

    public Expression getIndex() {
        return index;
    }
}
