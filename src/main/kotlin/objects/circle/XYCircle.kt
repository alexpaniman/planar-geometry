package objects.circle

import objects.point.XYPoint

data class XYCircle(val center: XYPoint, var radius: Double) {
    fun intersects(other: XYCircle): Boolean {
        val distance = other.center
            .distanceTo(this.center)

        return distance <= other.radius + radius
    }

    fun touches(other: XYCircle): Boolean {
        val distance = other.center
            .distanceTo(this.center)

        return distance == other.radius + radius
    }
}