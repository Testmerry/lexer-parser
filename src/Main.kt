
import Lexer.Lexer.Companion.lex

import Parser.Parser.parse
import kotlin.jvm.JvmStatic
import java.util.LinkedList
import Parser.RPN
import java.lang.Exception


    // @JvmStatic
    fun main(args: Array<String>) {
        val tokens =
            lex("a = 10; i = 0; while(i < 12){i = i + 1;} a--; a = a - 10; array new List; array.add(5-2); array.add(22); array.add(4*4); array.add(3/3); arrayNew new Set; arrayNew.add(3); arrayNew.add(6+ 7 / 2); c = array.get(2); ")
        println("--------- Доступные токены: ------------")
        for (value in tokens) {
            println(value.str)
        }
        try {
            parse(tokens)
        } catch (ex: Exception) {
            System.err.println(ex)
            System.exit(1)
        }
        println("-------------- ОПЗ: ------------")
        val testCalc = RPN.makeRPN(tokens)
        var i = 1
        for (token in testCalc) {
            println("$i ${token.str}")
            i++
        }
    }
