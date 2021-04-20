package parsec

import parsec.Parser.Companion.fromLambda
import parsec.Result.*

data class StringInput<TContext>(val input: String, val context: TContext, val position: Int = 0) {
    val isEmpty: Boolean
        get() = position !in input.indices
    val current: Char? get() = input.getOrNull(position)

    fun shrink(shift: Int) =
        this.copy(position = position + shift)

    operator fun get(i: Int) = input.getOrNull(position + i)
}

class ErrorInContext(private val expected: String, private val context: StringInput<*>): ErrorHolder() {
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

            val got = when {
                context.isEmpty -> "end of file"
                context.current == '\n' -> "end of line"
                else -> "'${context.current}'"
            }

            return """
            error at $row:$column: expected $expected, got $got
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

            return when {
                expected !in other.expected && other.expected !in expected ->
                    ErrorInContext(expected + " or " + other.expected, context)

                expected.length > other.expected.length -> this
                else -> other
            }

        }

        return other
    }
}

fun StringInput<*>.contextualError(expected: String) = Error(ErrorInContext(expected, this))

inline fun <TContext, TValue> Parser<StringInput<TContext>, TValue>.ensure(expected: String, crossinline condition: (TValue) -> Boolean) =
    ensure({ it.contextualError(expected) }, condition)

inline fun <TContext> satisfy(expectedMessage: String, crossinline condition: (Char) -> Boolean): Parser<StringInput<TContext>, Char> =
    fromLambda { input ->
        if (!input.isEmpty && condition(input.current!!))
            Success(input.current!!, input.shrink(1))
        else
            input.contextualError(expectedMessage)
    }

fun <TContext> char(symbol: Char) = satisfy<TContext>("'$symbol'") { it == symbol }

fun <TContext> word(word: String): Parser<StringInput<TContext>, String> =
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

fun <TContext> blank() = satisfy<TContext>("space or tabulation") {
    it.isWhitespace()
}

fun <TContext> space(min: Int = 1) = blank<TContext>().many(min)

fun <TContext> digit() = satisfy<TContext>("digit") {
    it.isDigit()
}

fun <TContext> letter() = satisfy<TContext>("letter") {
    it.isLetter()
}

fun <TContext> number() = digit<TContext>().many(min = 1)
    .combine(char<TContext>('.').then(digit<TContext>().many()).optional()) { before, after ->
        (before + '.' + after).joinToString("")
            .toDouble()
    }

private fun <TContext> symbol() = letter<TContext>() or digit() or char('_')
fun <TContext> identifier() = (letter<TContext>() append symbol<TContext>().many())
    .map { it.joinToString("") }

fun <TContext> eof() = fromLambda<StringInput<TContext>, String> { input ->
    if (input.isEmpty)
        Success("", input)
    else input.contextualError("end of file")
}

fun <TContext> eol() = eof<TContext>() or
        satisfy<TContext>("end of line") { it == '\n' }.stringify()

fun <TContext> anything() = fromLambda<StringInput<TContext>, Char> { input ->
    if (input.isEmpty || input.current == '\n')
        input.contextualError("something")
    else Success(input.current!!, input.shrink(1))
}

fun <TContext> spacedWord(word: String) = word<TContext>(word) then blank()

@JvmName("appendValueToList")
infix fun <TInput, TValue> Parser<TInput, TValue>.append(other: Parser<TInput, List<TValue>>) =
    this.combine(other) { fst, snd -> listOf(fst) + snd }

@JvmName("appendListToValue")
infix fun <TInput, TValue> Parser<TInput, List<TValue>>.appendV(other: Parser<TInput, TValue>) =
    this.combine(other) { fst, snd -> fst + snd }

@JvmName("appendListToList")
infix fun <TInput, TValue> Parser<TInput, List<TValue>>.append(other: Parser<TInput, List<TValue>>) =
    this.combine(other) { fst, snd -> fst + snd }