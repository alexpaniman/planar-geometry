package objects.container

import DEBUG_TIKZ
import objects.PlanarObject
import objects.circle.XYCircle
import objects.point.XYPoint
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class CircularContainer(
    private val maxRatio: Double,
    private val growFactorBound: Double,
    private val growFactorArea: Double,

    // List of objects this container will control
    val objects: MutableList<PlanarObject<*>> = mutableListOf(),

    // This method should return true when passed circle touches object's boundaries
    private val touches: (XYCircle) -> Boolean
) {

    // Place point in new random location inside of circle
    private fun XYCircle.randomizePoint(point: XYPoint, entropy: Random) {
        val radius = entropy.nextDouble(0.0, radius)
        val rotate = entropy.nextDouble(0.0, 2 * PI)

        point.moveTo(
            radius * cos(rotate),
            radius * sin(rotate)
        )
    }

    // Generate list of random points inside of circle
    private fun XYCircle.generateRandomPoints(count: Int, entropy: Random) =
        List(count) {
            XYPoint().also {
                randomizePoint(it, entropy)
            }
        }

    fun setup(area: XYCircle, entropy: Random) {
//        DEBUG_TIKZ.draw(entropy.nextInt(), "\\draw (${area.center.x}, ${area.center.y}) circle [radius = ${area.radius}];")
        if (objects.size == 1)
        // Pass this object's area to the single child
            objects.first().softSetup(area, entropy)

        // If there are only 1 or 0 points then no balancing is needed
        if (objects.size <= 1)
            return

        val nestedAreas = area
            .generateRandomPoints(objects.size, entropy)
            .map { XYCircle(it, 0.0) }

        growing_generations@ do {
            val maxRadius = nestedAreas
                .maxOf { it.radius } // Find largest circle radius

            var finished = true

            // Check all areas
            for (currentArea in nestedAreas) {
                val touchesSide = touches(currentArea)
                val touchesArea = nestedAreas
                    .filter { it != currentArea }
                    .any { it.intersects(currentArea) }

                if (touchesSide || touchesArea) {
                    val ratio = maxRadius / currentArea.radius
                    if (ratio > maxRatio) {
                        // If area is too small but has no place to grow
                        // Then we need to place it in another point
                        area.randomizePoint(currentArea.center, entropy)

                        // Resize area we are searching points in
                        area.radius += growFactorBound

                        // And repeat whole growing process all over again
                        nestedAreas.forEach { it.radius = 0.0 }
                        finished = false
                        continue@growing_generations
                    }

                    // However, if current area has room to grow
                    // Then it should grow
                } else currentArea.radius += growFactorArea

                finished = finished && (touchesArea || touchesSide)
            }
        } while (!finished)

        for (currentArea in nestedAreas) {
            val point = currentArea.center
            DEBUG_TIKZ.draw(entropy.nextInt(), "\\draw (${point.x}, ${point.y}) circle [radius = ${currentArea.radius}];")
        }

        // Then we're gonna pass generated area to every object
        for ((currentObject, currentArea) in objects.zip(nestedAreas))
            currentObject.softSetup(currentArea, entropy)
    }
}