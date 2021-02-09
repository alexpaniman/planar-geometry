package parser

import lexer.Token
import lexer.TokenType.*
import objects.PlanarObject
import objects.circle.Circle
import objects.line.Line
import objects.point.AnyPoint
import objects.polygon.PointsPolygon

class Parser {
    private fun randomPoints(vararg names: String) = names
        .map { AnyPoint(it) }
        .toTypedArray()

    private fun instantiateObject(id: String, vararg names: String): PlanarObject {
        val points = randomPoints(*names)

        return when (id) {
            "triangle" -> PointsPolygon(*points)
            "line" -> Line(points[0], points[1])
            "point" -> AnyPoint(names.first())
            "circle" -> Circle(points[0], points[1])
            else -> error("No such object defined!")
        }
    }

    fun parse(tokens: List<Token>): List<PlanarObject> {
        val objects = mutableListOf<PlanarObject>()

        var i = 0; while(i in tokens.indices) {
            val token = tokens[i]

            when (token.type) {
                DEF -> {
                    val id = tokens[++ i]
                        .symbol ?: error("Unexpected token: ${tokens[i].type}")

                    if (tokens[++ i].type != LCB)
                        error("Unexpected token: ${tokens[i].type}")

                    val names = mutableListOf<String>()
                    while (tokens[++ i].type != RCB)
                        names.add(tokens[i].symbol ?: error("Unexpected token: ${tokens[i].type}"))

                    val obj = instantiateObject(id, *names.toTypedArray())
                    objects.add(obj)
                }

                else -> error("Unexpected token: ${token.type}")
            }

            ++ i
        }

        return objects
    }
}