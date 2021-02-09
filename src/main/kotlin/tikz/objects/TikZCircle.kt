package tikz.objects

import objects.point.XYPoint
import tikz.TikZObject

class TikZCircle(private val center: XYPoint, private val radius: Double): TikZObject() {
    override fun tikzify() = """
        \draw (${center.x}, ${center.y}) circle [radius=$radius];
    """.trimIndent()
}