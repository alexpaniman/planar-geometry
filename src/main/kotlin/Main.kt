import latex.compileTikZ
import lexer.Lexer
import objects.circle.Circle
import objects.circle.XYCircle
import objects.illustration.Illustration
import objects.illustration.style.IllustrationStyle
import objects.point.AnyPoint
import objects.point.XYPoint
import parser.Parser
import tikz.TikZ
import java.io.File
import java.lang.Thread.sleep
import kotlin.random.Random

fun openInZathura(file: File) {
    val runtime = Runtime.getRuntime()
    runtime.exec("zathura ${file.absolutePath}")
}

fun main() {
    val file = File("src/main/resources/example")

    val tokens = Lexer(file)
        .tokenize()
    val objects = Parser()
        .parse(tokens)

    val illustration = Illustration()
        .also {
            it.objects.addAll(objects)
            it.applyStyle(IllustrationStyle)
        }

    val seed = /* System.currentTimeMillis() */ 1613080623291
    println("Seed: $seed\n")

    // seed = 1612969559065

    val random = Random(seed)

    val circle = XYCircle(XYPoint(0.0, 0.0), 100.0)
    illustration.setup(random, circle)

    val canvasTikZ = TikZ()
    illustration.draw(canvasTikZ)

    val tikzCode = canvasTikZ.tikzify()
    println(tikzCode)

    compileTikZ(tikzCode, "my-test-tikzpicture")
    // openInZathura(output)
}