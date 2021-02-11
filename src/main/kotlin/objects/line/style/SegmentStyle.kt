package objects.line.style

import objects.Style
import objects.line.Segment
import objects.line.XYSegment
import tikz.TikZ

object SegmentStyle: Style<Segment> {
    override fun draw(obj: Segment, tikz: TikZ) {
        obj.p1.draw(tikz)
        obj.p2.draw(tikz)

        tikz.draw(obj, """
            \draw (${obj.p1.define().x}, ${obj.p1.define().y}) --
                  (${obj.p2.define().x}, ${obj.p2.define().y});
            """.trimIndent())
    }
}