package math

import math.Constant.Companion.ZERO

class Sum(e1: Expression, e2: Expression) : Expression {
    private val operands = listOf(e1, e2)

    override fun calculate(vars: Map<Variable, Double>) = operands
        .map { it.calculate(vars) }
        .sum()

    override fun derive(variable: Variable) = operands
        .map { it.derive(variable) }
        .reduceRight { expr, acc -> acc + expr }

    override fun toString() = "(${operands[0]} + ${operands[1]})"
}

operator fun Expression.plus(other: Expression): Expression {
    if (this == ZERO)
        return other

    if (other == ZERO)
        return this

    return Sum(this, other)
}

operator fun Double.plus(other: Expression) = Sum(const(this), other)
operator fun Expression.plus(other: Double) = Sum(this, const(other))