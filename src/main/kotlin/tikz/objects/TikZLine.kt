package tikz.objects

import objects.point.XYPoint
import tikz.TikZObject

data class TikZLine(private val p1: XYPoint, private val p2: XYPoint): TikZObject() {
    override fun tikzify() = """
        \draw (${p1.x}, ${p1.y}) -- (${p2.x}, ${p2.y});
    """.trimIndent()
}