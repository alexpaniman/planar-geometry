package objects.triangle

import illustration.Illustration
import objects.PlanarObject

abstract class Triangle : PlanarObject() {
    abstract override fun define(illustration: Illustration): XYTriangle
}