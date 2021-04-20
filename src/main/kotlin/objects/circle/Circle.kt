package objects.circle

import objects.PlanarObject
import objects.container.AreaContainer
import objects.container.CircularContainer
import objects.point.Point
import kotlin.random.Random

open class Circle(private val center: Point, private val side: Point): PlanarObject<XYCircle>(), AreaContainer { // TODO PointIn, PointOn, make it abstract
    val areaContainer = CircularContainer(2.0, 0.0, 0.01) {
        it.intersects(this.define())
    }

    override fun define(): XYCircle {
        val centerXY = center.define()

        val sideXY = side.define()
        val radius = centerXY.distanceTo(sideXY)

        return XYCircle(centerXY, radius)
    }

    override fun corePoints() = center.corePoints() + side.corePoints()

    override fun setup(area: XYCircle, entropy: Random) {
        val centerArea = XYCircle(area.center, 0.2)

        val centerSide = area.center.copy().also {
            val shiftX = entropy.nextDouble(area.radius * 0.6, area.radius * 0.9)
            it.shift(shiftX, 0.0)
        }
        val sideArea = XYCircle(centerSide, 0.2)

        center.softSetup(centerArea, entropy)
        side.softSetup(sideArea, entropy)

        val container = area.copy(radius = area.radius * 0.7)
        areaContainer.setup(container, entropy)
    }

    override fun addInObject(obj: PlanarObject<*>) {
        areaContainer.objects.add(obj)
    }
}