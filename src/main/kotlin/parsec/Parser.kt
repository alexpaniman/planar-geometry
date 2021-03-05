package parsec

import parsec.Result.*

abstract class Parser<TInput, TValue> {
    abstract fun parse(input: TInput): Result<TInput, TValue>

    companion object {
        inline fun <TInput, TValue> fromLambda(crossinline lambda: (TInput) -> Result<TInput, TValue>): Parser<TInput, TValue> =
            object : Parser<TInput, TValue>() {
                override fun parse(input: TInput) = lambda(input)
            }
    }

    operator fun invoke(input: TInput) = parse(input)

    infix fun or(other: Parser<TInput, TValue>) =
        fromLambda<TInput, TValue> { input ->
            when(val result = this.parse(input)) {
                is Success -> result
                is Error -> {
                    val second = other.parse(input)
                    if (second !is Error) second
                    else {
                        val fstError = result.errorHolder
                        val sndError = second.errorHolder

                        Error(fstError.either(sndError))
                    }
                }
            }
        }

    inline fun <TCombined, RValue> combine(other: Parser<TInput, RValue>, crossinline combine: (TValue, RValue) -> TCombined) =
        fromLambda<TInput, TCombined> { input ->
            when(val parseFirst = this.parse(input)) {
                is Success -> {
                    val (firstValue, firstRest) = parseFirst

                    when(val parseSecond = other.parse(firstRest)) {
                        is Success -> {
                            val (secondValue, secondRest) = parseSecond
                            val combinedValue = combine(firstValue, secondValue)

                            Success(combinedValue, secondRest)
                        }

                        is Error -> parseSecond
                    }
                }

                is Error -> parseFirst
            }
        }

    inline fun <TNew> transform(crossinline map: (TInput, TValue) -> Pair<TNew, TInput>): Parser<TInput, TNew> =
        fromLambda { input ->
            when (val result = this.parse(input)) {
                is Success -> {
                    val (value, rest) = result
                    val (valueNew, restNew) = map(rest, value)
                    Success(valueNew, restNew)
                }

                is Error -> result
            }
        }

    inline fun <TNew> map(crossinline map: (TValue) -> TNew) =
        transform { input, value -> map(value) to input }

    inline fun ensure(crossinline error: (TInput) -> Error, crossinline condition: (TValue) -> Boolean): Parser<TInput, TValue> =
        fromLambda { input ->
            when(val parsed = this.parse(input)) {
               is Success -> {
                   val (value, _) = parsed

                   val check = condition(value)
                   if (check)
                       parsed
                   else error(input)
               }

               is Error -> parsed
            }
        }

    infix fun <RValue> erst(other: Parser<TInput, RValue>) =
        combine(other) { fst, _ -> fst }

    infix fun <RValue> then(other: Parser<TInput, RValue>) =
        combine(other) { _, snd -> snd }

    fun many(min: Int = 0, max: Int = Int.MAX_VALUE) = fromLambda<TInput, List<TValue>> { input ->
        val list = mutableListOf<TValue>()

        var error: Error? = null
        var lastRest = input
        while(list.size < max) {
            val result = this.parse(lastRest)
            if (result is Error) {
                if (list.size < min)
                    error = result
                break
            }

            val (value, rest) = result as Success
            list += value
            lastRest = rest
        }

        error ?: Success(list, lastRest)
    }

    infix fun trying(other: Parser<TInput, TValue>): Parser<TInput, TValue> =
        fromLambda { input ->
            when(val first = this.parse(input)) {
                is Success -> when (val second = other.parse(input)) {
                    is Success -> Success(second.value, second.rest)
                    is Error -> Success(first.value, first.rest)
                }

                is Error -> when (val second = other.parse(input)) {
                    is Success -> Success(second.value, second.rest)
                    is Error -> Error(first.errorHolder.either(second.errorHolder))
                }
            }
        }

    fun optional() = many(0, 1)

    fun repeat(num: Int) = many(num, num)

    fun stringify() = map { "$it" }
}