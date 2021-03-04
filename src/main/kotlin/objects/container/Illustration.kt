package objects.container

import objects.PlanarObject
import objects.circle.XYCircle
import tikz.TikZ
import kotlin.random.Random

// Top-level container used to represent image as a whole
class Illustration(objects: MutableList<PlanarObject<*>>) {
    private val container = CircularContainer(1.5, 0.01, 0.01, objects) {
        false // Top-level container has no bounds
    } // Delegate positioning objects to circular container

    fun setup(area: XYCircle, entropy: Random) = container.setup(area, entropy)
    fun draw(tikZ: TikZ) = container.objects
        .forEach { it.draw(tikZ) }
}