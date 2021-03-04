package objects.point

import objects.circle.XYCircle
import kotlin.random.Random

class LockedPoint(/*private val lock: PlanarObject<*>*/): MovablePoint() {
    override fun corePoints() = error("")

    private var initialized = false
    override fun define() = pointXY

    override fun setup(area: XYCircle, entropy: Random) {
        if (!initialized)
            pointXY.moveTo(area.center)

        initialized = true
    }
}