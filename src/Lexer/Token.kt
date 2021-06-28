package Lexer

class Token(val _type:Lexeme,var _str:String) {

    val type:Lexeme
    var str:String
    init {
        type = _type
        str = _str

    }

     /*fun toString(){


    }*/




}