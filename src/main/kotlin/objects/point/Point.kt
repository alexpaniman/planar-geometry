package objects.point

import objects.PlanarObject

abstract class Point(val name: String): PlanarObject {
    abstract override fun define(): XYPoint
}