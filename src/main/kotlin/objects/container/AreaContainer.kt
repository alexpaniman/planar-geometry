package objects.container

import objects.PlanarObject

interface AreaContainer {
    fun addInObject(obj: PlanarObject<*>)
}