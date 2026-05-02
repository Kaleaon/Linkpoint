package com.linkpoint.protocol.messages

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class MessageTemplateProtocolConformanceTest {

    @Test
    fun `message template entries have explicit classification`() {
        val templateMessages = parseTemplateMessages()

        val supported = MessageIdRegistry.supportedTemplateMessages
        val declaredOnly = MessageIdRegistry.declaredOnlyTemplateMessages
        val deprecated = MessageIdRegistry.deprecatedTemplateMessages
        val declaredUserCritical = MessageTemplateCatalog.declaredOnlyUserCriticalMessagesWithRationale
        val declaredMediumUtility = MessageTemplateCatalog.declaredOnlyMediumUtilityMessagesWithRationale
        val declaredLowPriorityAdmin = MessageTemplateCatalog.declaredOnlyLowPriorityAdminMessagesWithRationale

        val duplicateClassifications = templateMessages.filter { message ->
            listOf(
                message.name in supported,
                message.name in declaredOnly,
                message.name in deprecated,
            ).count { it } > 1
        }

        val missingClassification = templateMessages.filter { message ->
            message.name !in supported && message.name !in declaredOnly && message.name !in deprecated
        }

        val blankDeclaredRationales = declaredOnly.filterValues { it.isBlank() }.keys.sorted()
        val blankDeprecatedRationales = deprecated.filterValues { it.isBlank() }.keys.sorted()
        val templateDeprecatedWithoutTable = templateMessages
            .filter { it.isDeprecated && it.name !in deprecated }
            .map { it.name }
            .sorted()

        val reportFile = File(
            System.getProperty("user.dir"),
            "build/reports/protocol/message-template-classification-report.txt",
        )
        reportFile.parentFile?.mkdirs()
        reportFile.writeText(
            buildString {
                appendLine("Message Template Conformance Report")
                appendLine("template_count=${templateMessages.size}")
                appendLine("supported_count=${templateMessages.count { it.name in supported }}")
                appendLine("declared_only_count=${templateMessages.count { it.name in declaredOnly }}")
                appendLine("declared_user_critical_count=${templateMessages.count { it.name in declaredUserCritical }}")
                appendLine("declared_medium_utility_count=${templateMessages.count { it.name in declaredMediumUtility }}")
                appendLine("declared_low_priority_admin_count=${templateMessages.count { it.name in declaredLowPriorityAdmin }}")
                appendLine("deprecated_count=${templateMessages.count { it.name in deprecated }}")
                appendLine("missing_classification_count=${missingClassification.size}")
                appendLine("duplicate_classification_count=${duplicateClassifications.size}")
                appendLine()

                appendLine("[MISSING_CLASSIFICATION]")
                if (missingClassification.isEmpty()) appendLine("none")
                missingClassification.forEach { appendLine("- ${it.name}") }
                appendLine()

                appendLine("[DUPLICATE_CLASSIFICATION]")
                if (duplicateClassifications.isEmpty()) appendLine("none")
                duplicateClassifications.forEach { appendLine("- ${it.name}") }
                appendLine()

                appendLine("[TEMPLATE_DEPRECATED_WITHOUT_TABLE]")
                if (templateDeprecatedWithoutTable.isEmpty()) appendLine("none")
                templateDeprecatedWithoutTable.forEach { appendLine("- $it") }
                appendLine()

                appendLine("[DECLARED_ONLY_MESSAGES]")
                declaredOnly.toSortedMap().forEach { (name, rationale) ->
                    appendLine("- $name :: $rationale")
                }
                appendLine()

                appendLine("[DECLARED_ONLY_USER_CRITICAL]")
                declaredUserCritical.toSortedMap().forEach { (name, rationale) ->
                    appendLine("- $name :: $rationale")
                }
                appendLine()

                appendLine("[DECLARED_ONLY_MEDIUM_UTILITY]")
                declaredMediumUtility.toSortedMap().forEach { (name, rationale) ->
                    appendLine("- $name :: $rationale")
                }
                appendLine()

                appendLine("[DECLARED_ONLY_LOW_PRIORITY_ADMIN]")
                declaredLowPriorityAdmin.toSortedMap().forEach { (name, rationale) ->
                    appendLine("- $name :: $rationale")
                }
                appendLine()

                appendLine("[DEPRECATED_MESSAGES]")
                deprecated.toSortedMap().forEach { (name, rationale) ->
                    appendLine("- $name :: $rationale")
                }
            },
        )

        assertTrue(
            "message_template.msg contains messages with duplicate classification: ${duplicateClassifications.joinToString { it.name }}",
            duplicateClassifications.isEmpty(),
        )
        assertTrue(
            "message_template.msg contains messages without explicit classification: ${missingClassification.joinToString { it.name }}",
            missingClassification.isEmpty(),
        )
        assertTrue(
            "Declared-only table contains blank rationale values: ${blankDeclaredRationales.joinToString()}",
            blankDeclaredRationales.isEmpty(),
        )
        assertTrue(
            "Deprecated table contains blank rationale values: ${blankDeprecatedRationales.joinToString()}",
            blankDeprecatedRationales.isEmpty(),
        )
        assertTrue(
            "Template marks messages deprecated but deprecated table is missing entries: ${templateDeprecatedWithoutTable.joinToString()}",
            templateDeprecatedWithoutTable.isEmpty(),
        )
    }

    private fun parseTemplateMessages(): List<TemplateMessage> {
        val template = javaClass.classLoader!!
            .getResource("protocol/message_template.msg")
            ?.readText()
            ?: error("Missing protocol/message_template.msg test resource")

        val messageRegex = Regex(
            "^\\s*([A-Za-z][A-Za-z0-9_]*)\\s+(Low|Medium|High|Fixed)\\s+(\\d+)\\s+([^\\n]+)$",
            setOf(RegexOption.MULTILINE),
        )

        val seen = linkedSetOf<String>()
        val parsed = mutableListOf<TemplateMessage>()
        messageRegex.findAll(template).forEach { match ->
            val name = match.groupValues[1]
            if (!seen.add(name)) return@forEach
            val trailer = match.groupValues[4]
            parsed += TemplateMessage(name = name, isDeprecated = trailer.contains("Deprecated"))
        }

        return parsed
    }

    private data class TemplateMessage(
        val name: String,
        val isDeprecated: Boolean,
    )
}
