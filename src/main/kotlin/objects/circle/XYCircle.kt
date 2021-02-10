package objects.circle

import objects.XYPlanarObject
import objects.point.XYPoint
import tikz.TikZ

data class XYCircle(val center: XYPoint, var radius: Double) : XYPlanarObject {
    override fun draw(tikz: TikZ) {
        center.draw(tikz)
        tikz.drawCircle(center, radius)
    }
}