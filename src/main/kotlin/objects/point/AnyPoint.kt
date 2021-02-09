package objects.point

class AnyPoint(name: String): MovablePoint(name) {
    override fun corePoints() = listOf(this)
}