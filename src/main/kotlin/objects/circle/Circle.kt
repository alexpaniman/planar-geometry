package objects.circle

import objects.PlanarObject
import objects.point.Point

class Circle(private val center: Point, private val side: Point): PlanarObject<XYCircle>() { // TODO PointIn, PointOn, make it abstract
    override fun define(): XYCircle {
        val centerXY = center.define()

        val sideXY = side.define()
        val radius = centerXY.distanceTo(sideXY)

        return XYCircle(centerXY, radius)
    }

    override fun corePoints() = center.corePoints() + side.corePoints()
}