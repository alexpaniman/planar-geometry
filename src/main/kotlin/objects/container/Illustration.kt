package objects.container

import objects.PlanarObject
import objects.circle.XYCircle
import tikz.TikZ
import kotlin.random.Random

// Top-level container used to represent image as a whole
class Illustration(private val objects: MutableList<PlanarObject<*>>) {
    private val container = CircularContainer(1.5, 0.01, 0.01, objects.toMutableList()) {
        false // Top-level container has no bounds
    } // Delegate positioning objects to circular container

    init {
        container.objects.removeIf { it.passive }
    }

    fun setup(area: XYCircle, entropy: Random) = container.setup(area, entropy)
    fun draw(tikZ: TikZ) = objects
        .forEach { it.draw(tikZ) }
}