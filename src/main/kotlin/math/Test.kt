package math

import math.Constant.Companion.E
import math.Constant.Companion.PI

fun main() {
    val x = variable("x")
    val expr = x.power(x)

    val derivative = expr.derive(x)
    println("Expression: $expr")
    println("Derivative: $derivative")
}

