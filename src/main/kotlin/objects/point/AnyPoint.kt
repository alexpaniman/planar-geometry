package objects.point

import objects.circle.XYCircle
import kotlin.random.Random

// Represents point that can be placed anywhere freely
class AnyPoint: MovablePoint() {
    override fun corePoints() = listOf(this)

    override fun setup(area: XYCircle, entropy: Random) {
        pointXY.moveTo(area.center.x, area.center.y)
    }
}