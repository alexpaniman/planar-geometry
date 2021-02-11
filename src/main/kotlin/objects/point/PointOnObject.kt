package objects.point

import objects.PlanarObject

class PointOnObject<XY>(private val parent: PlanarObject<XY>): MovablePoint() {
    override fun corePoints() = parent.corePoints()
}