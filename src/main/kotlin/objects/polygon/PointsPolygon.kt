package objects.polygon

import objects.PlanarObject
import objects.circle.XYCircle
import objects.point.Point
import util.random
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class PointsPolygon(val points: List<Point>): PlanarObject<XYPolygon>() {
    override fun define() = XYPolygon(points)

    override fun corePoints() = points
        .map { it.corePoints() }
        .flatten()

    override fun setup(random: Random, circle: XYCircle) {
        val part = 2.0 * PI / points.size
        for ((index, point) in points.withIndex()) {
            val rotate = (index * part..(index + 1) * part).random(random)
            val radius = (circle.radius * 0.7..circle.radius) // TODO Adjustable
                .random(random)

            val center = circle.center

            point.define().moveTo(
                center.x + radius * cos(rotate),
                center.y + radius * sin(rotate)
            ) // TODO What about defined points?
        }
    }
}