package parser

import parsec.*
import kotlin.Int.Companion.MAX_VALUE

fun points(min: Int = 1, max: Int = MAX_VALUE) =
    (identifier right blank).many(min - 1, max).append(identifier)

fun name(min: Int = 1, max: Int = MAX_VALUE) =
    char('(') left points(min, max) right char(')')

private val polygon = spacedWord("polygon") left name(min = 3)
private val line = spacedWord("line") left name(min = 2, max = 2)
private val triangle = spacedWord("triangle") left name(min = 3, max = 3)

private val definition = spacedWord("def") left
        (polygon or line or triangle)

fun parseLines(lines: StringInput) {
    var input = lines
    while (!input.isEmpty) {
        try {
            definition.parse(input).unwrap()
        } catch (ignore: ParsecException) {}

        while (!input.isEmpty) {
            input = input.shrink(1)
            if (input[-1] == '\n')
                break
        }

    }
}

fun main() {
    val inputText = """
        def polygon (A B C)
        def polygon (A B)
        def polygon (A B)
        def polygon (A B)
    """.trimIndent()
    val input = StringInput(inputText)
    parseLines(input)
}
