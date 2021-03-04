package lexer

import lexer.TokenType.*
import java.io.*
import java.lang.StringBuilder

class Lexer(file: File) {
    private val reader = file.bufferedReader()

    private val words = mapOf(
        "def" to DEF,
        "in" to IN,
        "ratio" to RATIO
    )

    fun tokenize(): List<Token> {
        val tokens = mutableListOf<Token>()
        val builder = StringBuilder()

        fun flushBuilder() {
            if (builder.isBlank())
                return

            val symbol = builder.toString()
            val token = Token(ID, symbol)
            tokens.add(token)
            builder.clear()
        }

        while (reader.ready()) {
            for ((word, type) in words)
                if (builder.toString() == word) {
                    val token = Token(type)

                    tokens.add(token)
                    builder.clear()
                }

            when(val symbol = reader.read().toChar()) {
                '(' -> {
                    flushBuilder()
                    tokens.add(Token(LCB))
                }

                ')' -> {
                    flushBuilder()
                    tokens.add(Token(RCB))
                }

                ':' -> {
                    flushBuilder()
                    tokens.add(Token(COLON))
                }

                else -> {
                    when {
                        symbol.isLetterOrDigit() ->
                            builder.append(symbol)

                        symbol.isWhitespace() -> flushBuilder()

                        else ->
                            throw LexerException("Unexpected token: $builder$symbol")
                    }
                }
            }
        }

        flushBuilder()

        return tokens
    }
}