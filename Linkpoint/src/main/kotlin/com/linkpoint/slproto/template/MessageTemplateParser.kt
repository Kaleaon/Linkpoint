package com.linkpoint.slproto.template

import java.lang.Long.decode

internal class MessageTemplateParser(content: String) {

    private val tokens: List<String>
    private var index = 0

    init {
        tokens = tokenize(content)
    }

    fun parse(): List<TemplateMessage> {
        val messages = mutableListOf<TemplateMessage>()
        while (index < tokens.size) {
            val token = tokens[index]
            if (token == "{") {
                index++
                parseMessage()?.let { messages += it }
            } else {
                index++
            }
        }
        return messages
    }

    private fun parseMessage(): TemplateMessage? {
        if (!hasMore()) return null

        val name = nextToken()
        val frequencyToken = nextTokenOrNull() ?: return null
        val frequency = TemplateFrequency.fromToken(frequencyToken)

        val idToken = nextTokenOrNull() ?: return null
        val messageId = parseInt(idToken) ?: return null

        var trust = TemplateTrust.NOT_TRUSTED
        var encoding = TemplateEncoding.UNENCODED

        while (hasMore()) {
            val peek = peekToken()
            if (peek == "{") break
            index++
            when (peek.lowercase()) {
                "trusted" -> trust = TemplateTrust.TRUSTED
                "nottrusted" -> trust = TemplateTrust.NOT_TRUSTED
                "zerocoded" -> encoding = TemplateEncoding.ZEROCODED
                "unencoded" -> encoding = TemplateEncoding.UNENCODED
                else -> {
                    // ignore additional flags
                }
            }
        }

        val blocks = mutableListOf<TemplateBlock>()
        while (hasMore() && tokens[index] == "{") {
            index++
            parseBlock()?.let { blocks += it }
        }

        expectToken("}")

        return TemplateMessage(
            name = name,
            id = messageId,
            frequency = frequency,
            trust = trust,
            encoding = encoding,
            blocks = blocks
        )
    }

    private fun parseBlock(): TemplateBlock? {
        if (!hasMore()) return null

        val name = nextToken()
        val typeToken = nextTokenOrNull() ?: return null
        val blockType = TemplateBlockType.fromToken(typeToken)

        var repeat: Int? = null
        if (blockType == TemplateBlockType.MULTIPLE && hasMore()) {
            val peek = peekToken()
            if (peek != "{") {
                repeat = parseInt(peek)
                index++
            }
        }

        val fields = mutableListOf<TemplateField>()
        while (hasMore() && tokens[index] == "{") {
            index++
            parseField()?.let { fields += it }
        }

        expectToken("}")

        return TemplateBlock(name = name, type = blockType, repeat = repeat, fields = fields)
    }

    private fun parseField(): TemplateField? {
        if (!hasMore()) return null

        val name = nextToken()
        val type = nextTokenOrNull() ?: return null

        var param: Int? = null
        if (hasMore()) {
            val peek = peekToken()
            if (peek != "}") {
                param = parseInt(peek)
                if (param != null) {
                    index++
                }
            }
        }

        expectToken("}")

        return TemplateField(name = name, rawType = type, param = param)
    }

    private fun tokenize(content: String): List<String> {
        val pattern = Regex("\\{|\\}|[^\\s{}]+")
        val tokens = mutableListOf<String>()
        content.lineSequence().forEach { rawLine ->
            val line = rawLine.substringBefore("//")
            pattern.findAll(line).forEach { match ->
                tokens += match.value
            }
        }
        return tokens
    }

    private fun parseInt(token: String): Int? {
        return try {
            when {
                token.startsWith("0x", ignoreCase = true) -> decode(token).toInt()
                token.startsWith("-0x", ignoreCase = true) -> (-decode(token.removePrefix("-")).toInt())
                else -> token.toInt()
            }
        } catch (_: NumberFormatException) {
            null
        }
    }

    private fun expectToken(expected: String) {
        if (!hasMore()) return
        if (tokens[index] == expected) {
            index++
        }
    }

    private fun nextToken(): String {
        return tokens[index++]
    }

    private fun nextTokenOrNull(): String? = if (hasMore()) nextToken() else null

    private fun peekToken(): String = tokens[index]

    private fun hasMore(): Boolean = index < tokens.size
}
