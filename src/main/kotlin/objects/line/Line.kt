package objects.line

import objects.PlanarObject
import objects.point.MovablePoint
import objects.point.Point

class Line(private val p1: Point, private val p2: Point) : PlanarObject {
    override fun define() = XYLine(p1.define(), p2.define())
    override fun corePoints(): List<MovablePoint> {
        TODO("Not yet implemented")
    }
}