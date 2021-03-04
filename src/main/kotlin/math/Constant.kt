package math

open class Constant(private val value: Double) : Expression {
    companion object {
        val ZERO = const(0.0)
        val ONE  = const(1.0)

        val E  = object : Constant(2.718_281_828_459_045_235_360_287_471_352_662_497_757) {
            override fun toString() = "e"
        }

        val PI = object : Constant(3.141_592_653_589_793_238_462_643_383_279_502_884_197) {
            override fun toString() = "pi"
        }
    }

    override fun calculate(vars: Map<Variable, Double>) = value
    override fun derive(variable: Variable) = ZERO

    override fun toString() = "$value"
}

fun const(value: Double) = Constant(value)