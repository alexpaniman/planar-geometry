package math

interface Expression {
    fun calculate(vars: Map<Variable, Double>): Double
    fun derive(variable: Variable): Expression

    operator fun invoke(vararg values: Pair<Variable, Double>) =
        calculate(mapOf(*values))
}