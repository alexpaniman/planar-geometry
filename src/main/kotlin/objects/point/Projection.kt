package objects.point

import objects.line.Line
import objects.line.XYLine

class Projection(val point: Point, val line: Line): Point() {
    override fun define(): XYPoint {
        val definedPoint = point.define()
        val definedLine = line.define()

        val perpendicular = XYLine.withTangent(definedLine.normal, definedPoint)
        return perpendicular.intersect(definedLine)!!
    }

    override fun corePoints(): List<MovablePoint> = point.corePoints() + line.corePoints()
}