package objects.illustration.style

import objects.style.Style
import objects.illustration.Illustration
import tikz.TikZ

object IllustrationStyle: Style<Illustration> {
    override fun draw(obj: Illustration, tikz: TikZ) =
        obj.objects.forEach { it.draw(tikz) }
}