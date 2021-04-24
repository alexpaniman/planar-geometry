package objects.point

import objects.circle.Circle
import objects.circle.XYCircle

class TangentPoint(private val num: Int, val point: Point, val circle: Circle): Point() {
    override fun define(): XYPoint {
        val definedPoint = point.define()
        val definedCircle = circle.define()

        val middle = (definedPoint + definedCircle.center) / 2.0
        val secondCircle = XYCircle(middle, middle distanceTo definedPoint)

        return secondCircle.intersect(definedCircle)[num]
    }

    override fun corePoints(): List<MovablePoint> = point.corePoints() + circle.corePoints()
}