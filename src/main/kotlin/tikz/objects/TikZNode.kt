package tikz.objects

import objects.point.XYPoint
import tikz.TikZObject

data class TikZNode(private val name: String, private val point: XYPoint): TikZObject() {
    override fun tikzify() = """
        \node ($name) at (${point.x}, ${point.y}) { $name };
        \draw ($name) circle [radius=1];
    """.trimIndent() // TODO Change radius
}