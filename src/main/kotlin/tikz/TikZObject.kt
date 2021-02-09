package tikz

abstract class TikZObject {
    abstract fun tikzify(): String
}