package objects

import illustration.Illustration

abstract class PlanarObject {
    abstract val isDefined: Boolean

    abstract fun define(illustration: Illustration): XYPlanarObject
}