package parser

import lexer.Token
import lexer.TokenType.*
import objects.PlanarObject
import objects.circle.Circle
import objects.line.Line
import objects.point.LockedPoint
import objects.point.Point
import objects.polygon.Polygon
import objects.style.circle.CircleStyle
import objects.style.line.LineStyle
import objects.style.point.PointStyle
import objects.style.point.labeled.LabeledPointLineStyle
import objects.style.point.labeled.LabeledPointPolygonStyle
import objects.style.point.labeled.LabeledPointRotatedStyle
import objects.style.polygon.PointsPolygonStyle
import kotlin.math.PI

class OldParser {
    private val pointsMap: MutableMap<String, Point> = mutableMapOf()
    private fun initializePoints(vararg names: String) = names
        .map {
            pointsMap.computeIfAbsent(it) {
                LockedPoint().applyStyle(PointStyle)
            }
        }

    private fun instantiateObject(id: String, vararg names: String): PlanarObject<*> {
        val points = initializePoints(*names)

        return when (id) {
            "triangle", "segment", "polygon" -> {
                val polygon = Polygon(points)
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

    private fun parseNames(tokens: List<Token>, i: Int, names: MutableList<String>): Int { // TODO Bad practice, need fix
        var newI = i // Counter delta

        if (tokens[++ newI].type != LCB)
            error("Unexpected token: ${tokens[newI].type}")

        while (tokens[++ newI].type != RCB)
            names.add(tokens[newI].symbol ?: error("Unexpected token: ${tokens[newI].type}"))

        return newI
    }

    private val parsed: MutableList<String> = mutableListOf()
    private val blacklist: MutableSet<PlanarObject<*>> = mutableSetOf()
    fun parse(tokens: List<Token>): List<PlanarObject<*>> {
        val objects = mutableMapOf<String, PlanarObject<*>>()

        var i = 0; while (i in tokens.indices) {
            val token = tokens[i]

            when (token.type) {
                DEF -> {
                    val id = tokens[++ i]
                        .symbol ?: error("Unexpected token: ${tokens[i].type}")

                    val names: MutableList<String> = mutableListOf()
                    i = parseNames(tokens, i, names)

                    if (i + 1 in tokens.indices && tokens[i + 1].type == IN) {
                        ++ i

                        val obj = instantiateObject(id, *names.toTypedArray())

                        val innerNames: MutableList<String> = mutableListOf()
                        i = parseNames(tokens, i, innerNames)

                        val actualName = innerNames.joinToString("")
                        val outerObject = objects[actualName]

                        if (outerObject is Polygon)
                            outerObject.areaContainer.objects.add(obj)

                        else error("It's not a container!")

                        parsed.addAll(innerNames)

                        val actualNameInner = names.joinToString("")
                        objects[actualNameInner] = obj
                    } else {
                        val obj = instantiateObject(id, *names.toTypedArray())
                        val actualName = names.joinToString("")

                        objects[actualName] = obj
                        if (names.any { it in parsed })
                            blacklist.add(obj)

                        parsed.addAll(names)
                    }
                }

                else -> error("Unexpected token: ${token.type}")
            }

            ++ i
        }

        return objects.map { (_, obj) -> obj }.filter { it !in blacklist }
    }
}