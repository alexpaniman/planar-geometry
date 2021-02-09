package tikz.objects

import objects.point.XYPoint
import tikz.TikZObject

data class TikZNode(private val name: String, private val point: XYPoint): TikZObject() {
    override fun tikzify() = """
        \node[above right] ($name) at (${point.x}, ${point.y}) { $name };
        \draw[fill] (${point.x}, ${point.y}) circle [radius=.02];
    """.trimIndent() // TODO Change radius
}