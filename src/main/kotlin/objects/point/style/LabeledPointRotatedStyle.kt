package objects.point.style

import objects.style.Style
import objects.label.XYLabel
import objects.point.Point
import objects.point.XYPoint
import tikz.TikZ
import kotlin.math.cos
import kotlin.math.sin

class LabeledPointRotatedStyle(private val text: String, private val angle: Double): Style<Point> {
    override fun draw(obj: Point, tikz: TikZ) {
        val radius = 0.3 // TODO Adjustable

        val definedPoint = obj.define()
        val position = XYPoint(
            radius * cos(angle) + definedPoint.x,
            radius * sin(angle) + definedPoint.y
        )
        val label = XYLabel(text, position) // TODO Probably add label style

        tikz.draw(label, "\\node at (${position.x}, ${position.y}) { $text };")

        PointStyle.draw(obj, tikz)
    }
}