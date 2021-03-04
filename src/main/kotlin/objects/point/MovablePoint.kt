package objects.point

// Represents point that has no defined position
abstract class MovablePoint: Point() {
    val pointXY = XYPoint()
    override fun define() = pointXY
}