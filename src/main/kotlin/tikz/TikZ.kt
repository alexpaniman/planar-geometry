package tikz

import objects.point.XYPoint
import tikz.objects.TikZCircle
import tikz.objects.TikZLine
import tikz.objects.TikZNode

class TikZ {
    private val objects: MutableSet<TikZObject> = mutableSetOf()

    fun tikzify() = buildString {
        for (obj in objects)
            append(obj.tikzify())
    }

    fun drawLine(p1: XYPoint, p2: XYPoint) {
        objects.add(TikZLine(p1, p2))
    }

    fun drawPoint(point: XYPoint) {
        val tikzNode = TikZNode(point.name, point)

        objects.add(tikzNode)
    }

    fun drawCircle(center: XYPoint, radius: Double) {
        objects.add(TikZCircle(center, radius))
    }
}