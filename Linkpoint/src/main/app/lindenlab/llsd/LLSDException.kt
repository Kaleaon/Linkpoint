package lindenlab.llsd

class LLSDException : SecondLifeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable?) : super(message, cause)
}
