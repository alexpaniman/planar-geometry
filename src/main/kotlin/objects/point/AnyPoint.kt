package objects.point

import objects.circle.XYCircle
import kotlin.random.Random

class AnyPoint: MovablePoint() {
    override fun corePoints() = listOf(this)

    override fun setup(random: Random, circle: XYCircle) {
        pointXY.moveTo(circle.center.x, circle.center.y)
    }
}