package objects.circle

import illustration.Illustration
import objects.PlanarObject
import objects.point.Point

class Circle(private val center: Point, private val side: Point): PlanarObject() {
    override val isDefined: Boolean
        get() = center.isDefined && side.isDefined

    override fun define(illustration: Illustration): XYCircle {
        val centerXY = center.define(illustration)

        val sideXY = side.define(illustration)
        val radius = centerXY.distanceTo(sideXY)

        return XYCircle(centerXY, radius)
    }
}