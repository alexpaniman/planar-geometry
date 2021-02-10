package objects.polygon

import objects.PlanarObject
import objects.circle.Circle
import objects.circle.XYCircle
import objects.illustration.Illustration.Companion.BEST_LENGTH
import objects.point.Point
import util.random
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class PointsPolygon(private vararg val points: Point) : PlanarObject {
    override fun define() = XYPolygon(*points)

    override fun corePoints() = points
        .map { it.corePoints() }
        .flatten()

    override fun setup(random: Random, circle: XYCircle) {
        // TODO write it
    }
}