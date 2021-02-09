package objects.polygon

import objects.PlanarObject
import objects.point.Point

class PointsPolygon(private vararg val points: Point) : PlanarObject {
    override fun define() = XYPolygon(*points)

    override fun corePoints() = points
        .map { it.corePoints() }
        .flatten()
}