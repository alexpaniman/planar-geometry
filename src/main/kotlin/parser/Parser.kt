package parser

import lexer.Token
import lexer.TokenType.*
import objects.PlanarObject
import objects.circle.Circle
import objects.circle.style.CircleStyle
import objects.line.Line
import objects.line.style.LineStyle
import objects.point.AnyPoint
import objects.point.Point
import objects.point.style.LabeledPointLineStyle
import objects.point.style.LabeledPointPolygonStyle
import objects.point.style.LabeledPointRotatedStyle
import objects.point.style.PointStyle
import objects.polygon.PointsPolygon
import objects.polygon.style.PointsPolygonStyle
import kotlin.math.PI

class Parser {
    private val pointsMap: MutableMap<String, Point> = mutableMapOf()
    private fun initializePoints(vararg names: String) = names
        .map {
            pointsMap.computeIfAbsent(it) {
                AnyPoint().applyStyle(PointStyle)
            }
        }

    private fun instantiateObject(id: String, vararg names: String): PlanarObject<*> {
        val points = initializePoints(*names)

        return when (id) {
            "triangle", "segment", "polygon" -> {
                val polygon = PointsPolygon(points)
                    .applyStyle(PointsPolygonStyle)

                points.zip(names).forEach { (point, name) ->
                    val style = LabeledPointPolygonStyle(name, polygon)
                    point.applyStyle(style)
                }

                polygon
            }

            "line" -> {
                val line = Line(points[0], points[1])
                    .applyStyle(LineStyle)

                points.zip(names).forEach { (point, name) ->
                    val style = LabeledPointLineStyle(name, line)
                    point.applyStyle(style)
                }

                line
            }

            "point" ->{
                val style = LabeledPointRotatedStyle(names.first(), - PI / 3)
                points[0].applyStyle(style)
            }

            "circle" -> Circle(points[0], points[1])
                .applyStyle(CircleStyle)

            else -> error("No such object defined!")
        }
    }

    fun parse(tokens: List<Token>): List<PlanarObject<*>> {
        val objects = mutableListOf<PlanarObject<*>>()

        var i = 0; while (i in tokens.indices) {
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