package objects.point

abstract class MovablePoint: Point() {
    val pointXY = XYPoint()
    override fun define() = pointXY
}