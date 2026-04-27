package com.linkpoint.slproto.llsd

/**
 * Enumeration of LLSD XML element names.
 */
enum class LLSDNodeType(val tagName: String) {
    llsdRoot("llsd"),
    llsdUndef("undef"),
    llsdBoolean("boolean"),
    llsdInteger("integer"),
    llsdDouble("real"),
    llsdUUID("uuid"),
    llsdString("string"),
    llsdDate("date"),
    llsdURI("uri"),
    llsdBinary("binary"),
    llsdArray("array"),
    llsdMap("map"),
    llsdKey("key");

    companion object {
        private val byTag: Map<String, LLSDNodeType> = values().associateBy { it.tagName }

        fun fromTag(tag: String?): LLSDNodeType? = if (tag == null) null else byTag[tag]
    }
}
