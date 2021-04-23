package objects.line

import objects.circle.XYCircle
import objects.point.XYPoint
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class XYLine(val from: XYPoint, val to: XYPoint) {
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

    val tangent get() = (from.y - to.y) / (from.x - to.x)
    val normal get() = - 1 / tangent
    val y0 get() = to.y - tangent * to.x

    fun intersect(other: XYLine): XYPoint? {
        if (from.x == to.x && other.from.x == other.to.x)
            return null

        if (tangent == other.tangent)
            return null

        if (from.x == to.x)
            return XYPoint(from.x, other.tangent * from.x + other.y0)

        if (other.from.y == other.to.y)
            return XYPoint(from.x, tangent * other.from.x + y0)

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
            else -> {
                val newLine = withTangent(normal, circle.center)
                val current = intersect(newLine)!!

                val radiusVector = current
                    .also {
                        it.subtract(circle.center)
                        it.normalize()
                        val length = this.distanceTo(circle.center)
                        it.multiply(length)
                    }

                val verticalVector = (from - to)
                    .also {
                        it.normalize()
                        val length = sqrt(circle.radius.pow(2.0) - radiusVector.len.pow(2.0))
                        it.multiply(length)
                    }

//                DEBUG_TIKZ.draw(Math.random(), "\\draw[fill] (${circle.center.x}, ${circle.center.y}) circle[radius=0.05];")
//
//                val rv = radiusVector + circle.center
//                val vv = verticalVector + rv
//                DEBUG_TIKZ.draw(Math.random(), "\\draw[thick, <-] (${vv.x}, ${vv.y}) -- (${rv.x}, ${rv.y});")
//
//                DEBUG_TIKZ.draw(Math.random(), "\\draw[thick, <-] (${rv.x}, ${rv.y}) -- (${circle.center.x}, ${circle.center.y});")

                // DEBUG_TIKZ.draw(Math.random(), "\\draw[thick, ->] (${this.from.x}, ${this.from.y}) -- (${this.to.x}, ${this.to.y});")

                listOf(circle.center + radiusVector + verticalVector, circle.center + radiusVector - verticalVector)
            }
        }
    }
}