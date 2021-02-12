package objects.line

import objects.PlanarObject
import objects.circle.XYCircle
import objects.point.Point
import util.random
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Line(val from: Point, val to: Point) : PlanarObject<XYLine>() {
    override fun define() = XYLine(from.define(), to.define())
    override fun corePoints() = from.corePoints() + to.corePoints()

    override fun setup(random: Random, circle: XYCircle) {
        val points = corePoints()

        val part = 2.0 * PI / points.size
        for ((index, point) in points.withIndex()) {
            val rotate = ((index + 0.15) * part..(index + 1 - 0.15) * part).random(random)
            val radius = (circle.radius * 0.6..0.9 * circle.radius) // TODO Adjustable
                .random(random)

            val center = circle.center

            point.define().moveTo(
                center.x + radius * cos(rotate),
                center.y + radius * sin(rotate)
            ) // TODO What about defined points?
        }
    }
}