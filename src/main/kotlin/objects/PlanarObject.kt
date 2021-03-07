package objects

import objects.circle.XYCircle
import objects.point.MovablePoint
import objects.style.InvisibleStyle
import objects.style.Style
import tikz.TikZ
import kotlin.random.Random

abstract class PlanarObject<XY> {
    var style: Style<PlanarObject<XY>>? = null

    abstract fun define(): XY
    abstract fun corePoints(): List<MovablePoint>

    open fun setup(area: XYCircle, entropy: Random) {
        // No setup by default
    }

    var passive = false
    fun softSetup(area: XYCircle, entropy: Random) {
        if (!passive)
            setup(area, entropy)
    }

    fun draw(tikz: TikZ) {
        style?.draw(this, tikz)
    }

    inline fun <reified T: PlanarObject<XY>> applyStyle(style: Style<T>?): T {
        if (style == null) {
            this.style = null
            return this as T
        }

        if (this.style != null)
            return this as T

        // Type system went crazy
        @Suppress("UNCHECKED_CAST")
        this.style = style as Style<PlanarObject<XY>>

        return this as T
    }

    fun hide() {
        style = null
    }
}