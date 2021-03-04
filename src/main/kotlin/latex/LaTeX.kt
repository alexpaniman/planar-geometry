package latex

import java.io.File

private fun wrapInLaTeX(tikz: String) = """
        | \documentclass[crop, tikz, margin={20pt 40pt 20pt 20pt}]{standalone}
        
        | \begin{document}
        |   \begin{tikzpicture}
        | ${tikz.lines().joinToString("\n") { "    $it" }}
        |   \end{tikzpicture}
        | \end{document}""".trimMargin("| ")

private fun compileTeX(program: String, fileTeX: File? = null): File {
    val tempFile = fileTeX ?: File
        .createTempFile("tikzpicture", ".tex")

    val targetFile = File("${tempFile
        .absolutePath
        .removeSuffix(".tex")}.pdf")

    tempFile.writeText(program)

    val runtime = Runtime.getRuntime()
    val command = "pdflatex -output-directory ${targetFile.parentFile!!.absolutePath} ${tempFile.absolutePath}"
    runtime.exec(command)

    return targetFile
}

fun compileTikZ(tikz: String, name: String): File {
    val program = wrapInLaTeX(tikz)
    val file = File("/tmp/$name.tex")
    return compileTeX(program, file)
}
