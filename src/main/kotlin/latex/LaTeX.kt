package latex

import java.io.File

private fun wrapInLaTeX(tikz: String) = """
        \documentclass[crop, tikz]{standalone}
        
        \begin{document}
        \begin{tikzpicture}
        ${tikz.lines().joinToString("\n") { "\t$it" }}
        \end{tikzpicture}
        \end{document}
    """.trimIndent()

private fun compileTeX(program: String): File {
    val tempFile = File.createTempFile("tikzpicture", ".tex")
    val targetFile = File("${tempFile.absolutePath.removeSuffix(".tex")}.pdf")

    tempFile.writeText(program)

    val runtime = Runtime.getRuntime()
    val command = "pdflatex -output-directory ${targetFile.parentFile!!.absolutePath} ${tempFile.absolutePath}"
    runtime.exec(command)

    return targetFile
}

fun compileTikZ(tikz: String): File {
    val program = wrapInLaTeX(tikz)
    return compileTeX(program)
}
