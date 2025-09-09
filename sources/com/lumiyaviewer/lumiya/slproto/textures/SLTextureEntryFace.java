package com.lumiyaviewer.lumiya.slproto.textures;

import com.lumiyaviewer.lumiya.utils.InternPool;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class SLTextureEntryFace {
    public static final int AttributeAll = -1;
    static final int AttributeGlow = 512;
    static final int AttributeMaterial = 128;
    static final int AttributeMedia = 256;
    static final int AttributeOffsetU = 16;
    static final int AttributeOffsetV = 32;
    static final int AttributeRGBA = 2;
    static final int AttributeRepeatU = 4;
    static final int AttributeRepeatV = 8;
    static final int AttributeRotation = 64;
    static final int AttributeTextureID = 1;
    private static final InternPool<SLTextureEntryFace> pool = new InternPool<>();

    public static SLTextureEntryFace create(MutableSLTextureEntryFace mutableSLTextureEntryFace) {
        if (mutableSLTextureEntryFace == null) {
            return null;
        }
        return pool.intern(new AutoValue_SLTextureEntryFace(mutableSLTextureEntryFace.textureID, mutableSLTextureEntryFace.rgba, mutableSLTextureEntryFace.repeatU, mutableSLTextureEntryFace.repeatV, mutableSLTextureEntryFace.offsetU, mutableSLTextureEntryFace.offsetV, mutableSLTextureEntryFace.rotation, mutableSLTextureEntryFace.glow, mutableSLTextureEntryFace.materialb, mutableSLTextureEntryFace.mediab, mutableSLTextureEntryFace.hasAttribute));
    }

    public final float getGlow(@Nonnull SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 512) != 0 ? glow() : sLTextureEntryFace.glow();
    }

    public final byte getMaterial(@Nonnull SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 128) != 0 ? materialb() : sLTextureEntryFace.materialb();
    }

    public final byte getMedia(@Nonnull SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 256) != 0 ? mediab() : sLTextureEntryFace.mediab();
    }

    public final float getOffsetU(@Nonnull SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 16) != 0 ? offsetU() : sLTextureEntryFace.offsetU();
    }

    public final float getOffsetV(@Nonnull SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 32) != 0 ? offsetV() : sLTextureEntryFace.offsetV();
    }

    public final int getRGBA(@Nonnull SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 2) != 0 ? rgba() : sLTextureEntryFace.rgba();
    }

    public final float getRepeatU(@Nonnull SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 4) != 0 ? repeatU() : sLTextureEntryFace.repeatU();
    }

    public final float getRepeatV(@Nonnull SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 8) != 0 ? repeatV() : sLTextureEntryFace.repeatV();
    }

    public final float getRotation(@Nonnull SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 64) != 0 ? rotation() : sLTextureEntryFace.rotation();
    }

    @Nullable
    public final UUID getTextureID(@Nonnull SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 1) != 0 ? textureID() : sLTextureEntryFace.textureID();
    }

    public abstract float glow();

    public abstract int hasAttribute();

    public abstract byte materialb();

    public abstract byte mediab();

    public abstract float offsetU();

    public abstract float offsetV();

    public abstract float repeatU();

    public abstract float repeatV();

    public abstract int rgba();

    public abstract float rotation();

    @Nullable
    public abstract UUID textureID();
}
