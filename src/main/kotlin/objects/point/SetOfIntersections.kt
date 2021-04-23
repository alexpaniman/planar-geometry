package objects.point

import objects.PlanarObject
import objects.circle.Circle
import objects.line.XYLine
import objects.polygon.Polygon
import kotlin.math.floor

class SetOfIntersections(val num: Int, val objects: List<PlanarObject<*>>): Point() {
    private fun lineify(polygon: Polygon): XYLine {
        check(polygon.points.size == 2) {
            "Intersections for polygons aren't supported."
        }

        val (a, b) = polygon.points
            .map { it.define() }

        return XYLine(a, b)
    }

    override fun define(): XYPoint {
        val intersections: MutableList<XYPoint> = mutableListOf()

        for ((i, obj0) in objects.withIndex())
            for (j in (i + 1)..objects.lastIndex) {
                val obj1 = objects[j]

                val currentIntersections = when (obj0) {
                    is Polygon -> when (obj1) {
                        is Polygon -> {
                            val line0 = lineify(obj0)
                            val line1 = lineify(obj1)

                            val intersection = line0.intersect(line1)
                            if (intersection == null)
                                listOf()
                            else
                                listOf(intersection)
                        }

                        is Circle -> {
                            val line = lineify(obj0)
                            val circle = obj1.define()

                            line.intersect(circle)
                        }

                        else -> error("Intersections of objects of this kind aren't supported.")
                    }

                    is Circle -> when (obj1) {
                        is Polygon -> {
                            val circle = obj0.define()
                            val line = lineify(obj1)

                            line.intersect(circle)
                        }

                        is Circle -> {
                            val circle0 = obj0.define()
                            val circle1 = obj1.define()

                            circle1.intersect(circle0)
                        }

                        else -> error("Intersections of objects of this kind aren't supported.")
                    }

                    else -> error("Intersections of objects of this kind aren't supported.")
                }

                intersections += currentIntersections
            }

        return intersections.distinctBy { floor((it.x + 1e5 * it.y) * 1e5) }[num]
    }

    override fun corePoints(): List<MovablePoint> = TODO()
}