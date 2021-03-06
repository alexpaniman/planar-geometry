package objects.polygon

import objects.PlanarObject
import objects.circle.XYCircle
import objects.container.AreaContainer
import objects.container.CircularContainer
import objects.container.SurfaceContainer
import objects.line.XYLine
import objects.point.Point
import objects.point.XYPoint
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

open class Polygon(val points: List<Point>): PlanarObject<XYPolygon>(), AreaContainer, SurfaceContainer {
    private val touches: (XYCircle) -> Boolean = touches@ {
        val segments = points.zip(points.drop(1) + points.first())

        for ((from, to) in segments) {
            val line = XYLine(from.define(), to.define())
            val distance = line.distanceTo(it.center)

            if (distance <= it.radius) // TODO Implement non-convex shapes
                return@touches true
        }

        return@touches  false
    }

    val areaContainer = CircularContainer(2.0, 0.0, 0.01, touches = touches)

    override fun addInObject(obj: PlanarObject<*>) {
        areaContainer.objects.add(obj)
    }

    override fun addOnObject(obj: PlanarObject<*>) = TODO()

    override fun define() = XYPolygon(points)

    override fun corePoints() = points
        .map { it.corePoints() }
        .flatten()

    override fun setup(area: XYCircle, entropy: Random) {
        val shift = entropy.nextDouble(- PI / 4.0, PI / 4.0)

        val part = 2.0 * PI / points.size
        for ((index, point) in points.withIndex()) {
            val rotate = entropy.nextDouble(
                (index + 0.3) * part,
                (index + 1 - 0.3) * part
            ) + shift

            val radius = entropy.nextDouble(
                area.radius * 0.6,
                0.9 * area.radius
            ) // TODO Adjustable

            val position = XYPoint(
                radius * cos(rotate) + area.center.x,
                radius * sin(rotate) + area.center.y
            )
            val circleRadius = area.radius - area.center.distanceTo(position)

            val circle = XYCircle(position, circleRadius)
            point.softSetup(circle, entropy)

//            DEBUG_TIKZ.draw(entropy.nextInt(), "\\draw (${position.x}, ${position.y}) circle [radius = ${circleRadius}];")
        }

        // Setup inner points
        area.radius = 0.0
        while (!touches(area))
            area.radius += 0.01 // Adjustable

//        DEBUG_TIKZ.draw(entropy.nextInt(), "\\draw (${area.center.x}, ${area.center.y}) circle [radius = ${area.radius}];")

        areaContainer.setup(area, entropy)
    }
}