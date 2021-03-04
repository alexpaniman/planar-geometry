package math

import kotlin.math.tan

class Cot(private val operand: Expression) : Expression {
    override fun calculate(vars: Map<Variable, Double>) =
        1.0 / tan(operand.calculate(vars))

    override fun derive(variable: Variable) =
        - operand.derive(variable) / sin(operand).power(2.0)
}

fun cot(operand: Expression) = Cot(operand)