package objects.style.point.labeled

import objects.line.Line
import objects.point.Point
import objects.point.XYPoint
import objects.polygon.Polygon
import objects.style.Style
import objects.style.point.PointStyle
import objects.style.polygon.PolygonStyle
import tikz.TikZ
import kotlin.math.pow
import kotlin.math.sqrt

class LabeledPointLineStyle(private val text: String, private val line: Line): Style<Point> {
    override fun draw(obj: Point, tikz: TikZ) {
        val defPoint = obj.define()

        val defFrom = line.from.define()
        val defTo = line.to.define()

        val position = XYPoint(
            - 1.0,
            (defTo.x - defFrom.x) / (defTo.y - defFrom.y)
        ).also {
            it.normalize()
            it.multiply(0.3)
            it.add(defPoint)
        }

        val label = XYPointLabel(text, position) // TODO Probably add label style

        tikz.draw(label, "\\node at (${position.x}, ${position.y}) { $text };")

        PointStyle.draw(obj, tikz)
    }
}