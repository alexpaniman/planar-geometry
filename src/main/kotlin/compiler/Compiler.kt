package compiler

import illustration.Illustration
import objects.point.Point
import java.lang.StringBuilder

class Compiler {
    fun compile(illustration: Illustration): String {
        val points = mutableListOf<Point>()

        for (obj in objects)
            when(obj) {
                is Triangle -> points.addAll(obj.points)
                is Line -> points.addAll(obj.points)
                is Point -> points.add(obj)
            }

        val builder = StringBuilder()

        val map = mutableMapOf<Point, ActualPoint>()
        for (point in points) {
            map[point] = ActualPoint(
                (0..10000).random() / 100f,
                (0..10000).random() / 100f,
            )

            builder.appendLine("\\node at (${map[point]!!.x}, ${map[point]!!.y}) { \\textbullet };")
        }

        fun point(point: Point) = "(${map[point]!!.x}, ${map[point]!!.y})"

        for (obj in objects) {
            if (obj is Triangle)
                builder.appendLine("\\draw ${point(obj.points[0])} -- ${point(obj.points[1])} -- ${point(obj.points[2])} -- cycle;")
        }

        return builder.toString()
    }
}