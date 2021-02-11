package objects.illustration

import objects.PlanarObject
import objects.circle.XYCircle
import objects.point.XYPoint
import util.random
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Illustration : PlanarObject<XYIllustration>() {
    companion object {
        @JvmStatic
        val BEST_LENGTH = 1.0..5.0

        @JvmStatic
        val BEST_RATIO = 1.0..1.5
    }

    val objects: MutableList<PlanarObject<*>> = mutableListOf()

    override fun corePoints() = objects
        .map { it.corePoints() }
        .flatten()
        .distinct()

    override fun define(): XYIllustration { // TODO It's useless!
        val definedObjects = objects
            .map { it.define() }

        return XYIllustration(definedObjects)
    }

    private val firstRadius = .01
    private val growFactor  = .01

    override fun setup(random: Random, circle: XYCircle) {
        fun randomizePoint(pointXY: XYPoint) {
            val radius = BEST_LENGTH.random(random)
            val rotate = (0.0..2 * PI)
                .random(random)

            pointXY.moveTo(
                radius * cos(rotate),
                radius * sin(rotate)
            )
        }

        val areas = (1..objects.size)
            .map {
                val center = XYPoint()
                randomizePoint(center)

                XYCircle(center, firstRadius)
            }

        while(true) {
            val maxRadius = areas.maxOf { it.radius }

            val finished = areas.map { area ->
                val touching = areas
                    .filter { it != area }
                    .map { neighbor ->
                        val distance = neighbor.center
                            .distanceTo(area.center)

                        distance <= neighbor.radius + area.radius
                    }.any { it }

                if (!touching)
                    area.radius += growFactor
                else if (maxRadius / area.radius !in BEST_RATIO) {
                    for (current in areas)
                        current.radius = firstRadius

                    randomizePoint(area.center)
                }

                touching
            }.all { it } // TODO Attempt overflow

            if (finished)
                break
        }

        for ((obj, area) in objects.zip(areas))
            obj.setup(random, area)

        // TODO This block is just for testing
//        for (current in areas) {
//            val actualCenter = AnyPoint(('A'..'Z').random().toString())
//                .also { it.pointXY.moveTo(current.center.x, current.center.y) }
//
//            val sidePoint = AnyPoint(('A'..'Z').random().toString())
//                .also {
//                    val rotate = (0.0..2 * PI).random(random)
//
//                    it.pointXY.moveTo(
//                        current.radius * cos(rotate) + current.center.x,
//                        current.radius * sin(rotate) + current.center.y
//                    )
//                }
//
//            Circle(actualCenter, sidePoint)
//                .also { objects.add(it) }
//        }
    }
}