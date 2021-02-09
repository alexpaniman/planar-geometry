package lexer

data class Token(val type: TokenType, val symbol: String? = null)