package objects.point

class RatioPoint(private val p1: Point, private val p2: Point, ratio: Double) : Point() {
    override fun define(): XYPoint {
        val definedP1 = p1.define()
        val definedP2 = p2.define()
        return definedP1.copy().apply {
            add(definedP2)
            multiply(0.5)
        }
    }

    override fun corePoints() = p1.corePoints() + p2.corePoints()
}