package com.linkpoint.slproto.llsd

/**
 * Thrown when requesting a missing key from an LLSD map node.
 */
class LLSDInvalidKeyException(message: String) : LLSDException(message)
