package objects.point

import illustration.Illustration
import illustration.Illustration.Companion.BEST_LENGTH
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class AnyPoint: Point() {
    private var pointXY: XYPoint? = null

    override val isDefined: Boolean
        get() = pointXY != null

    override fun define(illustration: Illustration): XYPoint = illustration.run {
        if (pointXY != null)
            return pointXY!!

        val radius = BEST_LENGTH.localRandom()
        val rotate = (0.0..2 * PI).localRandom()

        val (x, y) = listOf(
            cos(rotate),
            sin(rotate)
        ).map { it * radius }
        val currentPoint = XYPoint(x, y)

        for (point in defined) {
            val definedPoint = point.define(this)
            val distance = definedPoint
                .distanceTo(currentPoint)

            if (!BEST_LENGTH.contains(distance)) {
                val newDistance = BEST_LENGTH.localRandom()
                definedPoint.moveAlongLine(currentPoint, newDistance)
            }
        }

        currentPoint.apply {
            defined.add(this@AnyPoint)
            pointXY = this
            // TODO Probably no the best way to do this
        }
    }
}