package math

import math.Constant.Companion.E
import kotlin.math.abs
import kotlin.math.log

class Logarithm(private val expr: Expression, private val base: Expression) : Expression {
    override fun calculate(vars: Map<Variable, Double>) = log(abs(expr.calculate(vars)), base.calculate(vars))
    override fun derive(variable: Variable) =
        expr.derive(variable) / (expr * ln(base)) -
        base.derive(variable) / (base * ln(base)) * (ln(expr) / ln(base))

    override fun toString() = "log($expr, $base)"
}

fun log(expr: Expression, base: Expression) = Logarithm(expr, base)
fun log(expr: Expression, base: Double) = Logarithm(expr, const(base))
fun log(expr: Double, base: Expression) = Logarithm(const(expr), base)
fun ln(expr: Expression) = log(expr, E)
fun ln(expr: Double) = log(expr, E)
