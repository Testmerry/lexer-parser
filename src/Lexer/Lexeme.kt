package Lexer

import java.util.regex.Pattern;
enum class Lexeme(pattern:String) {
    NUMBER("0|[1-9]{1}[0-9]*"),
    LEFT_BR("\\{"),
    RIGHT_BR("\\}"),
    LEFT_PAR("\\("),
    RIGHT_PAR("\\)"),
    SC(";"),
    GOTO(""),
    GOTO_INDEX(""),
    ASSIGN_OP("="),
    METHOD("\\."),
    SET_OP("add|remove|get|contains"),
    TYPE_CREATE("new"),
    TYPE("Set|List"),
    WHILE_KW("while"),
    FOR_KW("for"),
    IF_KW("if"),
    ELSE_KW("else"),
    PRINT_KW("print"),
    BOOL_OP("<|>|<=|>=|==|!="),
    BOOL("true|false"),
    VAR("[a-zA-Z][a-zA-Z0-9_]*"),
    INC("\\+\\+"),
    DEC("\\--"),
    MATH_OP("[*|/|+|-]");

    val pattern: Pattern = Pattern.compile(pattern)
}