package objects.illustration

import objects.PlanarObject
import objects.XYPlanarObject
import util.random
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Illustration : PlanarObject {
    companion object {
        @JvmStatic
        val BEST_LENGTH = 2.0..10.0
        val SIZE = 5.0..70.0
    }

    val objects: MutableList<PlanarObject> = mutableListOf()

    override fun corePoints() = objects
        .map { it.corePoints() }
        .flatten()
        .distinct()

    override fun define(): XYPlanarObject {
        val definedObjects = objects
            .map { it.define() }
            .toTypedArray()

        return XYIllustration(*definedObjects)
    }

    override fun setup(random: Random) {
        val points = corePoints()

        for (point in points) {
            val radius = SIZE.random(random)
            val rotate = (0.0..2 * PI)
                .random(random)

            point.pointXY.moveTo(
                radius * cos(rotate),
                radius * sin(rotate)
            )
        }

        for (steady in points) for (moving in points) {
            if (moving == steady)
                continue

            val distance = moving.pointXY
                .distanceTo(steady.pointXY)

            if (distance in BEST_LENGTH)
                continue

            val distanceNew = BEST_LENGTH
                .random(random)
            moving.pointXY.moveAlongLine(steady.pointXY, distanceNew)
        }

        for (obj in objects)
            obj.setup(random)
    }
}