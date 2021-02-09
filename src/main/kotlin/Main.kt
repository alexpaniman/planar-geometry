import latex.compileTikZ
import lexer.Lexer
import objects.illustration.Illustration
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

    for (i in 2..10) {
        val random = Random(i * 1010101010L)

        illustration.setup(random)
        val image = illustration.define()

        val tikz = TikZ()
            .also { image.draw(it) }
            .tikzify()

        compileTikZ(tikz, "my-test-tikzpicture")
        sleep(1000)
    }
    // openInZathura(output)
}