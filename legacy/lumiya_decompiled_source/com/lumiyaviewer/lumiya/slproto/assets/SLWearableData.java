package com.lumiyaviewer.lumiya.slproto.assets;

import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
/* loaded from: classes.dex */
public class SLWearableData {
    public final String name;
    public final ImmutableList<WearableParam> params;
    public final ImmutableList<WearableTexture> textures;

    /* loaded from: classes.dex */
    static class WearableFormatException extends AssetFormatException {
        WearableFormatException() {
            super("Unsupported wearable format");
        }

        WearableFormatException(Throwable th) {
            super("Unsupported wearable format", th);
        }
    }

    /* loaded from: classes.dex */
    public static class WearableParam {
        public final int paramIndex;
        public final float paramValue;

        WearableParam(int i, float f) {
            this.paramIndex = i;
            this.paramValue = f;
        }
    }

    /* loaded from: classes.dex */
    public static class WearableTexture {
        public final int layer;
        public final UUID textureID;

        WearableTexture(int i, UUID uuid) {
            this.layer = i;
            this.textureID = uuid;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SLWearableData(byte[] bArr) throws WearableFormatException {
        int i;
        String[] split;
        String[] split2;
        try {
            String[] split3 = new String(bArr, "ISO-8859-1").trim().split("\n+");
            if (split3.length < 2) {
                throw new WearableFormatException();
            }
            if (!split3[0].trim().startsWith("LLWearable")) {
                throw new WearableFormatException();
            }
            try {
                this.name = split3[1];
                ImmutableList.Builder builder = ImmutableList.builder();
                ImmutableList.Builder builder2 = ImmutableList.builder();
                int i2 = 2;
                loop0: while (i2 < split3.length) {
                    String[] split4 = split3[i2].trim().split("\\s+");
                    if (split4.length < 1) {
                        i2++;
                    } else if (split4[0].equalsIgnoreCase("permissions") || split4[0].equalsIgnoreCase("sale_info")) {
                        i2++;
                        if (i2 >= split3.length) {
                            throw new WearableFormatException();
                        }
                        if (!split3[i2].trim().equalsIgnoreCase("{")) {
                            throw new WearableFormatException();
                        }
                        while (true) {
                            if (i2 >= split3.length) {
                                break;
                            } else if (split3[i2].trim().equalsIgnoreCase("}")) {
                                i2++;
                                break;
                            } else {
                                i2++;
                            }
                        }
                    } else if (split4[0].equalsIgnoreCase("parameters")) {
                        int parseInt = Integer.parseInt(split4[1]);
                        i = i2 + 1;
                        for (int i3 = 0; i3 < parseInt; i3++) {
                            if (i >= split3.length) {
                                throw new WearableFormatException();
                            }
                            try {
                                try {
                                    split = split3[i].trim().split("\\s+");
                                } catch (WearableFormatException e) {
                                    Debug.Warning(e);
                                }
                            } catch (NumberFormatException e2) {
                                Debug.Warning(e2);
                            }
                            if (split.length < 2) {
                                throw new WearableFormatException();
                                break loop0;
                            }
                            builder.add((ImmutableList.Builder) new WearableParam(Integer.parseInt(split[0]), Float.parseFloat(split[1])));
                            i++;
                        }
                        i2 = i;
                    } else if (split4[0].equalsIgnoreCase("textures")) {
                        int parseInt2 = Integer.parseInt(split4[1]);
                        i = i2 + 1;
                        for (int i4 = 0; i4 < parseInt2; i4++) {
                            if (i >= split3.length) {
                                throw new WearableFormatException();
                            }
                            try {
                                try {
                                    split2 = split3[i].trim().split("\\s+");
                                } catch (WearableFormatException e3) {
                                    Debug.Warning(e3);
                                }
                            } catch (NumberFormatException e4) {
                                Debug.Warning(e4);
                            }
                            if (split2.length < 2) {
                                throw new WearableFormatException();
                                break loop0;
                            }
                            builder2.add((ImmutableList.Builder) new WearableTexture(Integer.parseInt(split2[0]), UUID.fromString(split2[1])));
                            i++;
                        }
                        i2 = i;
                    } else {
                        i2++;
                    }
                }
                this.params = builder.build();
                this.textures = builder2.build();
            } catch (NumberFormatException e5) {
                throw new WearableFormatException(e5);
            }
        } catch (UnsupportedEncodingException e6) {
            throw new WearableFormatException(e6);
        }
    }
}
