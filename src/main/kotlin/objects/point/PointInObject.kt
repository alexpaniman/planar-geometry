package objects.point

class PointInObject(name: String, private val obj: PointIn): MovablePoint(name) {
    init { obj.addPointIn(this) }

    override fun corePoints() = obj.corePoints()
}