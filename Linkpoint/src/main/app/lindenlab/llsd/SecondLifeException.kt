package lindenlab.llsd

/**
 * Base exception for Second Life protocol errors.
 */
open class SecondLifeException : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable?) : super(message, cause)
}
