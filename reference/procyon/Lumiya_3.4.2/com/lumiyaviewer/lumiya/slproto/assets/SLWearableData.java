// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.assets;

import java.util.UUID;
import com.lumiyaviewer.lumiya.Debug;
import java.io.UnsupportedEncodingException;
import com.google.common.collect.ImmutableList;

public class SLWearableData
{
    public final String name;
    public final ImmutableList<WearableParam> params;
    public final ImmutableList<WearableTexture> textures;
    
    SLWearableData(byte[] builder) throws WearableFormatException {
        String[] split;
        try {
            split = new String(builder, "ISO-8859-1").trim().split("\n+");
            if (split.length < 2) {
                throw new WearableFormatException();
            }
        }
        catch (final UnsupportedEncodingException ex) {
            throw new WearableFormatException(ex);
        }
        if (!split[0].trim().startsWith("LLWearable")) {
            throw new WearableFormatException();
        }
        while (true) {
            ImmutableList.Builder<WearableTexture> builder2 = null;
        Label_0089:
            while (true) {
                int n = 0;
                Label_0564: {
                    String[] split2 = null;
                    Label_0238: {
                        try {
                            this.name = split[1];
                            builder = (byte[])(Object)ImmutableList.builder();
                            builder2 = ImmutableList.builder();
                            n = 2;
                            if (n >= split.length) {
                                break Label_0089;
                            }
                            split2 = split[n].trim().split("\\s+");
                            if (split2.length < 1) {
                                break Label_0564;
                            }
                            if (!split2[0].equalsIgnoreCase("permissions") && !split2[0].equalsIgnoreCase("sale_info")) {
                                break Label_0238;
                            }
                            ++n;
                            try {
                                if (n >= split.length) {
                                    throw new WearableFormatException();
                                }
                            }
                            catch (final NumberFormatException ex2) {
                                throw new WearableFormatException(ex2);
                            }
                        }
                        catch (final NumberFormatException ex3) {}
                        int n2 = n;
                        if (!split[n].trim().equalsIgnoreCase("{")) {
                            throw new WearableFormatException();
                        }
                        while ((n = n2) < split.length) {
                            if (split[n2].trim().equalsIgnoreCase("}")) {
                                n = n2 + 1;
                                continue Label_0089;
                            }
                            ++n2;
                        }
                        continue Label_0089;
                    }
                    Label_0587: {
                        if (split2[0].equalsIgnoreCase("parameters")) {
                            final int int1 = Integer.parseInt(split2[1]);
                            final int n3 = n + 1;
                            final int n4 = 0;
                            n = n3;
                            if (n4 >= int1) {
                                break Label_0587;
                            }
                            if (n3 >= split.length) {
                                throw new WearableFormatException();
                            }
                            try {
                                if (split[n3].trim().split("\\s+").length < 2) {
                                    throw new WearableFormatException();
                                }
                                goto Label_0346;
                            }
                            catch (final WearableFormatException ex4) {
                                Debug.Warning(ex4);
                            }
                            catch (final NumberFormatException ex5) {
                                Debug.Warning(ex5);
                                goto Label_0337;
                            }
                        }
                        if (split2[0].equalsIgnoreCase("textures")) {
                            final int int2 = Integer.parseInt(split2[1]);
                            final int n5 = n + 1;
                            final int n6 = 0;
                            n = n5;
                            if (n6 >= int2) {
                                break Label_0587;
                            }
                            if (n5 >= split.length) {
                                throw new WearableFormatException();
                            }
                            try {
                                if (split[n5].trim().split("\\s+").length < 2) {
                                    throw new WearableFormatException();
                                }
                                goto Label_0506;
                            }
                            catch (final WearableFormatException ex6) {
                                Debug.Warning(ex6);
                            }
                            catch (final NumberFormatException ex7) {
                                Debug.Warning(ex7);
                                goto Label_0497;
                            }
                        }
                        ++n;
                        continue Label_0089;
                    }
                    continue Label_0089;
                }
                ++n;
                continue Label_0089;
            }
            this.params = ((ImmutableList.Builder<WearableParam>)(Object)builder).build();
            this.textures = builder2.build();
        }
    }
    
    static class WearableFormatException extends AssetFormatException
    {
        WearableFormatException() {
            super("Unsupported wearable format");
        }
        
        WearableFormatException(final Throwable t) {
            super("Unsupported wearable format", t);
        }
    }
    
    public static class WearableParam
    {
        public final int paramIndex;
        public final float paramValue;
        
        WearableParam(final int paramIndex, final float paramValue) {
            this.paramIndex = paramIndex;
            this.paramValue = paramValue;
        }
    }
    
    public static class WearableTexture
    {
        public final int layer;
        public final UUID textureID;
        
        WearableTexture(final int layer, final UUID textureID) {
            this.layer = layer;
            this.textureID = textureID;
        }
    }
}
