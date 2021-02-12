package objects.container

import objects.circle.XYCircle
import java.util.*

abstract class Container(val objects: List<Objects>) {
    abstract fun touches(circle: XYCircle): Boolean

    fun setup(area: XYCircle) {

    }
}