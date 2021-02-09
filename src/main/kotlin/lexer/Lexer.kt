package lexer

import lexer.TokenType.ID
import java.io.*
import java.lang.StringBuilder

class Lexer(file: File) {
    private val reader = file.bufferedReader()

    fun tokenize(): List<Token> {
        val tokens = mutableListOf<Token>()
        val builder = StringBuilder()

        fun flushBuilder() {
            if (builder.isEmpty())
                return

            val symbol = builder.toString()
            val token = Token(ID, symbol)
            tokens.add(token)
            builder.clear()
        }

        while (reader.ready()) {
            if (builder.toString() == "def") {
                tokens.add(Token(TokenType.DEF))
                builder.clear()
            }

            when(val symbol = reader.read().toChar()) {
                '(' -> {
                    flushBuilder()
                    tokens.add(Token(TokenType.LCB))
                }

                ')' -> {
                    flushBuilder()
                    tokens.add(Token(TokenType.RCB))
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