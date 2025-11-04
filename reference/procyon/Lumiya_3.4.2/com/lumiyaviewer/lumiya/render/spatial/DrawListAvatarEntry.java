// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar;
import java.lang.ref.WeakReference;

public class DrawListAvatarEntry extends DrawListObjectEntry
{
    private WeakReference<DrawableAvatar> drawableAvatar;
    private WeakReference<DrawableAvatarStub> drawableAvatarStub;
    @Nonnull
    private final SLObjectAvatarInfo objectAvatarInfo;
    
    public DrawListAvatarEntry(@Nonnull final SLObjectAvatarInfo objectAvatarInfo) {
        super(objectAvatarInfo);
        this.drawableAvatar = null;
        this.drawableAvatarStub = null;
        this.objectAvatarInfo = objectAvatarInfo;
    }
    
    @Override
    public void addToDrawList(@Nonnull final DrawList list) {
        final DrawableAvatarStub drawableAvatarStub = null;
        DrawableAvatar drawableAvatar = null;
        if (list.avatars.size() < list.avatarCountLimit || this.objectAvatarInfo.isMyAvatar()) {
            final WeakReference<DrawableAvatar> drawableAvatar2 = this.drawableAvatar;
            if (drawableAvatar2 != null) {
                drawableAvatar = (DrawableAvatar)drawableAvatar2.get();
            }
            DrawableAvatar myAvatar;
            if ((myAvatar = drawableAvatar) == null) {
                myAvatar = list.drawableStore.drawableAvatarCache.getUnchecked(this.objectAvatarInfo);
                this.drawableAvatar = new WeakReference<DrawableAvatar>(myAvatar);
            }
            list.avatars.add(myAvatar);
            if (this.objectAvatarInfo.isMyAvatar()) {
                list.myAvatar = myAvatar;
            }
        }
        else {
            final WeakReference<DrawableAvatarStub> drawableAvatarStub2 = this.drawableAvatarStub;
            DrawableAvatarStub drawableAvatarStub3 = drawableAvatarStub;
            if (drawableAvatarStub2 != null) {
                drawableAvatarStub3 = drawableAvatarStub2.get();
            }
            DrawableAvatarStub drawableAvatarStub4;
            if ((drawableAvatarStub4 = drawableAvatarStub3) == null) {
                drawableAvatarStub4 = list.drawableStore.drawableAvatarStubCache.getUnchecked(this.objectAvatarInfo);
                this.drawableAvatarStub = new WeakReference<DrawableAvatarStub>(drawableAvatarStub4);
            }
            list.avatarStubs.add(drawableAvatarStub4);
        }
    }
    
    @Nonnull
    public SLObjectAvatarInfo getObjectAvatarInfo() {
        return this.objectAvatarInfo;
    }
}
