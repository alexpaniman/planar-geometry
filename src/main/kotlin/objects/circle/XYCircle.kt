package objects.circle

import objects.XYPlanarObject
import objects.point.XYPoint

data class XYCircle(val center: XYPoint, val radius: Double) : XYPlanarObject() {
    override fun drawTikZ() = """
        \draw (${center.name}) circle [radius=$radius];
    """.trimIndent()
}