package Lexer
import java.util.LinkedList;
class Lexer {
    companion object {
        fun lex(input: String): LinkedList<Token> {
            val tokens = LinkedList<Token>()
            var istype: Boolean
            var lexeme: Lexeme? = null
            var substr = ""
            var i = 0
            while (input.length > i) {
                istype = false
                substr += input[i]
                for (l in Lexeme.values()) {
                    val pattern = l.pattern
                    val matcher = pattern.matcher(substr)
                    if (matcher.matches()) {
                        lexeme = l
                        istype = true
                        break
                    }
                }
                if (input[i] == ' ' || input[i] == '\n' || input[i] == '\t') {
                    if (lexeme != null) {
                        tokens.add(Token(lexeme, substr.substring(0, substr.length - 1)))
                        lexeme = null
                        substr = ""
                    }
                }
                if (lexeme != null && !istype) {
                    substr = substr.substring(0, substr.length - 1)
                    i--
                    tokens.add(Token(lexeme, substr))
                    lexeme = null
                    substr = ""
                }
                i++
            }
            if (lexeme != null) tokens.add(Token(lexeme, substr))
            return tokens
        }
    }
}