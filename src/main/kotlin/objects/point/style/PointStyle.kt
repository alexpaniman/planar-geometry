package objects.point.style

import objects.style.Style
import objects.point.Point
import tikz.TikZ

object PointStyle: Style<Point> {
    override fun draw(obj: Point, tikz: TikZ) {
        tikz.draw(obj, "\\draw[fill] (${obj.define().x}, ${obj.define().y}) circle [radius=.02];")
    }
}