package math

import kotlin.math.sin

class Sin(private val operand: Expression) : Expression {
    override fun calculate(vars: Map<Variable, Double>) =
        sin(operand.calculate(vars))

    override fun derive(variable: Variable) =
        cos(operand) * operand.derive(variable)
}

fun sin(expr: Expression) = Sin(expr)