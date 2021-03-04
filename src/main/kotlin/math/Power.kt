package math

import kotlin.math.pow

class Power(private val base: Expression, private val exponent: Expression) : Expression {
    override fun calculate(vars: Map<Variable, Double>) =
        base.calculate(vars).pow(exponent.calculate(vars))

    override fun derive(variable: Variable) =
        base.power(exponent - 1.0) * (
                base * exponent.derive(variable) * ln(base) +
                base.derive(variable) * exponent
        )

    override fun toString() = "($base)^($exponent)"
}

fun Expression.power(exponent: Expression) = Power(this, exponent)
fun Expression.power(exponent: Double) = Power(this, const(exponent))
fun Double.power(exponent: Expression) = Power(const(this), exponent)
fun sqrt(expr: Expression) = Power(expr, const(0.5))
