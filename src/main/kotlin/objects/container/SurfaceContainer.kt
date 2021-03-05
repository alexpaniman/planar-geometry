package objects.container

import objects.PlanarObject

interface SurfaceContainer {
    fun addOnObject(obj: PlanarObject<*>)
}