package com.linkpoint.slproto.assets

import com.google.common.collect.ImmutableList
import com.linkpoint.Debug
import java.io.UnsupportedEncodingException
import java.util.UUID

class SLWearableData {
    val String name
    val ImmutableList<WearableParam> params
    val ImmutableList<WearableTexture> textures

    static class WearableFormatException : AssetFormatException() {
        WearableFormatException() {
            super("Unsupported wearable format")
        }

        WearableFormatException(Throwable th) {
            super("Unsupported wearable format", th)
        }
    }

    @JvmStatic
    class WearableParam {
        val Int paramIndex
        val Float paramValue

        WearableParam(Int i, Float f) {
            this.paramIndex = i
            this.paramValue = f
        }
    }

    @JvmStatic
    class WearableTexture {
        val Int layer
        val UUID textureID

        WearableTexture(Int i, UUID uuid) {
            this.layer = i
            this.textureID = uuid
        }
    }

    SLWearableData(ByteArray bArr) throws WearableFormatException {
        try {
            val split: Array<String> = String(bArr, "ISO-8859-1").trim().split("\n+")
            if (split.length < 2) {
                throw WearableFormatException()
            } else if (!split[0].trim().startsWith("LLWearable")) {
                throw WearableFormatException()
            } else {
                try {
                    this.name = split[1]
                    ImmutableList.Builder builder = ImmutableList.builder()
                    ImmutableList.Builder builder2 = ImmutableList.builder()
                    val i2: Int = 2
                    while (i2 < split.length) {
                        val split2: Array<String> = split[i2].trim().split("\\s+")
                        if (split2.length < 1) {
                            i2++
                        } else if (split2[0].equalsIgnoreCase("permissions") || split2[0].equalsIgnoreCase("sale_info")) {
                            i2++
                            if (i2 >= split.length) {
                                throw WearableFormatException()
                            } else if (!split[i2].trim().equalsIgnoreCase("{")) {
                                throw WearableFormatException()
                            } else {
                                while (true) {
                                    if (i2 >= split.length) {
                                        break
                                    } else if (split[i2].trim().equalsIgnoreCase("}")) {
                                        i2++
                                        break
                                    } else {
                                        i2++
                                    }
                                }
                            }
                        } else {
                            if (split2[0].equalsIgnoreCase("parameters")) {
                                val parseInt: Int = Integer.parseInt(split2[1])
                                i = i2 + 1
                                val i3: Int = 0
                                while (i3 < parseInt) {
                                    if (i >= split.length) {
                                        throw WearableFormatException()
                                    }
                                    try {
                                        val split3: Array<String> = split[i].trim().split("\\s+")
                                        if (split3.length < 2) {
                                            throw WearableFormatException()
                                        }
                                        builder.add((Object) WearableParam(Integer.parseInt(split3[0]), Float.parseFloat(split3[1])))
                                        i++
                                        i3++
                                    } catch (WearableFormatException e) {
                                        Debug.Warning(e)
                                    } catch (NumberFormatException e2) {
                                        Debug.Warning(e2)
                                    }
                                }
                            } else if (split2[0].equalsIgnoreCase("textures")) {
                                val parseInt2: Int = Integer.parseInt(split2[1])
                                val i4: Int = i2 + 1
                                val i5: Int = 0
                                while (i5 < parseInt2) {
                                    if (i >= split.length) {
                                        throw WearableFormatException()
                                    }
                                    try {
                                        val split4: Array<String> = split[i].trim().split("\\s+")
                                        if (split4.length < 2) {
                                            throw WearableFormatException()
                                        }
                                        builder2.add((Object) WearableTexture(Integer.parseInt(split4[0]), UUID.fromString(split4[1])))
                                        i4 = i + 1
                                        i5++
                                    } catch (WearableFormatException e3) {
                                        Debug.Warning(e3)
                                    } catch (NumberFormatException e4) {
                                        Debug.Warning(e4)
                                    }
                                }
                            } else {
                                i2++
                            }
                            i2 = i
                        }
                    }
                    this.params = builder.build()
                    this.textures = builder2.build()
                } catch (NumberFormatException e5) {
                    throw WearableFormatException(e5)
                }
            }
        } catch (UnsupportedEncodingException e6) {
            throw WearableFormatException(e6)
        }
    }
}
