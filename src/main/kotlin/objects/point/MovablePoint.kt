package objects.point

abstract class MovablePoint(name: String): Point(name) {
    val pointXY = XYPoint(name)
    override fun define() = pointXY
}