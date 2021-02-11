package objects.line

import objects.PlanarObject
import objects.point.MovablePoint
import objects.point.Point

class Segment(val p1: Point, val p2: Point) : PlanarObject<XYSegment>() {
    override fun define() = XYSegment(p1.define(), p2.define())
    override fun corePoints(): List<MovablePoint> {
        TODO("Not yet implemented")
    }
}