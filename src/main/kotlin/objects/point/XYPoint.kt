package objects.point

import objects.XYPlanarObject
import objects.line.XYLine
import tikz.TikZ
import kotlin.math.pow
import kotlin.math.sqrt

data class XYPoint(val name: String, var x: Double = 0.0, var y: Double = 0.0) : XYPlanarObject {
    fun distanceTo(other: XYPoint) = sqrt((x - other.x).pow(2) + (y - other.y).pow(2))

    fun moveTo(x: Double, y: Double) {
        this.x = x
        this.y = y
    }

    fun moveAlongLine(other: XYPoint, newDistance: Double) {
        val oldDistance = distanceTo(other)
        val k = newDistance / oldDistance

        this.x += k * (other.x - this.x)
        this.y += k * (other.y - this.y)
    }

    override fun draw(tikz: TikZ) = tikz.drawPoint(this)
}