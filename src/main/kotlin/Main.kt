import latex.compileTikZ
import lexer.Lexer
import objects.circle.Circle
import objects.circle.XYCircle
import objects.illustration.Illustration
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
        .also { it.objects.addAll(objects) }

    var seed = System.currentTimeMillis()
    println("Seed: $seed")

    // seed = 1612969559065

    val random = Random(seed)

    illustration.setup(random, XYCircle(XYPoint("NONE", 0.0, 0.0), 100.0))
    val image = illustration.define()

    val tikz = TikZ()
        .also { image.draw(it) }
        .tikzify()

    compileTikZ(tikz, "my-test-tikzpicture")
    // openInZathura(output)
}