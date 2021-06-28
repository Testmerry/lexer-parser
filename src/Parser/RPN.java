package Parser;

import Lexer.Lexeme;
import Lexer.Token;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class RPN {
    public static LinkedList<Token> calc = new LinkedList<>();

    public static LinkedList<Token> makeRPN(Queue<Token> input) {
        while (!input.isEmpty()) {
            Token token = input.peek();
            if (!(token.get_type() == Lexeme.WHILE_KW || token.get_type() == Lexeme.IF_KW)) {
                makeRPNFromExpr(input);
            }
            else {
                makeRPNFromWhile(input, token);
            }
        }

        return calc;
    }

    private static void makeRPNFromWhile(Queue<Token> input, Token tmp) {
        Queue<Token> boolExpr = new LinkedList<>();
        input.poll();
        Token token = input.poll();
        int index = calc.size();
        while (true) {
            assert token != null;
            if (token.get_type() == Lexeme.LEFT_BR) break;
            boolExpr.add(token);
            token = input.poll();
        }

        makeRPNFromExpr(boolExpr);
        if (tmp.get_type() == Lexeme.WHILE_KW) {
            calc.add(new Token(Lexeme.GOTO_INDEX, Integer.toString(p(calc.size(), input))));
        }
        String p = Integer.toString(p(calc.size(), input));
        if (tmp.get_type() == Lexeme.IF_KW) {
            calc.add(new Token(Lexeme.GOTO_INDEX, p));
        }
        calc.add(new Token(Lexeme.GOTO, "!F"));

        Queue<Token> expr = new LinkedList<>();
        token = input.poll();
        while (true) {
            assert token != null;
            if (token.get_type() == Lexeme.RIGHT_BR) break;
            if (token.get_type() == Lexeme.WHILE_KW || token.get_type() == Lexeme.IF_KW) {
                makeRPNFromExpr(expr);
                makeRPNFromWhile(input, token);
            }
            if (!(token.get_type() == Lexeme.WHILE_KW || token.get_type() == Lexeme.IF_KW))
                expr.add(token);
            token = input.poll();
        }
        makeRPNFromExpr(expr);
        if (tmp.get_type() != Lexeme.IF_KW)
            calc.add(new Token(Lexeme.GOTO_INDEX, Integer.toString(index)));
        if (tmp.get_type() != Lexeme.WHILE_KW)
            calc.add(new Token(Lexeme.GOTO_INDEX, p));
        calc.add(new Token(Lexeme.GOTO, "!"));
    }

    private static int p(int size, Queue<Token> tokens) {
        int p = size;
        int i = 1;

        Queue<Token> newtokens = new LinkedList<>(tokens);
        newtokens.poll();
        Token newtoken;

        while (i > 0){
            if (newtokens.isEmpty())
            {
                break;
            }
            else
            {
                newtoken = newtokens.poll();
            }
            if (newtoken.get_type() == Lexeme.WHILE_KW || newtoken.get_type() == Lexeme.IF_KW) {
                i++;
                p--;
            }
            if (newtoken.get_type() == Lexeme.RIGHT_BR) {
                i--;
            }
            if (newtoken.get_type() != Lexeme.SC) {
                if(!(newtoken.get_type() == Lexeme.INC || newtoken.get_type() == Lexeme.DEC)){
                    p++;
                }
                else p = p + 4;
            }
        }
        p+=4;

        return p;
    }


    private static void makeRPNFromExpr(Queue<Token> input) {
        Stack<Token> stack = new Stack<>();

        while (!input.isEmpty()) {
            Token token = input.peek();

            if (token.get_type() == Lexeme.WHILE_KW || token.get_type() == Lexeme.IF_KW) {
                break;
            }

            if (token.get_type() == Lexeme.TYPE_CREATE) {
                stack.add(token);
            }

            if (token.get_type() == Lexeme.TYPE) {
                calc.add(token);
                calc.add(stack.pop());
            }

            token = input.poll();
            assert token != null;
            if (token.get_type() == Lexeme.INC || token.get_type() == Lexeme.DEC)
            {
                calc.add(calc.getLast());
                Token tmpToken = new Token(Lexeme.NUMBER, "1");
                calc.add(tmpToken);
                if (token.get_type() == Lexeme.INC)
                {
                    tmpToken = new Token(Lexeme.MATH_OP, "+");
                }
                else {tmpToken = new Token(Lexeme.MATH_OP, "-");}
                calc.add(tmpToken);
                calc.add(new Token(Lexeme.ASSIGN_OP, "="));
            }

            //Если лексема является числом или переменной, добавляем ее в ПОЛИЗ-массив.
            if (token.get_type() == Lexeme.VAR || token.get_type() == Lexeme.NUMBER) {
                calc.add(token);
            }

            //Если лексема является бинарной операцией, тогда:
            if (token.get_type() == Lexeme.MATH_OP || token.get_type() == Lexeme.BOOL_OP || token.get_type() == Lexeme.ASSIGN_OP || token.get_type() == Lexeme.SET_OP) {
                if (!stack.empty()) {
                    while (getPriorOfOp(token.getStr()) >= getPriorOfOp(stack.peek().getStr())) {
                        calc.add(stack.pop());
                        if (stack.empty()){
                            break;
                        }
                    }
                }
                stack.push(token);
            }

            //Если лексема является открывающей скобкой, помещаем ее в стек.
            if (token.get_type() == Lexeme.LEFT_PAR) {
                stack.push(token);
            }

            if (token.get_type() == Lexeme.RIGHT_PAR) {
                if (!stack.empty()) {
                    while (!stack.empty() && stack.peek().get_type() != Lexeme.LEFT_PAR) {
                        calc.add(stack.pop());
                    }
                    if (!stack.empty() && stack.peek().get_type() == Lexeme.LEFT_PAR) {
                        stack.pop();
                    }
                }
            }

            if (token.get_type() == Lexeme.SC) {
                while (!stack.empty()) {
                    calc.add(stack.pop());
                }
            }
        }

        while (!stack.empty()) {
            calc.add(stack.pop());
        }
    }

    private static int getPriorOfOp(String op) {
        int priority;
        switch (op) {
            case "*":
            case "/":
                priority= 0;
            case "+":
            case "-":priority = 2;
            case ">":
            case ">=":
            case "<":
            case "<=":
            case "==":
            case "!=":priority = 3;
            case "=":priority = 5;
            default:priority= 4;
        };
        return priority;
    }

}