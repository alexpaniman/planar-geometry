import latex.compileTikZ
import objects.circle.XYCircle
import objects.container.Illustration
import objects.point.XYPoint
import parsec.ParsecException
import parser.parse
import tikz.TikZ
import java.io.File
import kotlin.random.Random
import kotlin.system.exitProcess

fun openInZathura(file: File) {
    val runtime = Runtime.getRuntime()
    runtime.exec("zathura ${file.absolutePath}")
}

fun copy(input: String) {
    val runtime = Runtime.getRuntime()
    runtime.exec("echo '$input' | xclip -selection clipboard")
}

val DEBUG_TIKZ = TikZ()

private fun wrapInTikZ(seed: Long, source: String, tikz: String) = """
        | \begin{minipage}{0.45\textwidth}
        | \begin{verbatim}
        | seed $seed
        | ${source.lines().filter { it.isNotBlank() && !it.startsWith('#') }.joinToString("\n")}
        | \end{verbatim}
        | \end{minipage}
        | \begin{minipage}{0.45\textwidth}
        |   \begin{center}
        |     \scalebox{0.7}{
        |       \begin{tikzpicture}
        | ${tikz.lines().joinToString("\n") { "        $it" }}
        |       \end{tikzpicture}
        |     }
        |   \end{center}
        | \end{minipage}
        | """.trimMargin("| ")

fun main() {
    val inputText = File("src/main/resources/example")
        .readText()

    val objects = try {
        parse(inputText).toMutableList()
    } catch (ignore: ParsecException) {
        exitProcess(1)
    }

    val illustration = Illustration(objects)

    val seed = System.currentTimeMillis()
    val random = Random(seed)

    val circle = XYCircle(XYPoint(0.0, 0.0), 5.0)
    illustration.setup(circle, random)

    illustration.draw(DEBUG_TIKZ)

    val tikzCode = DEBUG_TIKZ.tikzify()
    println(wrapInTikZ(seed, inputText, tikzCode))

    compileTikZ(tikzCode, "my-test-tikzpicture")
    // openInZathura(output)
}