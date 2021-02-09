package objects.point

class PointOnObject(name: String, private val obj: PointOn): MovablePoint(name) {
    init { obj.addPointOn(this) }

    override fun corePoints() = obj.corePoints()
}