package objects.polygon.style

import objects.style.Style
import objects.polygon.PointsPolygon
import tikz.TikZ

object PointsPolygonStyle : Style<PointsPolygon> {
    override fun draw(obj: PointsPolygon, tikz: TikZ) {
        val definedPoints = obj.points

        var previous = definedPoints.last()
        for (current in definedPoints) {
            val segment = PointsPolygon(listOf(previous, current))

            val definedPrevious = previous.define()
            val definedCurrent = current.define()

            tikz.draw(segment, """
                \draw (${definedPrevious.x}, ${definedPrevious.y}) --
                      (${definedCurrent.x}, ${definedCurrent.y});""".trimIndent())

            current.draw(tikz)

            previous = current
        }
    }
}