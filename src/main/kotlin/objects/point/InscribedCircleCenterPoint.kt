package objects.point

import objects.line.Line
import objects.line.XYLine

class InscribedCircleCenterPoint(val points: List<Point>): Point() {
    override fun define(): XYPoint {
        assert(points.size == 3)
        val (a, b, c) = points

        val bisectorBCPoint = AngleSector(1.0 / 2.0, a, Line(b, c)).define()
        val bisectorACPoint = AngleSector(1.0 / 2.0, b, Line(a, c)).define()

        val bisectorBC = XYLine(a.define(), bisectorBCPoint)
        val bisectorAC = XYLine(b.define(), bisectorACPoint)

        return bisectorAC.intersect(bisectorBC)!!
    }

    override fun corePoints(): List<MovablePoint> =
        points.map { it.corePoints() }.flatten()
}