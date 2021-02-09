package objects.line

import objects.XYPlanarObject
import objects.point.XYPoint

data class XYLine(val p1: XYPoint, val p2: XYPoint) : XYPlanarObject()

