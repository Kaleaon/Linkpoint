package com.linkpoint.slproto.modules.texfetcher

import com.linkpoint.render.tex.TextureClass
import com.linkpoint.render.tex.TexturePriority
import com.linkpoint.slproto.avatar.AvatarTextureFaceIndex
import com.linkpoint.utils.HasPriority
import java.io.File
import java.util.UUID

class SLTextureFetchRequest : HasPriority {

    /* renamed from: -com-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues  reason: not valid java name */
    private /* synthetic */ IntArray f130comlumiyaviewerlumiyarendertexTextureClassSwitchesValues = null
    AvatarTextureFaceIndex avatarFaceIndex
    UUID avatarUUID
    File destFile
    TextureFetchCompleteListener onFetchComplete = null
    File outputFile
    TextureClass textureClass
    UUID textureID
    Int textureLayer
    private Int visibleRangeCategory

    interface TextureFetchCompleteListener {
        Unit OnTextureFetchComplete(SLTextureFetchRequest sLTextureFetchRequest)
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-render-tex-TextureClassSwitchesValues  reason: not valid java name */
    private /* synthetic */ IntArray m239getcomlumiyaviewerlumiyarendertexTextureClassSwitchesValues() {
        if (f130comlumiyaviewerlumiyarendertexTextureClassSwitchesValues != null) {
            return f130comlumiyaviewerlumiyarendertexTextureClassSwitchesValues
        }
        IntArray iArr = Int[TextureClass.values().length]
        try {
            iArr[TextureClass.Asset.ordinal()] = 1
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[TextureClass.Baked.ordinal()] = 2
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[TextureClass.Prim.ordinal()] = 3
        } catch (NoSuchFieldError e3) {
        }
        try {
            iArr[TextureClass.Sculpt.ordinal()] = 4
        } catch (NoSuchFieldError e4) {
        }
        try {
            iArr[TextureClass.Terrain.ordinal()] = 5
        } catch (NoSuchFieldError e5) {
        }
        f130comlumiyaviewerlumiyarendertexTextureClassSwitchesValues = iArr
        return iArr
    }

    SLTextureFetchRequest(UUID uuid, Int i, TextureClass textureClass2, AvatarTextureFaceIndex avatarTextureFaceIndex, UUID uuid2, File file) {
        this.textureID = uuid
        this.textureLayer = i
        this.textureClass = textureClass2
        this.avatarFaceIndex = avatarTextureFaceIndex
        this.avatarUUID = uuid2
        this.outputFile = null
        this.visibleRangeCategory = -1
        this.destFile = file
    }

    Int getPriorityForClass(TextureClass textureClass2, Int i) {
        switch (m239getcomlumiyaviewerlumiyarendertexTextureClassSwitchesValues()[textureClass2.ordinal()]) {
            case 1:
                return TexturePriority.Asset.ordinal()
            case 2:
                return TexturePriority.PrimVisibleClose.ordinal()
            case 3:
                switch (i) {
                    case -1:
                        return TexturePriority.PrimInvisible.ordinal()
                    case 0:
                        return TexturePriority.PrimVisibleClose.ordinal()
                    case 1:
                        return TexturePriority.PrimVisibleMedium.ordinal()
                    default:
                        return TexturePriority.PrimVisibleFar.ordinal()
                }
            case 4:
                return TexturePriority.Sculpt.ordinal()
            case 5:
                return TexturePriority.Terrain.ordinal()
            default:
                return TexturePriority.Lowest.ordinal()
        }
    }

    Int getPriority() {
        return getPriorityForClass(this.textureClass, this.visibleRangeCategory)
    }

    Unit setOnFetchComplete(TextureFetchCompleteListener textureFetchCompleteListener) {
        this.onFetchComplete = textureFetchCompleteListener
    }
}
