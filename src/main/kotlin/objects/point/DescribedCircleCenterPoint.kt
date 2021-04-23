package objects.point

class DescribedCircleCenterPoint(private val points: List<Point>): Point() {
    override fun define(): XYPoint {
        assert(points.size == 3)
        val (a, b, c) = points
            .map { it.define() }

        val normLineAB = (b.x - a.x) / (a.y - b.y)
        val midPointAB = (a + b) / 2.0

        val freeLineAB = midPointAB.y - normLineAB * midPointAB.x

        val normLineBC = (c.x - a.x) / (a.y - c.y)
        val midPointBC = (a + c) / 2.0

        val freeLineBC = midPointBC.y - normLineBC * midPointBC.x

        val centerX = (freeLineAB - freeLineBC) / (normLineBC - normLineAB)
        val centerY = normLineAB * centerX + freeLineAB

        return XYPoint(centerX, centerY)
    }

    override fun corePoints(): List<MovablePoint> =
        points.map { it.corePoints() }.flatten()
}