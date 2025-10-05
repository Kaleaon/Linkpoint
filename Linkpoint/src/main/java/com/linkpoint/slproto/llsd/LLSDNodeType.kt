package com.linkpoint.slproto.llsd

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
        private val tagMap: Map<String, LLSDNodeType> = values().associateBy { it.tagName }

        @JvmStatic
        fun byTag(tag: String): LLSDNodeType? {
            return tagMap[tag]
        }
    }
}