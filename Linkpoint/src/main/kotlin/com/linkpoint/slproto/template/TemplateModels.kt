package com.linkpoint.slproto.template

enum class TemplateFrequency {
    HIGH, MEDIUM, LOW, FIXED;

    companion object {
        fun fromToken(token: String): TemplateFrequency = when (token.lowercase()) {
            "high" -> HIGH
            "medium" -> MEDIUM
            "low" -> LOW
            "fixed" -> FIXED
            else -> LOW
        }
    }
}

enum class TemplateTrust {
    TRUSTED, NOT_TRUSTED;

    companion object {
        fun fromToken(token: String): TemplateTrust = when (token.lowercase()) {
            "trusted" -> TRUSTED
            "nottrusted" -> NOT_TRUSTED
            else -> NOT_TRUSTED
        }
    }
}

enum class TemplateEncoding {
    UNENCODED, ZEROCODED;

    companion object {
        fun fromToken(token: String): TemplateEncoding = when (token.lowercase()) {
            "zerocoded" -> ZEROCODED
            "unencoded" -> UNENCODED
            else -> UNENCODED
        }
    }
}

enum class TemplateBlockType {
    SINGLE, MULTIPLE, VARIABLE;

    companion object {
        fun fromToken(token: String): TemplateBlockType = when (token.lowercase()) {
            "single" -> SINGLE
            "multiple" -> MULTIPLE
            "variable" -> VARIABLE
            else -> SINGLE
        }
    }
}

data class TemplateMessage(
    val name: String,
    val id: Int,
    val frequency: TemplateFrequency,
    val trust: TemplateTrust,
    val encoding: TemplateEncoding,
    val blocks: List<TemplateBlock>
)

data class TemplateBlock(
    val name: String,
    val type: TemplateBlockType,
    val repeat: Int?,
    val fields: List<TemplateField>
)

data class TemplateField(
    val name: String,
    val rawType: String,
    val param: Int?
)
