package objects

import tikz.TikZ

interface Style<T> {
    fun draw(obj: T, tikz: TikZ)
}