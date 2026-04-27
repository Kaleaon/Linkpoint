package com.linkpoint.slproto.template

import com.linkpoint.Debug
import java.io.Reader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.ConcurrentHashMap

object MessageTemplateRegistry {

    private val templatesById = ConcurrentHashMap<Int, TemplateMessage>()
    @Volatile
    private var loaded = false

    fun getTemplateById(id: Int): TemplateMessage? {
        ensureLoaded()
        return templatesById[id]
    }

    fun allTemplates(): Collection<TemplateMessage> {
        ensureLoaded()
        return templatesById.values
    }

    private fun ensureLoaded() {
        if (!loaded) {
            synchronized(this) {
                if (!loaded) {
                    loadDefault()
                    loaded = true
                }
            }
        }
    }

    private fun loadDefault() {
        val explicitPath = System.getProperty("linkpoint.template.path")?.takeIf { it.isNotBlank() }
        val candidates = buildList {
            if (explicitPath != null) add(explicitPath)
            add("Linkpoint/src/main/assets/message_template.msg")
            add("message_template.msg")
            add("../scripts/messages/message_template.msg")
            add("scripts/messages/message_template.msg")
        }

        for (candidate in candidates) {
            try {
                val path: Path = Paths.get(candidate)
                if (Files.exists(path)) {
                    Files.newBufferedReader(path).use { loadFromReader(it) }
                    Debug.Log("Loaded message templates from $candidate")
                    return
                }
            } catch (_: Exception) {
                // Ignore and try next candidate
            }
        }

        val resourceStream = MessageTemplateRegistry::class.java.classLoader?.getResourceAsStream("message_template.msg")
        if (resourceStream != null) {
            resourceStream.bufferedReader().use { loadFromReader(it) }
            Debug.Log("Loaded message templates from classpath resource")
            return
        }

        Debug.Log("Message template file not found; template-based decoding disabled")
    }

    fun loadFromReader(reader: Reader) {
        val content = reader.readText()
        val parser = MessageTemplateParser(content)
        val parsed = parser.parse()
        templatesById.clear()
        parsed.forEach { template ->
            templatesById[template.id] = template
        }
    }
}
