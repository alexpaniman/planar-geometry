package lexer

import java.io.*
import java.lang.StringBuilder

class Lexer(file: File) {
    private val reader = file.bufferedReader()

    fun tokenize(): List<Token> {
        val tokens = mutableListOf<Token>()
        val builder = StringBuilder()

        while (reader.ready()) {
            if (builder.toString() == "def") {
                tokens.add(Token(TokenType.DEF))
                builder.clear()
            }

            when(val symbol = reader.read().toChar()) {
                '(' -> tokens.add(Token(TokenType.LCB))
                ')' -> tokens.add(Token(TokenType.RCB))
                else -> {
                    when {
                        symbol.isLetterOrDigit() ->
                            builder.append(symbol)

                        symbol.isWhitespace() -> {
                            if (builder.isNotBlank())
                                tokens.add(Token(TokenType.ID, builder.toString()))
                            builder.clear()
                        }

                        else -> {
                            throw LexerException("Unexpected token: $builder$symbol")
                        }
                    }
                }
            }
        }

        if (builder.isNotEmpty())
            tokens.add(Token(TokenType.ID, builder.toString()))

        return tokens
    }
}