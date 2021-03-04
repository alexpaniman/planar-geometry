import math.*
import org.junit.jupiter.api.Test
import kotlin.math.abs

class MathTest {
    private fun compareDerivatives(expr: (Variable) -> Expression, diff: (Variable) -> Expression, vararg values: Double) {
        val x = variable("x")

        val expression = expr(x)
        val derivative = expression.derive(x)

        val actualDiff = diff(x)

        for (value in values) {
            val calculated = derivative(x to value)
            val actual = actualDiff(x to value)

            assert(abs(calculated - actual) < 1e-6) {
                "expected: <$actual> but was: <$calculated>"
            }
        }
    }

    @Test
    fun `(x)^(2) derivative`() = compareDerivatives(
        expr = { x -> x.power(2.0) },
        diff = { x -> 2.0 * x },

        1.0, 2.0, 3.0, 4.0, 5.0
    )

    @Test
    fun `(2)^(x) derivative`() = compareDerivatives(
        expr = { x -> 2.0.power(x) },
        diff = { x -> 2.0.power(x) * ln(2.0) },

        1.0, 2.0, 3.0, 4.0, 5.0
    )

    @Test
    fun `ln(x)^(2) derivative`() = compareDerivatives(
        expr = { x -> ln(x).power(2.0) },
        diff = { x ->  2.0 * ln(x) / x },

        1.0, 2.0, 3.0, 4.0, 5.0
    )

    @Test
    fun `sin(x)^(2) + cos(x)`() = compareDerivatives(
        expr = { x -> sin(x).power(2.0) + cos(x) },
        diff = { x ->  2.0 * cos(x) * sin(x) - sin(x) },

        1.0, 2.0, 3.0, 4.0, 5.0
    )

    @Test
    fun `(2)^(2 mul sin(x)) div (x mul cos(x)) + ln(cos(x))`() = compareDerivatives(
        expr = { x -> 2.0.power(2.0 * sin(x)) / (x * cos(x)) + ln(cos(x)) },
        diff = { x ->
            2.0 * 2.0.power(2.0 * sin(x)) * ln(2.0) / x -
            sin(x) / cos(x) +
            2.0.power(2.0 * sin(x)) * sin(x) / (x * cos(x).power(2.0)) -
            2.0.power(2.0 * sin(x)) / (x.power(2.0) * cos(x))
        },

        1.0, 2.0, 3.0, 4.0, 5.0
    )
}