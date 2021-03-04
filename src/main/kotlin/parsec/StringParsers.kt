package parsec

import parsec.Parser.Companion.fromLambda
import parsec.Result.*

data class StringInput(val input: String, val position: Int = 0) {
    val isEmpty: Boolean
        get() = position !in input.indices
    val current: Char get() = input[position]

    fun shrink(shift: Int) =
        this.copy(position = position + shift)

    operator fun get(i: Int) = input[position + i]
}

class ErrorInContext(private val expected: String, private val context: StringInput): ErrorHolder() {
    override val message: String
        get() {
            var column = 1
            var row = 1

            val previousSymbols = context.input.take(context.position)
            for (symbol in previousSymbols) {
                if (symbol == '\n') {
                    ++ row
                    column = 1
                } else ++ column
            }

            val line = context.input.lines()[row - 1]
            val cursor = " ".repeat(column - 1) + "^"

            val number = "$row"
            val filler = " ".repeat(number.length)

            return """
            error at $row:$column: expected $expected, got '${context.current}'
             $number | $line
             $filler | $cursor
             
            """.trimIndent()
        }

    override fun either(other: ErrorHolder): ErrorHolder {
        if (other is ErrorInContext) {
            if (other.context.position > this.context.position)
                return other

            if (this.context.position > other.context.position)
                return this

            return ErrorInContext(expected + " or " + other.expected, context)
        }

        return other
    }
}

fun StringInput.contextualError(expected: String) = Error(ErrorInContext(expected, this))

inline fun satisfy(expectedMessage: String, crossinline condition: (Char) -> Boolean): Parser<StringInput, Char> =
    fromLambda { input ->
        val symbol = input.current
        if (!input.isEmpty && condition(symbol))
            Success(symbol, input.shrink(1))
        else
            input.contextualError(expectedMessage)
    }

fun char(symbol: Char) = satisfy("'$symbol'") { it == symbol }

fun word(word: String): Parser<StringInput, String> =
    fromLambda { input ->
        var matches = true
        for ((index, symbol) in word.withIndex())
            if (input[index] != symbol) {
                matches = false; break
            }

        if (matches)
            Success(word, input.shrink(word.length))
        else
            input.contextualError("\"$word\"")
    }

val blank = satisfy("space or tabulation") {
    it.isWhitespace()
}

val space = blank.many(1)

val digit = satisfy("digit") {
    it.isDigit()
}

val letter = satisfy("letter") {
    it.isLetter()
}

private val symbol = letter or digit or char('_')
val identifier = (letter append symbol.many()).map { it.joinToString("") }

val eof = fromLambda<StringInput, String> { input ->
    if (input.isEmpty) Success("", input)
    else input.contextualError("end of file")
}

fun spacedWord(word: String) = word(word) left blank

@JvmName("appendValueToList")
infix fun <TInput, TValue> Parser<TInput, TValue>.append(other: Parser<TInput, List<TValue>>) =
    this.combine(other) { fst, snd -> listOf(fst) + snd }

@JvmName("appendListToValue")
infix fun <TInput, TValue> Parser<TInput, List<TValue>>.append(other: Parser<TInput, TValue>) =
    this.combine(other) { fst, snd -> fst + snd }
