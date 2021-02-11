package objects

import tikz.TikZ

object InvisibleStyle : Style<Any?> {
    override fun draw(obj: Any?, tikz: TikZ) {
        // Do nothing
    }
}