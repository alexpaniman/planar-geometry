package objects.style.point.labeled

import objects.point.Point
import objects.point.XYPoint
import objects.style.Style
import objects.style.point.PointStyle
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
        val label = XYPointLabel(text, position) // TODO Probably add label style

        tikz.draw(label, "\\node at (${position.x}, ${position.y}) { ${text.mathMode} };")

        PointStyle.draw(obj, tikz)
    }
}