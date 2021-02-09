package objects.triangle

import illustration.Illustration
import objects.PlanarObject
import objects.point.Point

data class ThreePointsTriangle(val p1: Point, val p2: Point, val p3: Point) : PlanarObject() {
    override val isDefined: Boolean
        get() = p1.isDefined && p2.isDefined && p3.isDefined

    override fun define(illustration: Illustration) = XYTriangle(
            p1.define(illustration),
            p2.define(illustration),
            p3.define(illustration)
        )
}