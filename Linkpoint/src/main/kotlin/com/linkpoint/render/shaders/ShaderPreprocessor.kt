package com.linkpoint.render.shaders

import com.google.common.base.Objects
import com.google.common.collect.ImmutableMap
import java.io.BufferedReader
import java.io.IOException
import java.util.Map
import java.util.Map.Entry
import javax.annotation.Nullable

class ShaderPreprocessor {
    private val ImmutableMap<String, String> definedMacros

    public ShaderPreprocessor(Map<String, String> map) {
        this.definedMacros = ImmutableMap.copyOf((Map) map)
    }

     private fun processCode(bufferedReader: BufferedReader, stringBuilder: StringBuilder) throws IOException {
        val str: String = null
        while (true) {
            val readLine: String = bufferedReader.readLine()
            if (readLine == null) {
                return str
            }
            readLine = readLine.trim()
            if (readLine.startsWith("#endif") || readLine.startsWith("#else")) {
                return readLine
            }
            if (readLine.startsWith("#ifdef") || readLine.startsWith("#ifndef")) {
                val startsWith: Boolean = readLine.startsWith("#ifdef")
                val containsKey: Boolean = this.definedMacros.containsKey(readLine.substring(readLine.indexOf(32)).trim())
                val processCode: Object = processCode(bufferedReader, startsWith == containsKey ? stringBuilder : null)
                if (Objects.equal(processCode, "#else")) {
                    processCode = processCode(bufferedReader, startsWith != containsKey ? stringBuilder : null)
                }
                if (!Objects.equal(processCode, "#endif")) {
                    throw IOException("#endif expected")
                }
            } else if (stringBuilder != null) {
                val str2: String = readLine
                for (Entry entry : this.definedMacros.entrySet()) {
                    str2 = str2.replace((CharSequence) entry.getKey(), (CharSequence) entry.getValue())
                }
                stringBuilder.append(str2).append("\r\n")
                str = str2
            }
            str = readLine
        }
    }

     public fun processCode(bufferedReader: BufferedReader) throws IOException {
        val stringBuilder: StringBuilder = StringBuilder()
        processCode(bufferedReader, stringBuilder)
        return stringBuilder.toString()
    }
}
