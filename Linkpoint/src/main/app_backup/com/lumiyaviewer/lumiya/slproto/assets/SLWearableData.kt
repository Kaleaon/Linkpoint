package com.lumiyaviewer.lumiya.slproto.assets

import com.google.common.collect.ImmutableList
import com.lumiyaviewer.lumiya.Debug
import java.io.UnsupportedEncodingException
import java.util.UUID

class SLWearableData {
    var name: String = ""
    lateinit var params: ImmutableList<WearableParam>
    lateinit var textures: ImmutableList<WearableTexture>

    class WearableFormatException : AssetFormatException {
        constructor() : super("Unsupported wearable format")
        constructor(cause: Throwable) : super("Unsupported wearable format", cause)
    }

    class WearableParam(val paramIndex: Int, val paramValue: Float)

    class WearableTexture(val layer: Int, val textureID: UUID)

    constructor(data: ByteArray) {
        try {
            val content = String(data, charset("ISO-8859-1")).trim()
            val lines = content.split(Regex("\n+"))
            
            if (lines.size < 2 || !lines[0].trim().startsWith("LLWearable")) {
                throw WearableFormatException()
            }
            
            try {
                name = lines[1]
                val paramBuilder = ImmutableList.builder<WearableParam>()
                val textureBuilder = ImmutableList.builder<WearableTexture>()
                
                var i = 2
                while (i < lines.size) {
                    val line = lines[i].trim()
                    val parts = line.split(Regex("\\s+"))
                    
                    if (parts.isEmpty() || parts[0].isEmpty()) {
                        i++
                        continue
                    }
                    
                    if (parts[0].equals("permissions", ignoreCase = true) || 
                        parts[0].equals("sale_info", ignoreCase = true)) {
                        
                        i++
                        if (i >= lines.size || !lines[i].trim().equals("{", ignoreCase = true)) {
                            throw WearableFormatException()
                        }
                        
                        while (i < lines.size) {
                            if (lines[i].trim().equals("}", ignoreCase = true)) {
                                i++
                                break
                            }
                            i++
                        }
                    } else if (parts[0].equals("parameters", ignoreCase = true)) {
                        val count = Integer.parseInt(parts[1])
                        i++
                        
                        var processed = 0
                        while (processed < count && i < lines.size) {
                            try {
                                val pParts = lines[i].trim().split(Regex("\\s+"))
                                if (pParts.size < 2) throw WearableFormatException()
                                
                                paramBuilder.add(WearableParam(
                                    Integer.parseInt(pParts[0]),
                                    pParts[1].toFloat()
                                ))
                            } catch (e: Exception) {
                                Debug.Warning(e)
                            }
                            i++
                            processed++
                        }
                    } else if (parts[0].equals("textures", ignoreCase = true)) {
                        val count = Integer.parseInt(parts[1])
                        i++
                        
                        var processed = 0
                        while (processed < count && i < lines.size) {
                            try {
                                val tParts = lines[i].trim().split(Regex("\\s+"))
                                if (tParts.size < 2) throw WearableFormatException()
                                
                                textureBuilder.add(WearableTexture(
                                    Integer.parseInt(tParts[0]),
                                    UUID.fromString(tParts[1])
                                ))
                            } catch (e: Exception) {
                                Debug.Warning(e)
                            }
                            i++
                            processed++
                        }
                    } else {
                        i++
                    }
                }
                
                params = paramBuilder.build()
                textures = textureBuilder.build()
                
            } catch (e: NumberFormatException) {
                throw WearableFormatException(e)
            }
        } catch (e: UnsupportedEncodingException) {
            throw WearableFormatException(e)
        }
    }
}
