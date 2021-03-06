package parser

import objects.PlanarObject
import objects.container.AreaContainer
import objects.line.Line
import objects.point.AnyPoint
import objects.point.Point
import objects.point.RatioPoint
import objects.polygon.Polygon
import objects.style.point.labeled.LabeledPointLineStyle
import objects.style.point.labeled.LabeledPointPolygonStyle
import objects.style.point.labeled.LabeledPointRotatedStyle
import objects.style.polygon.PolygonStyle
import parsec.*
import kotlin.Int.Companion.MAX_VALUE
import kotlin.math.PI

data class ParsedPoint(val name: String, val existed: Boolean, val point: Point)

data class ParserContext(
    val points: Map<String, Point>,
    val objects: Map<String, PlanarObject<*>>
) {

    fun point(name: String, create: ((String) -> Point)? = null):
            Pair<ParsedPoint?, ParserContext> {

        val pointsNew = points.toMutableMap()

        val parsedPoint = when {
            name in pointsNew -> ParsedPoint(name, true, pointsNew[name]!!)
            create != null -> {
                val point = create(name)
                pointsNew[name] = point
                ParsedPoint(name, false, point)
            }
            else -> null
        }

        return parsedPoint to this.copy(points = pointsNew)
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

    fun addPoint(name: String, point: Point) =
        this.copy(points = points + (name to point))
}


private fun point(create: ((String) -> Point)? = null):
        Parser<StringInput<ParserContext>, ParsedPoint> =
    identifier<ParserContext>().transform { input, name ->
        val (point, ctx) = input.context.point(name, create)
        point to input.copy(context = ctx)
    }.ensure("existing point") { it != null }.map { it!! }

private fun points(
    min: Int = 1,
    max: Int = MAX_VALUE,
    compute: ((String) -> Point)? = null
): Parser<StringInput<ParserContext>, List<ParsedPoint>> {

    val singlePoint = point(compute)
    val points = (singlePoint erst blank()).many(min - 1, max) append singlePoint
    return char<ParserContext>('(') then points erst char(')')
}

private fun points(num: Int, compute: ((String) -> Point)? = null) = points(num, num, compute)

fun planarObject(create: ((String) -> PlanarObject<*>)? = null):
        Parser<StringInput<ParserContext>, Pair<String, PlanarObject<*>>> =

    points().transform { input, name ->
        val stringName = name.joinToString(" ") { (name, _, _) -> name }

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
        val points = name.map { (_, _, point) -> point }
        val polygon = Polygon(points)
            .applyStyle(PolygonStyle)

        name.forEach { (name, _, point) ->
            val style = LabeledPointPolygonStyle(name, polygon)
            point.applyStyle(style)
        }

        val stringName = name.joinToString(" ") { (name, _, _) -> name }
        val ctx = input.context.addObj(stringName, polygon)

        val existed = name
            .filter { (_, existed, _) -> existed }
            .map { (_, _, point) -> point }

        when (existed.size) {
            points.count() -> polygon.also { it.passive = true }
            else -> polygon
            // else -> Polygon(existed) // TODO Is it good idea?
        } as PlanarObject<*> to input.copy(context = ctx)
    }

private val randomPoint = (spacedWord<ParserContext>("point") then points(num = 1, compute = pointAny))
    .map { namedPoint ->
        val (name, _, point) = namedPoint[0]
        val style = LabeledPointRotatedStyle(name, PI / 4.0)
        point.applyStyle(style)
    }

private val ratio = (spacedWord<ParserContext>("ratio") then number())
    .combine(char<ParserContext>(':') then number()) { fst, snd -> fst / snd }

private val ratioPoint = (spacedWord<ParserContext>("point") then
        char('(') then identifier() erst char(')'))
    .combine(blank<ParserContext>() then spacedWord("on") then points(num = 2)) { fst, snd -> fst to snd }
    .combine(blank<ParserContext>() then ratio) { fst, ratio ->
        val (name, points) = fst
        val (first, second) = points.map { (_, _, point) -> point }

        val container = Line(first, second)
        val point = RatioPoint(first, second, ratio)
            .applyStyle(LabeledPointLineStyle(name, container))

        name to point.also { it.passive = true }
    }
    .transform { input, (name, point) ->
        val ctx = input.context.addPoint(name, point)
        point to input.copy(context = ctx)
    }

private val point = randomPoint trying ratioPoint

private val defineGlobal = spacedWord<ParserContext>("def") then (polygonalShapes or point)

private val containerObject = planarObject()
    .map { (_, obj) -> obj }
    .ensure("container object") { it is AreaContainer }
    .map { it as AreaContainer }

private val defineNested = (defineGlobal erst blank() erst spacedWord("in"))
    .combine(containerObject) { obj, container ->
        container.addInObject(obj)
        obj.passive to null
        // We don't need to add nested objects in collection hence null
    }.ensure("non-linked object") { (passive, _) ->
        !passive
    }.map { (_, obj) -> obj }

private val definition = (defineGlobal erst eol()) trying (defineNested erst eol())

private val comment = char<ParserContext>('#').map { null } erst
        anything<ParserContext>().many() erst eol()

private val statement = definition or comment
private val statements = (statement erst space(min = 0)).many(min = 1) erst eof()

fun parse(input: String, currentMode: Boolean = false): List<PlanarObject<*>> {
    val ctx = ParserContext(mapOf(), mapOf())
    val stringInput = StringInput(input, ctx)

    return when (val parsed = statements.parse(stringInput)) {
        is Result.Success -> parsed.unwrap()
            .filterNotNull()

        is Result.Error -> {
            if (currentMode)
                parsed.unwrap()

            // Now let's parse file line by line
            // To tell user about problems on each line

            val errors: MutableList<Result.Error> = mutableListOf()

            // Let's create new context and new input
            val pureCTX = ParserContext(mapOf(), mapOf())
            var pureInput = StringInput(input, pureCTX)

            while (!pureInput.isEmpty) {
                val parsedStatement = (statement or space()).parse(pureInput)
                if (parsedStatement is Result.Success) { // Statement is guaranteed to end with eof or eol
                    pureInput = parsedStatement.rest
                    continue
                }

                errors += parsedStatement as Result.Error

                while (!pureInput.isEmpty && pureInput.current != '\n')
                    pureInput = pureInput.shrink(1) // Skip to the next line

                pureInput = pureInput.shrink(1) // Skip '\n'
            }

            for (error in errors) try {
                error.unwrap()
            } catch (ignore: ParsecException) {}

            throw ParsecException()
        }
    }
}