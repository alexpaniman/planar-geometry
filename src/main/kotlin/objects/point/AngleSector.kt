package objects.point

import objects.line.Line
import objects.line.XYLine

class AngleSector(private val ratio: Double, val point: Point, val line: Line): Point() {
    override fun define(): XYPoint {
        val definedLine  =  line.define()
        val definedPoint = point.define()

        val a = definedLine.to   - definedPoint
        val b = definedLine.from - definedPoint

        val angle = a angleWith b
        val delta = ratio * angle

        val rotatedA = a rotatedBy delta
        val rotatedB = a rotatedBy (- delta)

        val rotated = if (rotatedA angleWith b < rotatedB angleWith b)
            rotatedA
        else rotatedB

        val sector = XYLine(definedPoint, definedPoint + rotated)
        return sector.intersect(definedLine)!!
    }

    override fun corePoints(): List<MovablePoint> {
        TODO("Not yet implemented")
    }
}