package objects

import objects.circle.XYCircle
import objects.point.MovablePoint
import kotlin.random.Random

interface PlanarObject {
    fun define(): XYPlanarObject
    fun corePoints(): List<MovablePoint>

    fun setup(random: Random, circle: XYCircle) {}
}