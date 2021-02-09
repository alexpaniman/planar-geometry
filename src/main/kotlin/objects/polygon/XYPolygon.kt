package objects.polygon

import objects.XYPlanarObject
import objects.point.Point
import tikz.TikZ

class XYPolygon(private vararg val points: Point): XYPlanarObject {
    override fun draw(tikz: TikZ) {
        var previous = points.last().define()

        for (point in points) {
            val current = point
                .define()
                .also { it.draw(tikz) }

            tikz.drawLine(previous, current)
            previous = current
        }
    }
}