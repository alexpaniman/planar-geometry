import latex.compileTikZ
import objects.circle.XYCircle
import objects.container.Illustration
import objects.point.XYPoint
import parser.parse
import tikz.TikZ
import java.io.File
import kotlin.random.Random

fun openInZathura(file: File) {
    val runtime = Runtime.getRuntime()
    runtime.exec("zathura ${file.absolutePath}")
}

val DEBUG_TIKZ = TikZ()

fun main() {
    val inputText = File("src/main/resources/example")
        .readText()

    val objects = parse(inputText)
        .toMutableList()

    val illustration = Illustration(objects)

    val seed = System.currentTimeMillis() /* 1613245476814 */
    println("Seed: $seed\n")

    val random = Random(seed)

    val circle = XYCircle(XYPoint(0.0, 0.0), 5.0)
    illustration.setup(circle, random)

    illustration.draw(DEBUG_TIKZ)

    val tikzCode = DEBUG_TIKZ.tikzify()
    println(tikzCode)

    compileTikZ(tikzCode, "my-test-tikzpicture")
    // openInZathura(output)
}