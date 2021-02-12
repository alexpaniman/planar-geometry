package objects.label

import objects.style.Style
import tikz.TikZ

class LabeledStyle<XY>(private val label: XYLabel, private val objectStyle: Style<XY>): Style<XY> {
    override fun draw(obj: XY, tikz: TikZ) {
        objectStyle.draw(obj, tikz)
        tikz.draw(label, "\\node at (${label.point.x}) { ${label.text} };")
    }
}