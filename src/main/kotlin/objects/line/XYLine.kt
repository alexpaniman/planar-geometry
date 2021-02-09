package objects.line

import objects.XYPlanarObject
import objects.point.XYPoint
import tikz.TikZ

class XYLine(private val from: XYPoint, private val to: XYPoint) : XYPlanarObject {
    override fun draw(tikz: TikZ) {
        from.draw(tikz)
        to.draw(tikz)

        tikz.drawLine(from, to)
    }
}

