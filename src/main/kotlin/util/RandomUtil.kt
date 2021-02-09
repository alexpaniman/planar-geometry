package util

import kotlin.random.Random

fun ClosedFloatingPointRange<Double>.random(random: Random) =
    random.nextDouble(this.start, this.endInclusive)