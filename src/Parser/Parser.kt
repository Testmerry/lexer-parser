package Parser
import java.util.LinkedList
import kotlin.Throws
import Lexer.Lexeme
import Lexer.Token
import java.lang.Exception

object Parser {
    private var token: Token? = null
    private var tokens: LinkedList<Token>? = null
    private var pos = 0

   
    //@JvmStatic
    @Throws(Exception::class)
    fun parse(input: LinkedList<Token>?) {
        tokens = LinkedList(input)
        while (pos < tokens!!.size) {
            lang()
        }
        println("Parser: no syntax error were found")
    }

    private fun match() {
        token = tokens!![pos]
    }

    @Throws(Exception::class)
    private fun lang() {
        expr(pos)
    }

    @Throws(Exception::class)
    private fun expr(startPos: Int): Int {
        return try {
            whileExpr()
        } catch (ex: Exception) {
            try {
                pos = startPos
                ifExpr()
            } catch (e: Exception) {
                try {
                    pos = startPos
                    declarationExpr()
                } catch (ex1: Exception) {
                    try {
                        pos = startPos
                        funcExpr(pos)
                    } catch (ex2: Exception) {
                        pos = startPos
                        assignExpr()
                    }
                }
            }
        }
    }

    @Throws(Exception::class)
    private fun funcExpr(startPos: Int): Int {
        return try {
            pos = `var`(pos)
            pos = assignOp(pos)
            pos = `var`(pos)
            pos = method(pos)
            pos = func(pos)
            pos = lb(pos)
            pos = arExpr()
            pos = rb(pos)
            pos = cOp(pos)
            pos
        } catch (e: Exception) {
            pos = startPos
            pos = `var`(pos)
            pos = method(pos)
            pos = func(pos)
            pos = lb(pos)
            pos = arExpr()
            pos = rb(pos)
            pos = cOp(pos)
            pos
        }
    }

    @Throws(Exception::class)
    private fun method(startPos: Int): Int {
        match()
        if (token!!.type !== Lexeme.METHOD) {
            throw Exception("Вместо METHOD найдено: " + token!!.type + " " + token!!.str)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun whileExpr(): Int {
        pos = whileW(pos)
        pos = term()
        pos = body()
        return pos
    }

    @Throws(Exception::class)
    private fun ifExpr(): Int {
        pos = ifW(pos)
        pos = term()
        pos = body()
        return pos
    }

    @Throws(Exception::class)
    private fun term(): Int {
        pos = lb(pos)
        pos = boolExpr(pos)
        pos = rb(pos)
        return pos
    }

    @Throws(Exception::class)
    private fun boolExpr(startPos: Int): Int {
        var startPos = startPos
        try {
            pos = arExpr()
        } catch (ex: Exception) {
            pos = startPos
            pos = operand(pos)
        }
        pos = boolOp(pos)
        startPos = pos
        try {
            pos = arExpr()
        } catch (ex: Exception) {
            pos = startPos
            pos = operand(pos)
        }
        return pos
    }

    @Throws(Exception::class)
    private fun operand(startPos: Int): Int {
        return try {
            `var`(pos)
        } catch (ex: Exception) {
            try {
                pos = startPos
                num(pos)
            } catch (ex2: Exception) {
                pos = startPos
                bracketExpr()
            }
        }
    }

    @Throws(Exception::class)
    private fun bracketExpr(): Int {
        pos = lb(pos)
        pos = inBrackets(pos)
        pos = rb(pos)
        return pos
    }

    @Throws(Exception::class)
    private fun inBrackets(startPos: Int): Int {
        try {
            pos = bracketExpr()
        } catch (ex: Exception) {
            pos = startPos
            pos = arExpr()
        }
        return pos
    }

    @Throws(Exception::class)
    private fun arExpr(): Int {
        pos = operand(pos)
        while (true) {
            try {
                pos = arOp(pos)
            } catch (e: Exception) {
                break
            }
            try {
                pos = operand(pos)
            } catch (e: Exception) {
                throw Exception(e.message)
            }
        }
        return pos
    }

    @Throws(Exception::class)
    private fun body(): Int {
        pos = lcb(pos)
        pos = bodyExpr()
        pos = rcb(pos)
        return pos
    }

    @Throws(Exception::class)
    private fun bodyExpr(): Int {
        pos = expr(pos)
        var startPos = pos
        while (true) {
            try {
                pos = expr(pos)
                startPos = pos
            } catch (ex: Exception) {
                pos = startPos
                break
            }
        }
        return pos
    }

    @Throws(Exception::class)
    private fun assignExpr(): Int {
        pos = `var`(pos)
        var startPos = pos
        try {
            pos = assignOp(pos)
        } catch (ex: Exception) {
            pos = startPos
            pos = incAndDec(pos)
            pos = cOp(pos)
            return pos
        }
        startPos = pos
        try {
            pos = arExpr()
        } catch (ex: Exception) {
            pos = startPos
            pos = operand(pos)
        }
        pos = cOp(pos)
        return pos
    }

    @Throws(Exception::class)
    private fun incAndDec(startPos: Int): Int {
        try {
            pos = inc(startPos)
        } catch (ex: Exception) {
            pos = startPos
            pos = dec(startPos)
        }
        return pos
    }

    @Throws(Exception::class)
    private fun declarationExpr(): Int {
        pos = `var`(pos)
        pos = typeW(pos)
        pos = type(pos)
        pos = cOp(pos)
        return pos
    }

    @Throws(Exception::class)
    private fun whileW(startPos: Int): Int {
        match()
        if (token!!.type !== Lexeme.WHILE_KW) {
            throw Exception("Ключевое слово while не найдено: " + token!!.type + " " + token!!.str)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun ifW(startPos: Int): Int {
        match()
        if (token!!.type !== Lexeme.IF_KW) {
            throw Exception("Ключевое слово if не найденоо: " + token!!.type + " " + token!!.str)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun typeW(startPos: Int): Int {
        match()
        if (token!!.type !== Lexeme.TYPE_CREATE) {
            throw Exception("Ключевое слово new не найдено: " + token!!.type + " " + token!!.str)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun type(startPos: Int): Int {
        match()
        if (token!!.type !== Lexeme.TYPE) {
            throw Exception("Ключевое слово type не найдено " + token!!.type + " " + token!!.str)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun lb(startPos: Int): Int {
        match()
        if (token!!.type !== Lexeme.LEFT_PAR) {
            throw Exception("'(' не найдено " + token!!.type + " " + token!!.str)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun rb(startPos: Int): Int {
        match()
        if (token!!.type !== Lexeme.RIGHT_PAR) {
            throw Exception("')' не найдено " + token!!.type + " " + token!!.str)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun boolOp(startPos: Int): Int {
        match()
        if (token!!.type !== Lexeme.BOOL_OP) {
            throw Exception("Логическая операция не найдена. Вместо этого" + token!!.type + " " + token!!.str)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun func(startPos: Int): Int {
        match()
        if (token!!.type !== Lexeme.SET_OP) {
            throw Exception("Set не найдено. Вместо этого" + token!!.type + " " + token!!.str)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun `var`(startPos: Int): Int {
        match()
        if (token!!.type !== Lexeme.VAR) {
            throw Exception("var не найдено, вместо этого: " + token!!.type + " " + token!!.str + " " + startPos)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun inc(startPos: Int): Int {
        match()
        if (token!!.type !== Lexeme.INC) {
            throw Exception("Знак инкремента не найден. Вместо этого " + token!!.type + " " + token!!.str)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun dec(startPos: Int): Int {
        match()
        if (token!!.type !== Lexeme.DEC) {
            throw Exception("Знак декремента не найден. Вместо этого " + token!!.type + " " + token!!.str)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun num(startPos: Int): Int {
        match()
        if (token!!.type !== Lexeme.NUMBER) {
            throw Exception("Вместо number : " + token!!.type + " " + token!!.str)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun arOp(startPos: Int): Int {
        match()
        if (token!!.type !== Lexeme.MATH_OP) {
            throw Exception("Вместо arOp найдено: " + token!!.type + " " + token!!.str)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun lcb(startPos: Int): Int {
        match()
        if (token!!.type !== Lexeme.LEFT_BR) {
            throw Exception("Вместо '{' найдено: " + token!!.type + " " + token!!.str)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun rcb(startPos: Int): Int {
        match()
        if (token!!.type !== Lexeme.RIGHT_BR) {
            throw Exception("Вместо '}' найдено: " + token!!.type + " " + token!!.str)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun assignOp(startPos: Int): Int {
        match()
        if (token!!.type !== Lexeme.ASSIGN_OP) {
            throw Exception("Вместо assignOp найдено: " + token!!.type + " " + token!!.str)
        }
        return startPos + 1
    }

    @Throws(Exception::class)
    private fun cOp(startPos: Int): Int {
        match()
        if (token!!.type !== Lexeme.SC) {
            throw Exception("Вместо ':' найдено: " + token!!.type + " " + token!!.str + " " + pos)
        }
        return startPos + 1
    }
}