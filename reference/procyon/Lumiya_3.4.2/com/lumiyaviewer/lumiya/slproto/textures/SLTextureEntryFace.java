// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.textures;

import javax.annotation.Nullable;
import java.util.UUID;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.utils.InternPool;

public abstract class SLTextureEntryFace
{
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
    private static final InternPool<SLTextureEntryFace> pool;
    
    static {
        pool = new InternPool<SLTextureEntryFace>();
    }
    
    public static SLTextureEntryFace create(final MutableSLTextureEntryFace mutableSLTextureEntryFace) {
        if (mutableSLTextureEntryFace == null) {
            return null;
        }
        return SLTextureEntryFace.pool.intern(new AutoValue_SLTextureEntryFace(mutableSLTextureEntryFace.textureID, mutableSLTextureEntryFace.rgba, mutableSLTextureEntryFace.repeatU, mutableSLTextureEntryFace.repeatV, mutableSLTextureEntryFace.offsetU, mutableSLTextureEntryFace.offsetV, mutableSLTextureEntryFace.rotation, mutableSLTextureEntryFace.glow, mutableSLTextureEntryFace.materialb, mutableSLTextureEntryFace.mediab, mutableSLTextureEntryFace.hasAttribute));
    }
    
    public final float getGlow(@Nonnull final SLTextureEntryFace slTextureEntryFace) {
        float n;
        if ((this.hasAttribute() & 0x200) != 0x0) {
            n = this.glow();
        }
        else {
            n = slTextureEntryFace.glow();
        }
        return n;
    }
    
    public final byte getMaterial(@Nonnull final SLTextureEntryFace slTextureEntryFace) {
        byte b;
        if ((this.hasAttribute() & 0x80) != 0x0) {
            b = this.materialb();
        }
        else {
            b = slTextureEntryFace.materialb();
        }
        return b;
    }
    
    public final byte getMedia(@Nonnull final SLTextureEntryFace slTextureEntryFace) {
        byte b;
        if ((this.hasAttribute() & 0x100) != 0x0) {
            b = this.mediab();
        }
        else {
            b = slTextureEntryFace.mediab();
        }
        return b;
    }
    
    public final float getOffsetU(@Nonnull final SLTextureEntryFace slTextureEntryFace) {
        float n;
        if ((this.hasAttribute() & 0x10) != 0x0) {
            n = this.offsetU();
        }
        else {
            n = slTextureEntryFace.offsetU();
        }
        return n;
    }
    
    public final float getOffsetV(@Nonnull final SLTextureEntryFace slTextureEntryFace) {
        float n;
        if ((this.hasAttribute() & 0x20) != 0x0) {
            n = this.offsetV();
        }
        else {
            n = slTextureEntryFace.offsetV();
        }
        return n;
    }
    
    public final int getRGBA(@Nonnull final SLTextureEntryFace slTextureEntryFace) {
        int n;
        if ((this.hasAttribute() & 0x2) != 0x0) {
            n = this.rgba();
        }
        else {
            n = slTextureEntryFace.rgba();
        }
        return n;
    }
    
    public final float getRepeatU(@Nonnull final SLTextureEntryFace slTextureEntryFace) {
        float n;
        if ((this.hasAttribute() & 0x4) != 0x0) {
            n = this.repeatU();
        }
        else {
            n = slTextureEntryFace.repeatU();
        }
        return n;
    }
    
    public final float getRepeatV(@Nonnull final SLTextureEntryFace slTextureEntryFace) {
        float n;
        if ((this.hasAttribute() & 0x8) != 0x0) {
            n = this.repeatV();
        }
        else {
            n = slTextureEntryFace.repeatV();
        }
        return n;
    }
    
    public final float getRotation(@Nonnull final SLTextureEntryFace slTextureEntryFace) {
        float n;
        if ((this.hasAttribute() & 0x40) != 0x0) {
            n = this.rotation();
        }
        else {
            n = slTextureEntryFace.rotation();
        }
        return n;
    }
    
    @Nullable
    public final UUID getTextureID(@Nonnull final SLTextureEntryFace slTextureEntryFace) {
        UUID uuid;
        if ((this.hasAttribute() & 0x1) != 0x0) {
            uuid = this.textureID();
        }
        else {
            uuid = slTextureEntryFace.textureID();
        }
        return uuid;
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
