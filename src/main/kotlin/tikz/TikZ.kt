package tikz

import objects.point.XYPoint
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
        objects.add(
            TikZNode(('A'..'Z').random().toString(), point) // TODO Randomness
        )
    }
}