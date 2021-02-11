package objects

import objects.circle.XYCircle
import objects.point.MovablePoint
import tikz.TikZ
import kotlin.random.Random

abstract class PlanarObject<XY> {
    var style: Style<PlanarObject<XY>>? = null

    abstract fun define(): XY
    abstract fun corePoints(): List<MovablePoint>

    open fun setup(random: Random, circle: XYCircle) {
        // No setup by default
    }

    fun draw(tikz: TikZ) {
        style?.draw(this, tikz)
    }

    inline fun <reified T: PlanarObject<XY>> applyStyle(style: Style<T>): T {
        // Type system went crazy
        @Suppress("UNCHECKED_CAST")
        this.style = style as Style<PlanarObject<XY>>

        return this as T
    }
}