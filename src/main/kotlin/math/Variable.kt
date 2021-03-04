package math

class Variable(private val name: String) : Expression {
    override fun calculate(vars: Map<Variable, Double>) =
        vars[this] ?: error("No value provided for $name variable!")

    override fun derive(variable: Variable) =
        if (variable == this) const(1.0)
        else this

    override fun toString() = name
}

fun variable(name: String) = Variable(name)
