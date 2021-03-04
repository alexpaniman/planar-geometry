package math

class Difference(private val e1: Expression, private val e2: Expression) : Expression {
    override fun calculate(vars: Map<Variable, Double>) =
        e1.calculate(vars) - e2.calculate(vars)

    override fun derive(variable: Variable) =
        e1.derive(variable) - e2.derive(variable)

    override fun toString() = "$e1 - $e2"
}

operator fun Expression.minus(other: Expression) = Difference(this, other)
operator fun Double.minus(other: Expression) = Difference(const(this), other)
operator fun Expression.minus(other: Double) = Difference(this, const(other))
