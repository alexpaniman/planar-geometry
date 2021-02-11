package objects.point

import objects.PlanarObject

class PointInObject<XY>(private val parent: PlanarObject<XY>): MovablePoint() {
    override fun corePoints() = parent.corePoints()
}