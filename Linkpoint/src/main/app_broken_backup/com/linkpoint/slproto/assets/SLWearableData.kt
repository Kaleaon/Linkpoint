package com.linkpoint.slproto.assets

import com.google.common.collect.ImmutableList
import com.linkpoint.Debug
import java.io.UnsupportedEncodingException
import java.util.UUID

class SLWearableData {
    String name
    ImmutableList<WearableParam> params
    ImmutableList<WearableTexture> textures

    class WearableFormatException : AssetFormatException {
        WearableFormatException() {
            super("Unsupported wearable format")
        }

        WearableFormatException(Throwable th) {
            super("Unsupported wearable format", th)
        }
    }

    class WearableParam {
        Int paramIndex
        Float paramValue

        WearableParam(Int i, Float f) {
            this.paramIndex = i
            this.paramValue = f
        }
    }

    class WearableTexture {
        Int layer
        UUID textureID

        WearableTexture(Int i, UUID uuid) {
            this.layer = i
            this.textureID = uuid
        }
    }

    SLWearableData(ByteArray bArr) throws WearableFormatException {
        try {
            Array<String> split = String(bArr, "ISO-8859-1").trim().split("\n+")
            if (split.size < 2) {
                throw WearableFormatException()
            } else if (!split[0].trim().startsWith("LLWearable")) {
                throw WearableFormatException()
            } else {
                try {
                    this.name = split[1]
                    ImmutableList.Builder builder = ImmutableList.builder()
                    ImmutableList.Builder builder2 = ImmutableList.builder()
                    var i2: Int = 2
                    while (i2 < split.size) {
                        Array<String> split2 = split[i2].trim().split("\\s+")
                        if (split2.size < 1) {
                            i2++
                        } else if (split2[0].equalsIgnoreCase("permissions") || split2[0].equalsIgnoreCase("sale_info")) {
                            i2++
                            if (i2 >= split.size) {
                                throw WearableFormatException()
                            } else if (!split[i2].trim().equalsIgnoreCase("{")) {
                                throw WearableFormatException()
                            } else {
                                while (true) {
                                    if (i2 >= split.size) {
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
                                var parseInt: Int = Int.parseInt(split2[1])
                                i = i2 + 1
                                var i3: Int = 0
                                while (i3 < parseInt) {
                                    if (i >= split.size) {
                                        throw WearableFormatException()
                                    }
                                    try {
                                        Array<String> split3 = split[i].trim().split("\\s+")
                                        if (split3.size < 2) {
                                            throw WearableFormatException()
                                        }
                                        builder.add((Any) WearableParam(Int.parseInt(split3[0]), Float.parseFloat(split3[1])))
                                        i++
                                        i3++
                                    } catch (WearableFormatException e) {
                                        Debug.Warning(e)
                                    } catch (NumberFormatException e2) {
                                        Debug.Warning(e2)
                                    }
                                }
                            } else if (split2[0].equalsIgnoreCase("textures")) {
                                var parseInt2: Int = Int.parseInt(split2[1])
                                var i4: Int = i2 + 1
                                var i5: Int = 0
                                while (i5 < parseInt2) {
                                    if (i >= split.size) {
                                        throw WearableFormatException()
                                    }
                                    try {
                                        Array<String> split4 = split[i].trim().split("\\s+")
                                        if (split4.size < 2) {
                                            throw WearableFormatException()
                                        }
                                        builder2.add((Any) WearableTexture(Int.parseInt(split4[0]), UUID.fromString(split4[1])))
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
