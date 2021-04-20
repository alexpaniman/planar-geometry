package objects.point

import objects.line.Line

class IntersectionOfLines(private val l0: Line, private val l1: Line): Point() {
    override fun define() = l0.define()
            .intersect(l1.define()) ?: error("Lines do not intersect!")

    override fun corePoints(): List<MovablePoint> =
        l0.corePoints() + l1.corePoints()
}