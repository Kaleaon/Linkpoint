package com.lumiyaviewer.lumiya.render.shaders

import com.google.common.base.Objects
import com.google.common.collect.ImmutableMap
import java.io.BufferedReader
import java.io.IOException
import javax.annotation.Nullable

/**
 * Preprocessor for shader source code.
 * Handles #ifdef, #ifndef, #else, #endif directives and macro substitution.
 */
class ShaderPreprocessor(map: Map<String, String>) {
    private val definedMacros: ImmutableMap<String, String> = ImmutableMap.copyOf(map)

    /**
     * Process shader code with preprocessor directives.
     * @param bufferedReader Source code reader
     * @return Processed code as string
     * @throws IOException if reading fails or syntax errors occur
     */
    @Throws(IOException::class)
    fun processCode(bufferedReader: BufferedReader): String {
        val stringBuilder = StringBuilder()
        processCode(bufferedReader, stringBuilder)
        return stringBuilder.toString()
    }

    @Nullable
    @Throws(IOException::class)
    private fun processCode(
        bufferedReader: BufferedReader,
        @Nullable stringBuilder: StringBuilder?
    ): String? {
        var lastLine: String? = null
        
        while (true) {
            val readLine = bufferedReader.readLine() ?: return lastLine
            val trimmedLine = readLine.trim()
            
            // Check for directive end markers
            if (trimmedLine.startsWith("#endif") || trimmedLine.startsWith("#else")) {
                return trimmedLine
            }
            
            // Handle conditional compilation
            if (trimmedLine.startsWith("#ifdef") || trimmedLine.startsWith("#ifndef")) {
                val isIfdef = trimmedLine.startsWith("#ifdef")
                val macroName = trimmedLine.substring(trimmedLine.indexOf(' ')).trim()
                val isDefined = definedMacros.containsKey(macroName)
                
                // Process the conditional block
                var nextDirective = processCode(
                    bufferedReader,
                    if (isIfdef == isDefined) stringBuilder else null
                )
                
                // Handle #else block
                if (Objects.equal(nextDirective, "#else")) {
                    nextDirective = processCode(
                        bufferedReader,
                        if (isIfdef != isDefined) stringBuilder else null
                    )
                }
                
                // Verify proper closing
                if (!Objects.equal(nextDirective, "#endif")) {
                    throw IOException("#endif expected")
                }
            } else if (stringBuilder != null) {
                // Process active code - perform macro substitution
                // Replace only whole tokens (macro names) using word boundaries
                var processedLine = trimmedLine
                for (entry in definedMacros.entries) {
                    val pattern = Regex("\\b" + Regex.escape(entry.key) + "\\b")
                    processedLine = pattern.replace(processedLine, entry.value)
                }
                stringBuilder.append(processedLine).append("\r\n")
                lastLine = processedLine
            }
            
            lastLine = trimmedLine
        }
    }
}
