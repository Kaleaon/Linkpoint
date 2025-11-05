package com.linkpoint.slproto.llsd

/**
 * Raised when an LLSD node cannot be coerced into the requested type.
 */
class LLSDValueTypeException(expectedType: String, node: LLSDNode) :
    LLSDException("Expected $expectedType, but was ${node::class.simpleName}")
