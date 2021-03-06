package objects.point

class RatioPoint(private val p1: Point, private val p2: Point, private val ratio: Double) : Point() {
    override fun define(): XYPoint {
        val definedP1 = p1.define()
        val definedP2 = p2.define()
        return definedP2.copy().apply {
            multiply(ratio)
            add(definedP1)
            multiply(1.0 / (1.0 + ratio))
        }
    }

    override fun corePoints() = p1.corePoints() + p2.corePoints()
}