package com.lumiyaviewer.lumiya.slproto.modules.texfetcher;

import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.render.tex.TexturePriority;
import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex;
import com.lumiyaviewer.lumiya.utils.HasPriority;
import java.io.File;
import java.util.UUID;

/* loaded from: classes.dex */
public class SLTextureFetchRequest implements HasPriority {

    /* renamed from: -com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues, reason: not valid java name */
    private static final /* synthetic */ int[] f130comlumiyaviewerlumiyarendertexTextureClassSwitchesValues = null;
    public AvatarTextureFaceIndex avatarFaceIndex;
    public UUID avatarUUID;
    public final File destFile;
    public TextureClass textureClass;
    public UUID textureID;
    public int textureLayer;
    TextureFetchCompleteListener onFetchComplete = null;
    public File outputFile = null;
    private int visibleRangeCategory = -1;

    /* loaded from: classes.dex */
    public interface TextureFetchCompleteListener {
        void OnTextureFetchComplete(SLTextureFetchRequest sLTextureFetchRequest);
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues, reason: not valid java name */
    private static /* synthetic */ int[] m249x8a7b09f7() {
        if (f130comlumiyaviewerlumiyarendertexTextureClassSwitchesValues != null) {
            return f130comlumiyaviewerlumiyarendertexTextureClassSwitchesValues;
        }
        int[] iArr = new int[TextureClass.valuesCustom().length];
        try {
            iArr[TextureClass.Asset.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[TextureClass.Baked.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[TextureClass.Prim.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[TextureClass.Sculpt.ordinal()] = 4;
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[TextureClass.Terrain.ordinal()] = 5;
        } catch (NoSuchFieldError e5) {
        }
        f130comlumiyaviewerlumiyarendertexTextureClassSwitchesValues = iArr;
        return iArr;
    }

    public SLTextureFetchRequest(UUID uuid, int i, TextureClass textureClass, AvatarTextureFaceIndex avatarTextureFaceIndex, UUID uuid2, File file) {
        this.textureID = uuid;
        this.textureLayer = i;
        this.textureClass = textureClass;
        this.avatarFaceIndex = avatarTextureFaceIndex;
        this.avatarUUID = uuid2;
        this.destFile = file;
    }

    public static int getPriorityForClass(TextureClass textureClass, int i) {
        switch (m249x8a7b09f7()[textureClass.ordinal()]) {
            case 1:
                return TexturePriority.Asset.ordinal();
            case 2:
                return TexturePriority.PrimVisibleClose.ordinal();
            case 3:
                switch (i) {
                    case -1:
                        return TexturePriority.PrimInvisible.ordinal();
                    case 0:
                        return TexturePriority.PrimVisibleClose.ordinal();
                    case 1:
                        return TexturePriority.PrimVisibleMedium.ordinal();
                    default:
                        return TexturePriority.PrimVisibleFar.ordinal();
                }
            case 4:
                return TexturePriority.Sculpt.ordinal();
            case 5:
                return TexturePriority.Terrain.ordinal();
            default:
                return TexturePriority.Lowest.ordinal();
        }
    }

    @Override // com.lumiyaviewer.lumiya.utils.HasPriority
    public int getPriority() {
        return getPriorityForClass(this.textureClass, this.visibleRangeCategory);
    }

    public void setOnFetchComplete(TextureFetchCompleteListener textureFetchCompleteListener) {
        this.onFetchComplete = textureFetchCompleteListener;
    }
}
