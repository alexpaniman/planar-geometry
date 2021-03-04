package objects.style.polygon

import objects.style.Style
import objects.polygon.Polygon
import tikz.TikZ

object PointsPolygonStyle : Style<Polygon> {
    override fun draw(obj: Polygon, tikz: TikZ) {
        val definedPoints = obj.points

        var previous = definedPoints.last()
        for (current in definedPoints) {
            val segment = Polygon(listOf(previous, current))

            val definedPrevious = previous.define()
            val definedCurrent = current.define()

            tikz.draw(segment, """
                \draw (${definedPrevious.x}, ${definedPrevious.y}) --
                      (${definedCurrent.x}, ${definedCurrent.y});""".trimIndent())

            current.draw(tikz)

            previous = current
        }

        obj.areaContainer.objects.forEach { it.draw(tikz) }
    }
}