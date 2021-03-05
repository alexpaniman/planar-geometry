package parser

import objects.PlanarObject
import objects.container.AreaContainer
import objects.point.AnyPoint
import objects.point.Point
import objects.polygon.Polygon
import objects.style.point.labeled.LabeledPointPolygonStyle
import objects.style.polygon.PointsPolygonStyle
import parsec.*
import kotlin.Int.Companion.MAX_VALUE

data class ParserContext(
    private val points: Map<String, Point>,
    private val objects: Map<String, PlanarObject<*>>
) {

    fun point(name: String, create: ((String) -> Point)? = null):
            Pair<Point?, ParserContext> {

        val pointsNew = points.toMutableMap()

        val point = if (create != null)
            pointsNew.computeIfAbsent(name, create)
        else pointsNew[name]

        return point to this.copy(points = pointsNew)
    }

    fun obj(name: String, create: ((String) -> PlanarObject<*>)? = null):
            Pair<PlanarObject<*>?, ParserContext> {

        val objectsNew = objects.toMutableMap()

        val obj = if (create != null)
            objectsNew.computeIfAbsent(name, create)
        else objectsNew[name]

        return obj to this.copy(objects = objectsNew)
    }

    fun addObj(name: String, obj: PlanarObject<*>) =
        this.copy(objects = objects + (name to obj))
}

private fun point(create: ((String) -> Point)? = null):
        Parser<StringInput<ParserContext>, Pair<String, Point>> =

    identifier<ParserContext>().transform { input, name ->
        val (point, ctx) = input.context.point(name, create)
        (name to point) to input.copy(context = ctx)
    }.ensure("existing point") { (_, point) ->
        point != null
    }.map { (name, point) -> name to point!! }

private fun points(
    min: Int = 1,
    max: Int = MAX_VALUE,
    compute: ((String) -> Point)? = null
): Parser<StringInput<ParserContext>, List<Pair<String, Point>>> {

    val singlePoint = point(compute)
    val points = (singlePoint erst blank()).many(min - 1, max) append singlePoint
    return char<ParserContext>('(') then points erst char(')')
}

private fun points(num: Int, compute: ((String) -> Point)? = null) = points(num, num, compute)

fun planarObject(create: ((String) -> PlanarObject<*>)? = null):
        Parser<StringInput<ParserContext>, Pair<String, PlanarObject<*>>> =

    points().transform { input, name ->
        val stringName = name.joinToString(" ")

        val (obj, ctx) = input.context.obj(stringName, create)
        (stringName to obj) to input.copy(context = ctx)
    }.ensure("existing object name") { (_, obj) ->
        obj != null
    }.map { (name, obj) -> name to obj!! }

private val pointAny = { _: String -> AnyPoint() }
private val polygon = spacedWord<ParserContext>("polygon") then points(
    min = 3,
    compute = pointAny
) // Triangle can be also declared as polygon

private val segment = spacedWord<ParserContext>("segment") then
        points(num = 2, compute = pointAny)

private val triangle = spacedWord<ParserContext>("triangle") then
        points(num = 3, compute = pointAny)

private val polygonalShapes = (polygon or triangle or segment)
    .transform { input, name ->
        val points = name.map { (_, point) -> point }
        val polygon = Polygon(points)
            .applyStyle(PointsPolygonStyle)

        name.forEach { (name, point) ->
            val style = LabeledPointPolygonStyle(name, polygon)
            point.applyStyle(style)
        }

        val stringName = name.joinToString(" ")
        val ctx = input.context.addObj(stringName, polygon)
        polygon to input.copy(context = ctx)
    }

private val point = (spacedWord<ParserContext>("point") then points(num = 1, compute = pointAny))
    .map { namedPoint ->
        val (_, point) = namedPoint[0]
        point
    }

private val defineGlobal = spacedWord<ParserContext>("def") then (polygonalShapes or point)

private val containerObject = planarObject()
    .map { (_, obj) -> obj }
    .ensure("container object") { it is AreaContainer }
    .map { it as AreaContainer }

private val defineNested = (defineGlobal erst blank() erst spacedWord("in"))
    .combine(containerObject) { obj, container ->
        container.addInObject(obj); obj
    }

private val definition = (defineGlobal erst eol()) trying (defineNested erst eol())

private val statements = (definition erst space(min = 0)).many(min = 1)

fun parse(input: String): List<PlanarObject<*>> {
    val ctx = ParserContext(mapOf(), mapOf())
    val stringInput = StringInput(input, ctx)
    return statements.parse(stringInput).unwrap()
}