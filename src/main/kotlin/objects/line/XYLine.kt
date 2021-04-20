package objects.line

import objects.circle.XYCircle
import objects.point.XYPoint
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class XYLine(private val from: XYPoint, private val to: XYPoint) {
    companion object {
        @JvmStatic fun withTangent(k: Double, point: XYPoint) =
            XYLine(point, point.shifted(1.0, k))
    }

    fun distanceTo(point: XYPoint): Double {
        val diffX = to.x - from.x
        val diffY = to.y - from.y

        return abs(diffY * point.x - diffX * point.y - from.x * diffY + from.y * diffX) /
                sqrt(diffX.pow(2) + diffY.pow(2))
    }

    private val tangent
        get() = (from.y - to.y) / (from.x - to.x)

    private val normal
        get() = - 1 / tangent

    private val y0
        get() = to.y - tangent * to.x

    fun intersect(other: XYLine): XYPoint? {
        if (tangent == other.tangent)
            return null

        val x = (other.y0 - y0) / (tangent - other.tangent)
        val y = tangent * x + y0
        return XYPoint(x, y)
    }

    fun intersect(circle: XYCircle): List<XYPoint> {
        val distance = distanceTo(circle.center)
        return when {
            abs(distance - circle.radius) <= 1e-5 -> {
                val newLine = withTangent(normal, circle.center)
                listOf(intersect(newLine)!!)
            }
            distance > circle.radius -> listOf()
            else -> TODO()
        }
    }
}

