package objects.circle

import objects.line.XYLine
import objects.point.XYPoint
import kotlin.math.pow

data class XYCircle(val center: XYPoint, var radius: Double) {
    fun intersects(other: XYCircle): Boolean {
        val distance = other.center
            .distanceTo(this.center)

        return distance <= other.radius + radius
    }

    fun touches(other: XYCircle): Boolean {
        val distance = other.center
            .distanceTo(this.center)

        return distance == other.radius + radius
    }

    fun intersect(other: XYCircle): List<XYPoint> {
        val dist = (center distanceTo other.center).pow(2.0)
        val diff = radius.pow(2.0) - other.radius.pow(2.0)

        val ratio = (dist + diff) / (dist - diff)

        val middle = (center + other.center * ratio) / (1 + ratio)
        val joined = XYLine(center, other.center)

        val crossingLine = XYLine.withTangent(joined.normal, middle)
        return crossingLine.intersect(this)
    }
}