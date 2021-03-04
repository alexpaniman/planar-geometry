package objects.style.point.labeled

import objects.point.Point
import objects.point.XYPoint
import objects.polygon.Polygon
import objects.style.Style
import objects.style.point.PointStyle
import tikz.TikZ
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class LabeledPointPolygonStyle(private val text: String, private val polygon: Polygon) : Style<Point> {
    override fun draw(obj: Point, tikz: TikZ) {
        val center = XYPoint()
        for (point in polygon.points) {
            val definedPoint = point.define()

            center.shift(
                definedPoint.x,
                definedPoint.y
            )
        }

        center.x /= polygon.points.size
        center.y /= polygon.points.size

        val definedPoint = obj.define()
        val vector = XYPoint(
            definedPoint.x - center.x,
            definedPoint.y - center.y
        )

        val zero = XYPoint(0.0, 0.0)

        val length = vector.distanceTo(zero)
        vector.x /= length
        vector.y /= length

        val rotate = atan2(vector.y, vector.x)
        val radius = 0.3 // TODO Adjustable

        val position = XYPoint(
            radius * cos(rotate) + definedPoint.x,
            radius * sin(rotate) + definedPoint.y
        )
        val label = XYPointLabel(text, position) // TODO Probably add label style

        tikz.draw(label, "\\node at (${position.x}, ${position.y}) { $text };")

        PointStyle.draw(obj, tikz)
    }
}