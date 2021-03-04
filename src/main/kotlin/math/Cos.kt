package math

import kotlin.math.cos

class Cos(private val operand: Expression) : Expression {
    override fun calculate(vars: Map<Variable, Double>) =
        cos(operand.calculate(vars))

    override fun derive(variable: Variable) =
        - sin(operand) * operand.derive(variable)
}

fun cos(expr: Expression) = Cos(expr)