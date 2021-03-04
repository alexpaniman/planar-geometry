package objects.line

import objects.point.XYPoint
import tikz.TikZ
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class XYLine(private val from: XYPoint, private val to: XYPoint) {
    fun distanceTo(point: XYPoint): Double {
        val diffX = to.x - from.x
        val diffY = to.y - from.y
        val distance = abs(diffY * point.x - diffX * point.y - from.x * diffY + from.y * diffX) /
                sqrt(diffX.pow(2) + diffY.pow(2))

        return distance
    }
}

