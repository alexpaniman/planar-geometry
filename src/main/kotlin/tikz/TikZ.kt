package tikz

class TikZ {
    private val objects: MutableMap<Any, String> = mutableMapOf()

    fun tikzify() = buildString {
        for ((_, tikz) in objects)
            appendLine(tikz)
    }

    fun draw(obj: Any, tikzCode: String) {
        objects[obj] = tikzCode
    }
}