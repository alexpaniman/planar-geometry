package util

import objects.PlanarObject
import objects.label.LabeledStyle
import objects.label.XYLabel
import objects.point.Point
import kotlin.math.cos
import kotlin.math.sin

fun <XY> PlanarObject<XY>.withLabel(label: XYLabel) {
    this.style = LabeledStyle(label, this.style!!)
}

fun Point.withLabel(text: String, angle: Double) {
    val label = XYLabel(text, this.define())
    val radius = .1 // TODO Adjustable

    label.point.shift(
        radius * cos(angle),
        radius * sin(angle)
    )

    this.withLabel(label)
}