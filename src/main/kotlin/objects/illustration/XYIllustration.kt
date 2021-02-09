package objects.illustration

import objects.XYPlanarObject
import tikz.TikZ

class XYIllustration(private vararg val objects: XYPlanarObject): XYPlanarObject {
    override fun draw(tikz: TikZ) =
        objects.forEach { it.draw(tikz) }
}