package parsec

abstract class ErrorHolder {
    abstract val message: String
    abstract fun either(other: ErrorHolder): ErrorHolder
}