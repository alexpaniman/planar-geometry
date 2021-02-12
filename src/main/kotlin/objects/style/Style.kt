package objects.style

import tikz.TikZ

interface Style<T> {
    fun draw(obj: T, tikz: TikZ)
}