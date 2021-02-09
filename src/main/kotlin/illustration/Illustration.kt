package illustration

import objects.PlanarObject
import objects.point.Point
import kotlin.random.Random

class Illustration(private val random: Random) {
    companion object {
        @JvmStatic
        val BEST_LENGTH = 2.0..10.0
    }

    val objects: MutableList<PlanarObject> = mutableListOf()
    val defined: MutableList<Point> = mutableListOf()

    fun ClosedFloatingPointRange<Double>.localRandom() =
        random.nextDouble(this.start, this.endInclusive)

    fun <T> Collection<T>.localRandom() = this.random(random)
}