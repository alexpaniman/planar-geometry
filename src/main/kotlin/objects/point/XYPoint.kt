package objects.point

import kotlin.math.*

data class XYPoint(var x: Double = 0.0, var y: Double = 0.0) {
    private fun distanceTo(x: Double, y: Double) = sqrt((this.x - x).pow(2) + (this.y - y).pow(2))

    infix fun angleWith(other: XYPoint): Double = acos((x * other.x + y * other.y) / (len * other.len))

    infix fun rotatedBy(angle: Double) = XYPoint(x * cos(angle) - y * sin(angle), x * sin(angle) + y * cos(angle))

    infix fun distanceTo(other: XYPoint) = distanceTo(other.x, other.y)

    fun moveTo(x: Double, y: Double) {
        this.x = x
        this.y = y
    }

    fun moveTo(other: XYPoint) = moveTo(other.x, other.y)

    fun shift(x: Double, y: Double) {
        this.x += x
        this.y += y
    }

    fun shifted(x: Double, y: Double) =
        copy().also { it.shift(x, y) }

    val len get() = distanceTo(0.0, 0.0)

    fun normalize() {
        val length = distanceTo(0.0, 0.0)
        x /= length
        y /= length
    }

    fun add(other: XYPoint) = shift(other.x, other.y)

    fun subtract(other: XYPoint) =
        shift(- other.x, - other.y)

    fun multiply(k: Double) {
        x *= k
        y *= k
    }

    fun moveAlongLine(other: XYPoint, newDistance: Double) {
        val oldDistance = distanceTo(other)
        val k = newDistance / oldDistance

        this.x += k * (other.x - this.x)
        this.y += k * (other.y - this.y)
    }

    operator fun plus(other: XYPoint) = copy()
        .apply { add(other) }

    operator fun minus(other: XYPoint) = copy()
        .apply { subtract(other) }

    operator fun times(num: Double) = copy()
        .apply { multiply(num) }

    operator fun div(num: Double) = copy()
        .apply { multiply(1.0 / num) }
}