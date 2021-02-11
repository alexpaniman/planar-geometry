package objects.circle.style

import objects.Style
import objects.circle.Circle
import tikz.TikZ

object CircleStyle: Style<Circle> {
    override fun draw(obj: Circle, tikz: TikZ) {
        val definedCircle = obj.define()
        val center = definedCircle.center
        val radius = definedCircle.radius

        tikz.draw(obj, "\\draw (${center.x}, ${center.y}) [radius = $radius];")
    }
}