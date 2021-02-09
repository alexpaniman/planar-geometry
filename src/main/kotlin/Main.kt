import compiler.Compiler
import latex.compileTikZ
import lexer.Lexer
import parser.Parser
import tikz.TikZ
import java.io.File
import java.lang.Thread.sleep

fun openInZathura(file: File) {
    val runtime = Runtime.getRuntime()
    runtime.exec("zathura ${file.absolutePath}")
}

fun main() {
    val file = File("src/main/resources/example")
    val lexer = Lexer(file)
    val tokens = lexer.tokenize()
    val objects = Parser().parse(tokens)
    val tikz = Compiler().compile(objects)

    val output = compileTikZ(tikz)
    sleep(1000)
    openInZathura(output)
}