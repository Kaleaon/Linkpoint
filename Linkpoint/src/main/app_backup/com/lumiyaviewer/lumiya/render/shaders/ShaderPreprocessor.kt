package com.lumiyaviewer.lumiya.render.shaders

import java.io.BufferedReader
import java.io.IOException
import java.util.HashMap

class ShaderPreprocessor(map: Map<String, String>) {
    private val definedMacros: Map<String, String>

    init {
        definedMacros = HashMap(map)
    }

    @Throws(IOException::class)
    fun processCode(bufferedReader: BufferedReader): String {
        val stringBuilder = StringBuilder()
        processCode(bufferedReader, stringBuilder)
        return stringBuilder.toString()
    }

    @Throws(IOException::class)
    private fun processCode(bufferedReader: BufferedReader, stringBuilder: StringBuilder?): String? {
        var line: String?
        while (bufferedReader.readLine().also { line = it } != null) {
            var currentLine = line!!.trim()
            if (currentLine.startsWith("#endif") || currentLine.startsWith("#else")) {
                return currentLine
            }
            if (currentLine.startsWith("#ifdef") || currentLine.startsWith("#ifndef")) {
                val isIfDef = currentLine.startsWith("#ifdef")
                val macroName = currentLine.substring(currentLine.indexOf(' ')).trim()
                val isDefined = definedMacros.containsKey(macroName)
                
                // Recursively process block
                var processedLine = processCode(bufferedReader, if (isIfDef == isDefined) stringBuilder else null)
                
                if (processedLine == "#else") {
                    processedLine = processCode(bufferedReader, if (isIfDef != isDefined) stringBuilder else null)
                }
                
                if (processedLine != "#endif") {
                    throw IOException("#endif expected")
                }
            } else {
                if (stringBuilder != null) {
                    for ((key, value) in definedMacros) {
                        currentLine = currentLine.replace(key, value)
                    }
                    stringBuilder.append(currentLine).append("\r\n")
                }
            }
        }
        return null
    }
}
