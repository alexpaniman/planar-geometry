package parser

import objects.PlanarObject
import objects.circle.Circle
import objects.container.AreaContainer
import objects.line.Line
import objects.point.*
import objects.polygon.Polygon
import objects.style.circle.CircleStyle
import objects.style.point.PointStyle
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
        else objectsNew[name] ?: points[name]

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
    val points = (singlePoint erst blank()).many(min - 1, max) appendV singlePoint
    return char<ParserContext>('(') then points erst char(')')
}

private fun points(num: Int, compute: ((String) -> Point)? = null) = points(num, num, compute)

fun planarObject(create: ((String) -> PlanarObject<*>)? = null, createSegments: Boolean = false):
        Parser<StringInput<ParserContext>, Pair<String, PlanarObject<*>>> =

    (char<ParserContext>('(') then
            ((identifier<ParserContext>() erst blank()).many() appendV identifier()) erst
            char(')')).transform { input, names ->
        val stringName = names.joinToString(" ")

        var (existingObj, context) = input.context.obj(stringName, create)
        if (existingObj == null && createSegments) {
            val points = names.mapNotNull {
                val (obj, ctx) = context.point(it)
                context = ctx; obj?.point
            }

            if (points.size == names.size) {
                existingObj = Polygon(points)
            }
        }

        (stringName to existingObj) to input.copy(context = context)
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
    .combine(char<ParserContext>(':') then number()) { fst, snd -> fst to snd }

private val ratioPoint = (spacedWord<ParserContext>("point") then
        char('(') then identifier() erst char(')'))
    .combine(blank<ParserContext>() then spacedWord("on") then points(num = 2)) { fst, snd -> fst to snd }
    .combine(blank<ParserContext>() then ratio) { fst, (numerator, denominator) ->
        val (name, points) = fst
        val (first, second) = points.map { (_, _, point) -> point }

        val container = Line(first, second)
        val point = RatioPoint(first, second, numerator / denominator)
            .applyStyle(LabeledPointLineStyle(name, container))

        name to point.also { it.passive = true }
    }
    .transform { input, (name, point) ->
        val ctx = input.context.addPoint(name, point)
        point to input.copy(context = ctx)
    }

private val point = randomPoint trying ratioPoint

private val circleBase = (spacedWord<ParserContext>("circle") then
        char('(') then identifier() erst char(')'))
    .transform { input, name ->
        val circle = Circle(AnyPoint().applyStyle(PointStyle), AnyPoint())
            .applyStyle(CircleStyle)
        val ctx = input.context.addObj(name, circle)
        circle to input.copy(context = ctx)
    }

private fun sw(w: String) = spacedWord<ParserContext>(w)
private fun ch(c: Char) = char<ParserContext>(c)
private fun sc(c: Char) = space then ch(c) erst space
private val id = identifier<ParserContext>()
private val space = space<ParserContext>(min = 1)

private val describedCircle = (sw("circle") then ch('(') then id erst ch(')') erst space erst sw("described"))
    .combineT(points(num = 3, compute = pointAny)) { input, circle, triangle ->
        val points = triangle.map { it.point }
        val center = DescribedCircleCenterPoint(points)
            .applyStyle(PointStyle)
        val create = Circle(center, triangle.first().point)
            .applyStyle(CircleStyle)
            .also { it.passive = true }

        val ctx = input.context.addObj(circle, create)
        create to input.copy(context = ctx)
    }

private val inscribedCircle = (sw("circle") then ch('(') then id erst ch(')') erst space erst sw("inscribed"))
    .combineT(points(num = 3, compute = pointAny)) { input, circle, triangle ->
        val points = triangle.map { it.point }

        val center = InscribedCircleCenterPoint(points)
            .applyStyle(PointStyle)
        val sidePoint = Projection(center, Line(points[0], points[1]))

        val create = Circle(center, sidePoint)
            .applyStyle(CircleStyle)
            .also { it.passive = true }

        val ctx = input.context.addObj(circle, create)
        create to input.copy(context = ctx)
    }


private val circleByTwoPoints = (sw("circle") then ch('(') then id erst ch(')'))
    .combine (space then sw("center" ) then points(num = 1)) { name, point -> name to point }
    .combineT(space then sw("through") then points(num = 1)) { input, (name, center), through ->
        val circle = Circle(center[0].point, through[0].point)
            .applyStyle(CircleStyle)
            .also { it.passive = true }

        val ctx = input.context.addObj(name, circle)
        circle to input.copy(context = ctx)
    }

private val circle = describedCircle or inscribedCircle or circleByTwoPoints or circleBase

private val defineGlobal = spacedWord<ParserContext>("def") then (polygonalShapes or point or circle)

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

// private val objectL = ch('(') then (id erst space).many() appendV id erst ch(')')

private val pointNew = ch('(') then id erst ch(')')
private val planarObj = planarObject(createSegments = true)

private val intersection = (sw("assign") then (pointNew erst space).many(min = 1))
    .combineT(sw("to") then (planarObj erst space erst ch('x') erst space).many() appendV planarObj) { input, names, objects ->
        val separated = objects
            .map { (_, obj) -> obj }

        var context = input.context
        var intersection: Point? = null
        for ((index, name) in names.withIndex()) {
            intersection = SetOfIntersections(index, separated)
                .also { it.passive = true }
                .also { it.applyStyle(LabeledPointRotatedStyle(name, PI / 3.0)) }
            context = context.addPoint(name, intersection)
        }

        intersection!! to input.copy(context = context)
    }

private val perpendicular = (sw("def") then sw("perpendicular") then ch('(') then point() erst space)
    .combine(id erst ch(')')) { point0, point1 -> point0 to point1 }
    .combineT(space then sw("to") then points(num = 2)) { input, (point, projection), (from, to) ->
        val line = Line(from.point, to.point)
        val projected = Projection(point.point, line)
            .also { it.passive = true }

        val segment = Polygon(listOf(point.point, projected))
            .applyStyle(PolygonStyle)
            .also { it.passive = true }

        projected.applyStyle(LabeledPointPolygonStyle(projection, segment))

        val context = input.context.addPoint(projection, projected)
        segment to input.copy(context = context)
    }

private val sector = (sw("def") then sw("sector") then ch('(') then point() erst space)
    .combine(id erst ch(')')) { fst, snd -> fst to snd }
    .combine(space then sw("of") then points(num = 2)) { sector, segment -> sector to segment }
    .combineT(space then ratio) { input, (sector, linePoints), (numerator, denominator) ->
        val (start, name) = sector

        val (from, to) = linePoints.map { it.point }
        val line = Line(from, to)

        val ratio = numerator / (numerator + denominator)

        val section = AngleSector(ratio, start.point, line)
            .also { it.passive = true }

        val segment = Polygon(listOf(start.point, section))
            .applyStyle(PolygonStyle)
            .also { it.passive = true }

        section.applyStyle(LabeledPointPolygonStyle(name, segment))

        val context = input.context.addPoint(name, section)
        segment to input.copy(context = context)
    }

private val definition = (defineNested erst eol()) or (defineGlobal erst eol()) or (intersection erst eol()) or (perpendicular erst eol()) or (sector erst eol())

private val comment = char<ParserContext>('#').map { null } erst
        anything<ParserContext>().many() erst eol()

private val styleHide = spacedWord<ParserContext>("style hide") then planarObject()
    .map { (_, obj) -> obj.hide(); null }

private val styleHideLabel = spacedWord<ParserContext>("style hide labels") then points()
    .map { points ->
        points.forEach { (_, _, point) ->
            point.hide()
            point.applyStyle(PointStyle)
        }

        null
    }

private val statement = definition or comment or styleHide or styleHideLabel
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