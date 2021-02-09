package objects.point

import objects.PlanarObject

interface PointOn: PlanarObject {
    fun addPointOn(point: Point)
}