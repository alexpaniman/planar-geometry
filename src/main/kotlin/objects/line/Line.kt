package objects.line

import illustration.Illustration
import objects.PlanarObject
import objects.point.Point

class Line(private val p1: Point, private val p2: Point) : PlanarObject() {
    override val isDefined: Boolean
        get() = p1.isDefined && p2.isDefined

    override fun define(illustration: Illustration) = XYLine(
        p1.define(illustration),
        p2.define(illustration)
    )
}