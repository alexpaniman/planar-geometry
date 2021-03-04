package objects.point

import kotlin.math.pow
import kotlin.math.sqrt

data class XYPoint(var x: Double = 0.0, var y: Double = 0.0) {
    fun distanceTo(x: Double, y: Double) = sqrt((this.x - x).pow(2) + (this.y - y).pow(2))

    fun distanceTo(other: XYPoint) = distanceTo(other.x, other.y)

    fun moveTo(x: Double, y: Double) {
        this.x = x
        this.y = y
    }

    fun moveTo(other: XYPoint) = moveTo(other.x, other.y)

    fun shift(x: Double, y: Double) {
        this.x += x
        this.y += y
    }

    fun vectorLength() = distanceTo(0.0, 0.0)

    fun normalize() {
        val length = distanceTo(0.0, 0.0)
        x /= length
        y /= length
    }

    fun add(other: XYPoint) = shift(other.x, other.y)

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
}