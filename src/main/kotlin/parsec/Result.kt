package parsec

import java.lang.System.err

sealed class Result<out TInput, out TValue> {
    data class Success<TInput, TValue>(val value: TValue, val rest: TInput): Result<TInput, TValue>()
    class Error(val errorHolder: ErrorHolder): Result<Nothing, Nothing>()

    fun unwrap(): TValue = when(this) {
        is Success -> this.value
        is Error -> {
            err.println(errorHolder.message)
            throw ParsecException()
        }
    }
}