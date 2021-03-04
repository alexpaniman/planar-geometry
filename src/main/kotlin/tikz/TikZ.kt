package tikz

class TikZ {
    private val objects: MutableMap<Any, String> = mutableMapOf()

    fun tikzify() = objects.entries.joinToString("\n") { (_, tikzCode) -> tikzCode }

    fun draw(obj: Any, tikzCode: String) {
        objects[obj] = tikzCode
    }
}