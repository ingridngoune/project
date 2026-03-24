# LINFO2132 — Compiler Project

A hand written compiler for a custom imperative language, implemented in Java as a Gradle project.

---

## Current Status

| Phase | Status |
|-------|--------|
| Lexer |  Done |
| Parser | Done |
| Semantic Analysis | TODO |
| Code Generation | TODO |

---

## How to Compile

```bash
gradle build
```

---

## How to Run

### Lexer only
```bash
gradle run --args="-lexer input.lang"
```
Prints all tokens from the input file, one per line.

### Parser
```bash
gradle run --args="-parser input.lang"
```
Prints the AST produced from the input file.

---

## How to Run the Tests

```bash
gradle test
```

Test files are located in `src/test/`. One test file is provided per phase.

---

## Project Structure

```
src/
├── main/java/compiler/
│   ├── Compiler.java          # Entry point
│   ├── Lexer/
│   │   ├── Lexer.java         # Tokeniser
│   │   ├── Token.java         # Token enum
│   │   └── Symbol.java        # Token + value wrapper
│   └── Parser/
│       ├── Parser.java        # Recursive-descent parser
│       └── AST/               # All AST node classes
│           ├── ASTNode.java
│           ├── ProgramNode.java
│           ├── Declaration.java
│           ├── Statement.java
│           ├── ExpressionNode.java
│           └── ...
└── test/
    └── ...                    # Test  files
```

---

## Language Overview

- **Comments**: lines starting with `#`
- **Types**: `INT`, `FLOAT`, `BOOL`, `STRING`, and user-defined collections
- **Arrays**: declared as `INT[]`, created as `INT ARRAY[n]`
- **Collections**: record types declared with `coll`
- **Functions**: declared with `def`, optional return type (void if omitted)
- **Control flow**: `if/else`, `while`, `for` with range syntax `start -> end`
- **Constants**: declared with `final`

---

## Authors

- Ngoune Kenfack Ingrid Laure 
- Scheuren Cindie

---

## References

- [Crafting Interpreters](https://craftinginterpreters.com/scanning.html)
- [Java Language Specification](https://docs.oracle.com/javase/specs/jls/se8/html/jls-19.html)
- [Recursive Descent Parsing](https://ucsb-cs56-pconrad.github.io/tutorials/parsing_05_parsing_grammars_and_asts/)