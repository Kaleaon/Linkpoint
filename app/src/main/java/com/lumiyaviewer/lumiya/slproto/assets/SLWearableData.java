package com.lumiyaviewer.lumiya.slproto.assets;

import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class SLWearableData {
    public final String name;
    public final ImmutableList<WearableParam> params;
    public final ImmutableList<WearableTexture> textures;

    static class WearableFormatException extends AssetFormatException {
        WearableFormatException() {
            super("Unsupported wearable format");
        }

        WearableFormatException(Throwable th) {
            super("Unsupported wearable format", th);
        }
    }

    public static class WearableParam {
        public final int paramIndex;
        public final float paramValue;

        WearableParam(int i, float f) {
            this.paramIndex = i;
            this.paramValue = f;
        }
    }

    public static class WearableTexture {
        public final int layer;
        public final UUID textureID;

        WearableTexture(int i, UUID uuid) {
            this.layer = i;
            this.textureID = uuid;
        }
    }

    SLWearableData(byte[] bArr) throws WearableFormatException {
        try {
            String[] split = new String(bArr, "ISO-8859-1").trim().split("\n+");
            if (split.length < 2) {
                throw new WearableFormatException();
            } else if (!split[0].trim().startsWith("LLWearable")) {
                throw new WearableFormatException();
            } else {
                try {
                    this.name = split[1];
                    ImmutableList.Builder builder = ImmutableList.builder();
                    ImmutableList.Builder builder2 = ImmutableList.builder();
                    int i2 = 2;
                    while (i2 < split.length) {
                        String[] split2 = split[i2].trim().split("\\s+");
                        if (split2.length < 1) {
                            i2++;
                        } else if (split2[0].equalsIgnoreCase("permissions") || split2[0].equalsIgnoreCase("sale_info")) {
                            i2++;
                            if (i2 >= split.length) {
                                throw new WearableFormatException();
                            } else if (!split[i2].trim().equalsIgnoreCase("{")) {
                                throw new WearableFormatException();
                            } else {
                                while (true) {
                                    if (i2 >= split.length) {
                                        break;
                                    } else if (split[i2].trim().equalsIgnoreCase("}")) {
                                        i2++;
                                        break;
                                    } else {
                                        i2++;
                                    }
                                }
                            }
                        } else {
                            if (split2[0].equalsIgnoreCase("parameters")) {
                                int parseInt = Integer.parseInt(split2[1]);
                                i = i2 + 1;
                                int i3 = 0;
                                while (i3 < parseInt) {
                                    if (i >= split.length) {
                                        throw new WearableFormatException();
                                    }
                                    try {
                                        String[] split3 = split[i].trim().split("\\s+");
                                        if (split3.length < 2) {
                                            throw new WearableFormatException();
                                        }
                                        builder.add((Object) new WearableParam(Integer.parseInt(split3[0]), Float.parseFloat(split3[1])));
                                        i++;
                                        i3++;
                                    } catch (WearableFormatException e) {
                                        Debug.Warning(e);
                                    } catch (NumberFormatException e2) {
                                        Debug.Warning(e2);
                                    }
                                }
                            } else if (split2[0].equalsIgnoreCase("textures")) {
                                int parseInt2 = Integer.parseInt(split2[1]);
                                int i4 = i2 + 1;
                                int i5 = 0;
                                while (i5 < parseInt2) {
                                    if (i >= split.length) {
                                        throw new WearableFormatException();
                                    }
                                    try {
                                        String[] split4 = split[i].trim().split("\\s+");
                                        if (split4.length < 2) {
                                            throw new WearableFormatException();
                                        }
                                        builder2.add((Object) new WearableTexture(Integer.parseInt(split4[0]), UUID.fromString(split4[1])));
                                        i4 = i + 1;
                                        i5++;
                                    } catch (WearableFormatException e3) {
                                        Debug.Warning(e3);
                                    } catch (NumberFormatException e4) {
                                        Debug.Warning(e4);
                                    }
                                }
                            } else {
                                i2++;
                            }
                            i2 = i;
                        }
                    }
                    this.params = builder.build();
                    this.textures = builder2.build();
                } catch (NumberFormatException e5) {
                    throw new WearableFormatException(e5);
                }
            }
        } catch (UnsupportedEncodingException e6) {
            throw new WearableFormatException(e6);
        }
    }
}
