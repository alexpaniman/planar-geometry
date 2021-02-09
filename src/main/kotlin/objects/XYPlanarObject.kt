package objects

import tikz.TikZ

interface XYPlanarObject {
    fun draw(tikz: TikZ)
}