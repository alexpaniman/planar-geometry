package math

import kotlin.math.tan

class Tan(private val operand: Expression) : Expression {
    override fun calculate(vars: Map<Variable, Double>) =
        tan(operand.calculate(vars))

    override fun derive(variable: Variable) =
        operand.derive(variable) / cos(operand).power(2.0)
}

fun tan(expr: Expression) = Tan(expr)