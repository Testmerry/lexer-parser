package Lexer
import java.util.LinkedList;
class Lexer {
    companion object {
        fun lex(input: String): LinkedList<Token> {
            val tokens = LinkedList<Token>()
            var istype: Boolean
            var lexeme: Lexeme? = null //текущее значение type
            var substr = "" //текущее значение data
            var i = 0 //индекс перемещения по строке
            while (input.length > i) {
                istype = false
                substr += input[i] //считываем следующий символ
                for (l in Lexeme.values()) { //определяем тип токена
                    val pattern = l.pattern
                    val matcher = pattern.matcher(substr)
                    if (matcher.matches()) {
                        lexeme = l
                        istype = true //если определили успешно, то изменяем текущее значение токена и идем дальше
                        break
                    }
                }
                if (input[i] == ' ' || input[i] == '\n' || input[i] == '\t') { //если пробел, перенос строки или табуляция, то игнорируем
                    if (lexeme != null) {
                        tokens.add(Token(lexeme, substr.substring(0, substr.length - 1)))
                        lexeme = null
                        substr = ""
                    }
                }
                if (lexeme != null && !istype) { //если тип токена не определился при считывании нового символа, то возвращаемся назад на 1 символ и добавляем токен
                    substr = substr.substring(0, substr.length - 1)
                    i--
                    tokens.add(Token(lexeme, substr))
                    lexeme = null
                    substr = ""
                }
                i++
            }
            if (lexeme != null) tokens.add(Token(lexeme, substr)) //добавляем последний токен
            return tokens
        }
    }
}