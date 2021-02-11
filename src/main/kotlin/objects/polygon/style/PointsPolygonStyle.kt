package objects.polygon.style

import objects.Style
import objects.line.Segment
import objects.line.style.SegmentStyle
import objects.polygon.PointsPolygon
import tikz.TikZ

object PointsPolygonStyle : Style<PointsPolygon> {
    override fun draw(obj: PointsPolygon, tikz: TikZ) {
        val definedPoints = obj.points

        var previous = definedPoints.last()
        for (current in definedPoints) {
            Segment(previous, current)
                .also { it.applyStyle(SegmentStyle) }
                .draw(tikz)

            previous = current
        }
    }
}