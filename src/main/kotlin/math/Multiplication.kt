package math

import math.Constant.Companion.ONE
import math.Constant.Companion.ZERO

class Multiplication(e1: Expression, e2: Expression) : Expression {
    private val operands = listOf(e1, e2)

    override fun calculate(vars: Map<Variable, Double>) = operands
        .map { it.calculate(vars) }
        .reduceRight { expr, acc -> expr * acc }

    override fun derive(variable: Variable) =
        operands[0].derive(variable) * operands[1] +
        operands[1].derive(variable) * operands[0]

    override fun toString() = "${operands[0]} * ${operands[1]}"
}

operator fun Expression.times(other: Expression): Expression {
    if (this == ZERO || other == ZERO)
        return ZERO

    if (this == ONE)
        return other

    if (other == ONE)
        return this

    return Multiplication(this, other)
}

operator fun Double.times(other: Expression) = const(this) * other
operator fun Expression.times(other: Double) = this * const(other)

operator fun Expression.unaryMinus() = - 1.0 * this