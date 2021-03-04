package parser

import lexer.Token
import lexer.TokenType
import lexer.TokenType.*
import objects.PlanarObject
import objects.circle.Circle
import objects.line.Line
import objects.point.AnyPoint
import objects.point.Point
import objects.polygon.Polygon
import objects.style.point.labeled.LabeledPointPolygonStyle

class NewParser(private val tokens: List<Token>) {
    private var i = 0
    private val planarObjects: MutableList<PlanarObject<*>> = mutableListOf()

    private val next: Token // This property actually modifies state of parser by stepping forward in token chain
        get() {
            if (i !in tokens.indices)
                throw ParserException("Unexpected end of tokens")
            return tokens[i++]
        }

    private fun checkType(type: TokenType) =
        tokens.getOrNull(i)?.type == type

    private fun <T: Any> saveExcursion(modify: () -> T?): () -> T? = {
        val start = i

        val parse = modify()
        if (parse == null)
            i = start

        parse
    }

    private fun <T: Any> (() -> T?).ensure(vararg types: TokenType): () -> T? =
        saveExcursion {
            val parseLast = this() ?: return@saveExcursion null

            for (type in types) {
                if (!checkType(type))
                    return@saveExcursion null
                else ++i
            }

            parseLast
        }

    private infix fun <T: Any> (() -> T?).or(other: () -> T?): () -> T? =
        saveExcursion { this() ?: other() }

    private fun <F: Any, T: Any> (() -> F?).parse(parse: () -> T): () -> T? =
        saveExcursion {
            if (this() != null)
                parse()
            else null
        }

    private fun <T: Any> (() -> T?).check(check: (T) -> Boolean) =
        saveExcursion {
            val parseFirst = this()
            if (parseFirst == null || !check(parseFirst))
                return@saveExcursion null
            parseFirst
        }

    private fun ensure(vararg types: TokenType): () -> Unit? =
        saveExcursion {
            for (type in types) {
                if (!checkType(type))
                    return@saveExcursion null
                else ++i
            }
        }

    private fun <T: Any> withType(type: TokenType, parse: (String) -> T): T? {
        return if (checkType(type))
            parse(tokens[i ++].symbol ?: throw ParserException("Symbol expected!"))
        else null
    }

    private fun text() = withType(ID) { it }

    private fun <F: Any, T: Any> (() -> F?).many(parse: () -> T?): () -> List<T>? =
        saveExcursion {
            val list = mutableListOf<T>()
            if (this() == null) do {
                val parseCurrent = parse()
                list += parseCurrent ?: break
            } while(true)

            list
        }

    private val names = {}
        .ensure(LCB)
        .many(::text)
        .ensure(RCB)
        .check { it.isNotEmpty() }


    private val vertices = mutableMapOf<String, Point>()
    private fun collectPoints(create: (String) -> Point): List<Pair<String, Point>>? {
//        val names = parserPointsNames() ?: return null
//
//        return names.zip(names.map {
//            vertices.computeIfAbsent(it, create)
//        })
        TODO()
    }

    // private inline fun <reified T: PlanarObject<*>> List<Pair<String, T>>.createObject(create: ())

    private inline fun <reified L: PlanarObject<*>, T: PlanarObject<*>> List<Pair<String, T>>.styleObject(createObj: (List<T>) -> L, styleObj: T.(String, L) -> Unit): List<T> {
        val createdObject = createObj(this.map { (_, obj) -> obj })

        this.forEach { (name, obj) ->
            obj.styleObj(name, createdObject)
        }

        return this.map { (_, obj) -> obj }
    }

    private fun parseCircle(): Circle? {
        TODO()
    }

    private fun parseSegment(): Polygon? {
        TODO()
    }

    private fun planar(): Nothing =  TODO() // polygon or polygon

    private fun parsePolygon(): Polygon? {
        collectPoints { AnyPoint() }
            ?.styleObject({ points -> Polygon(points) }) { name, polygon ->
                applyStyle(LabeledPointPolygonStyle(name, polygon))
            }

        TODO()
    }

    private fun parseTriangle(): Polygon? {
        TODO()
    }

    private fun parseIntersection(): Point? {
        TODO()
    }

    private fun parseLine(): Line? {
        TODO()
    }

    // Object definition goes like this:
    // [def] [id] (for object type) [(] [id] (for name) [)]
    private fun parseObject(): PlanarObject<*>? {
        val start = i

        if (tokens[i++].type != DEF) {
            --i
            return null
        }

        if (tokens[i++].type != ID) {
            --i
            return null
        }

        return when (tokens[i++].symbol) {
            "triangle" -> parseTriangle()
            "polygon" -> parsePolygon()
            "circle" -> parseCircle()
            "segment" -> parseSegment()
            "intersection" -> parseIntersection()
            "line" -> parseLine()
            else -> throw ParserException("Unexpected type of object!")
        }.also {
            if (it == null)
                i = start
        }
    }

    fun parse() {
        while (i in tokens.indices)
            planarObjects += parseObject() ?: throw ParserException("Object was expected!")
    }
}