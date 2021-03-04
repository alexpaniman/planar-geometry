package parser

import lexer.Token

abstract class ParserCombinator<T: Any>  {
    abstract fun parse(vararg tokens: Token): T?
}

fun <T: Any> ParserCombinator<T>.or(second: ParserCombinator<T>): ParserCombinator<T> =
    object : ParserCombinator<T>() {
        override fun parse(vararg tokens: Token): T? {
            TODO("Not yet implemented")
        }

    }