package lexer

class LexerException(private val name: String) : Exception() {
    override val message
        get() = name
}