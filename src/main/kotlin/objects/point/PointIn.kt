package objects.point

import objects.PlanarObject

interface PointIn: PlanarObject {
    fun addPointIn(point: Point)
}