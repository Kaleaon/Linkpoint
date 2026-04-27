package com.linkpoint.slproto.llsd

/**
 * Base exception for LLSD parsing/serialization errors.
 */
open class LLSDException(message: String, cause: Throwable? = null) : Exception(message, cause)
