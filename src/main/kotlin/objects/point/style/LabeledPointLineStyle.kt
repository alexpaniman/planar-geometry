package objects.point.style

import objects.style.Style
import objects.label.XYLabel
import objects.line.Line
import objects.point.Point
import objects.point.XYPoint
import tikz.TikZ
import kotlin.math.pow
import kotlin.math.sqrt

class LabeledPointLineStyle(private val text: String, private val line: Line): Style<Point> {
    override fun draw(obj: Point, tikz: TikZ) {
        val defPoint = obj.define()

        val defFrom = line.from.define()
        val defTo = line.to.define()

        val k = (defTo.x - defFrom.x) / (defTo.y - defFrom.y)
        val normal = XYPoint(
            1 / sqrt(k.pow(2) + 1),
            1 / sqrt((1 / k).pow(2) + 1)
        ).also {
            it.normalize()
            it.multiply(0.3)
        } // TODO Adjustable

        val position = normal.also {
            it.add(defPoint)
        }

        val label = XYLabel(text, position) // TODO Probably add label style

        tikz.draw(label, "\\node at (${position.x}, ${position.y}) { $text };")

        PointStyle.draw(obj, tikz)
    }
}