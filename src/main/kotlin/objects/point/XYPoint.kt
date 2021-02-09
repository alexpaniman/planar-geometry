package objects.point

import objects.XYPlanarObject
import objects.line.XYLine
import kotlin.math.pow
import kotlin.math.sqrt

data class XYPoint(val name: String, var x: Double, var y: Double) : XYPlanarObject() {
    fun distanceTo(other: XYPoint) = sqrt((x - other.x).pow(2) + (y - other.y).pow(2))

    fun moveAlongLine(other: XYPoint, newDistance: Double) {
        val oldDistance = distanceTo(other)
        val k = newDistance / oldDistance

        x += k * (other.x - x)
        y += k * (other.y - y)
    }

    override fun drawTikZ() = """
        \node ($name) at ($x, $y) { $name };
    """.trimIndent()

}