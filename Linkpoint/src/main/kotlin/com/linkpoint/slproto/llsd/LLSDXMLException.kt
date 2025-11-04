package com.linkpoint.slproto.llsd

/**
 * Signals malformed or unsupported LLSD XML input.
 */
class LLSDXMLException(message: String, cause: Throwable? = null) : LLSDException(message, cause)
