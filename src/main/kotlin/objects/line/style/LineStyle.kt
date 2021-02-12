package objects.line.style

import objects.style.Style
import objects.line.Line
import objects.point.XYPoint
import tikz.TikZ

object LineStyle: Style<Line> {
    override fun draw(obj: Line, tikz: TikZ) {
        val definedFrom = obj.from.define()
        val definedTo = obj.to.define()

        val newDistance = definedFrom.distanceTo(definedTo) * 1.3

        val beforeFrom = XYPoint(
            definedFrom.x,
            definedFrom.y
        ).also { it.moveAlongLine(definedTo, newDistance) } // TODO Adjustable

        val beforeTo = XYPoint(
            definedTo.x,
            definedTo.y
        ).also { it.moveAlongLine(definedFrom, newDistance) } // TODO Adjustable

        tikz.draw(obj, """
            \draw[help lines] (${ beforeFrom.x}, ${ beforeFrom.y}) --
                              (${definedFrom.x}, ${definedFrom.y});
            \draw[help lines] (${ beforeTo.x}, ${ beforeTo.y}) --
                              (${definedTo.x}, ${definedTo.y});
            \draw (${definedFrom.x}, ${definedFrom.y}) --
                  (${definedTo.x}, ${definedTo.y});
            """.trimIndent())

        obj.from.draw(tikz)
        obj.to.draw(tikz)
    }
}