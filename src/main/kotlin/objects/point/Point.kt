package objects.point

import objects.PlanarObject

abstract class Point: PlanarObject<XYPoint>() {
    abstract override fun define(): XYPoint
}