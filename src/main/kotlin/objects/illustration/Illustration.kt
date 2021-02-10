package objects.illustration

import objects.PlanarObject
import objects.XYPlanarObject
import objects.circle.Circle
import objects.circle.XYCircle
import objects.point.AnyPoint
import objects.point.XYPoint
import util.random
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Illustration : PlanarObject {
    companion object {
        @JvmStatic
        val BEST_LENGTH = 1.0..5.0

        @JvmStatic
        val BEST_RATIO = 1.0..1.5
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

    override fun setup(random: Random, circle: XYCircle) {
        val areas = (0..objects.size).map {
            val radius = BEST_LENGTH.random(random)
            val rotate = (0.0..2 * PI)
                .random(random)

            XYPoint(
                "object-point-[$it]",
                radius * cos(rotate),
                radius * sin(rotate)
            )
        }.map { XYCircle(it, 0.01) } // TODO Adjustable

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
                    area.radius += 0.01 // TODO Adjustable
                else if (maxRadius / area.radius !in BEST_RATIO) {
                    areas.forEach { it.radius = 0.01 } // TODO Adjustable

                    val radius = BEST_LENGTH.random(random)
                    val rotate = (0.0..2 * PI)
                        .random(random)

                    area.center.moveTo(
                        radius * cos(rotate),
                        radius * sin(rotate)
                    )
                }

                touching
            }.all { it } // TODO Attempt overflow

            if (finished)
                break
        }


//        for (steady in objectPoints) {
//            val closest = objectPoints
//                .filter { it != steady }
//                .minByOrNull {
//                    it.distanceTo(steady)
//                } ?: error("Not enough points!")
//
//            if (closest.distanceTo(steady) > BEST_LENGTH.endInclusive) {
//                val distanceNew = BEST_LENGTH
//                    .random(random)
//
//                closest.moveAlongLine(steady, distanceNew)
//            }
//
//            for (moving in objectPoints) {
//                if (moving == steady)
//                    continue
//
//                if (moving.distanceTo(steady) < 2 * BEST_LENGTH.start) {
//                    val distanceNew = BEST_LENGTH
//                        .random(random)
//
//                    moving.moveAlongLine(steady, distanceNew)
//                }
//            }
//        }
//
//        val circles = objectPoints.map {
//            XYCircle(it,0.1) // TODO Adjustable default radius
//        }
//
//        var grew: Boolean; do {
//            grew = false
//            for (current in circles) {
//                val colliding = circles
//                    .filter { it != current }
//                    .map {
//                        val distance = it.center.distanceTo(current.center)
//                        distance <= current.radius + it.radius
//                    }.any { it }
//
//                if (!colliding) {
//                    current.radius += 0.01 // TODO Adjustable
//                    grew = true
//                }
//            }
//        } while (grew)
//
//        for (current in circles)
//            current.radius *= 0.95 // TODO Adjustable
//
//        for ((obj, current) in objects.zip(circles))
//            obj.setup(random, current)

        // TODO This block is just for testing
        objects.clear()
        for (current in areas) {
            val actualCenter = AnyPoint(('A'..'Z').random().toString())
                .also { it.pointXY.moveTo(current.center.x, current.center.y) }

            val sidePoint = AnyPoint(('A'..'Z').random().toString())
                .also {
                    val rotate = (0.0..2 * PI).random(random)

                    it.pointXY.moveTo(
                        current.radius * cos(rotate) + current.center.x,
                        current.radius * sin(rotate) + current.center.y
                    )
                }

            Circle(actualCenter, sidePoint)
                .also { objects.add(it) }
        }
    }
}