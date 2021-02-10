package objects.polygon

import objects.PlanarObject
import objects.circle.Circle
import objects.circle.XYCircle
import objects.point.Point
import objects.point.PointIn
import objects.point.PointOn
import kotlin.random.Random

abstract class Polygon : PlanarObject, PointIn, PointOn {
    private val pointsIn: MutableList<Point> = mutableListOf()
    private val pointsOn: MutableList<Point> = mutableListOf()

    abstract override fun define(): XYPolygon

    override fun addPointIn(point: Point) { pointsIn.add(point) }
    override fun addPointOn(point: Point) { pointsOn.add(point) }

    override fun setup(random: Random, circle: XYCircle) {
        // Set right points?

        // TODO Place points in triangle
        // val triangleXY = define()
    }
}