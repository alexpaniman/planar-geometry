package objects.point

import illustration.Illustration
import objects.PlanarObject
import objects.point.XYPoint

abstract class Point : PlanarObject() {
    abstract override fun define(illustration: Illustration): XYPoint
}