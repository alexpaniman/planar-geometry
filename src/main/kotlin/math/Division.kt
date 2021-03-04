package math

class Division(private val numerator: Expression, private val denominator: Expression) : Expression {
    override fun calculate(vars: Map<Variable, Double>) =
        numerator.calculate(vars) / denominator.calculate(vars)

    override fun derive(variable: Variable) =
        (  numerator.derive(variable) * denominator -
         denominator.derive(variable) *   numerator) / denominator.power(const(2.0))

    override fun toString() = "$numerator / $denominator"
}

operator fun Expression.div(other: Expression) = Division(this, other)
operator fun Double.div(other: Expression) = Division(const(this), other)
operator fun Expression.div(other: Double) = Division(this, const(other))
