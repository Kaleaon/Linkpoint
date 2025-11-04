// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.texfetcher;

import com.lumiyaviewer.lumiya.render.tex.TexturePriority;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import java.io.File;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex;
import com.lumiyaviewer.lumiya.utils.HasPriority;

public class SLTextureFetchRequest implements HasPriority
{
    public AvatarTextureFaceIndex avatarFaceIndex;
    public UUID avatarUUID;
    public final File destFile;
    TextureFetchCompleteListener onFetchComplete;
    public File outputFile;
    public TextureClass textureClass;
    public UUID textureID;
    public int textureLayer;
    private int visibleRangeCategory;
    
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues() {
        if (SLTextureFetchRequest.-com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues != null) {
            return SLTextureFetchRequest.-com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues = new int[TextureClass.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues[TextureClass.Asset.ordinal()] = 1;
                try {
                    -com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues[TextureClass.Baked.ordinal()] = 2;
                    try {
                        -com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues[TextureClass.Prim.ordinal()] = 3;
                        try {
                            -com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues[TextureClass.Sculpt.ordinal()] = 4;
                            try {
                                -com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues[TextureClass.Terrain.ordinal()] = 5;
                                return -com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues = -com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues;
                            }
                            catch (final NoSuchFieldError noSuchFieldError) {}
                        }
                        catch (final NoSuchFieldError noSuchFieldError2) {}
                    }
                    catch (final NoSuchFieldError noSuchFieldError3) {}
                }
                catch (final NoSuchFieldError noSuchFieldError4) {}
            }
            catch (final NoSuchFieldError noSuchFieldError5) {
                continue;
            }
            break;
        }
    }
    
    public SLTextureFetchRequest(final UUID textureID, final int textureLayer, final TextureClass textureClass, final AvatarTextureFaceIndex avatarFaceIndex, final UUID avatarUUID, final File destFile) {
        this.onFetchComplete = null;
        this.textureID = textureID;
        this.textureLayer = textureLayer;
        this.textureClass = textureClass;
        this.avatarFaceIndex = avatarFaceIndex;
        this.avatarUUID = avatarUUID;
        this.outputFile = null;
        this.visibleRangeCategory = -1;
        this.destFile = destFile;
    }
    
    public static int getPriorityForClass(final TextureClass textureClass, final int n) {
        switch (-getcom-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues()[textureClass.ordinal()]) {
            default: {
                return TexturePriority.Lowest.ordinal();
            }
            case 1: {
                return TexturePriority.Asset.ordinal();
            }
            case 2: {
                return TexturePriority.PrimVisibleClose.ordinal();
            }
            case 3: {
                switch (n) {
                    default: {
                        return TexturePriority.PrimVisibleFar.ordinal();
                    }
                    case -1: {
                        return TexturePriority.PrimInvisible.ordinal();
                    }
                    case 0: {
                        return TexturePriority.PrimVisibleClose.ordinal();
                    }
                    case 1: {
                        return TexturePriority.PrimVisibleMedium.ordinal();
                    }
                }
                break;
            }
            case 4: {
                return TexturePriority.Sculpt.ordinal();
            }
            case 5: {
                return TexturePriority.Terrain.ordinal();
            }
        }
    }
    
    @Override
    public int getPriority() {
        return getPriorityForClass(this.textureClass, this.visibleRangeCategory);
    }
    
    public void setOnFetchComplete(final TextureFetchCompleteListener onFetchComplete) {
        this.onFetchComplete = onFetchComplete;
    }
    
    public interface TextureFetchCompleteListener
    {
        void OnTextureFetchComplete(final SLTextureFetchRequest p0);
    }
}
