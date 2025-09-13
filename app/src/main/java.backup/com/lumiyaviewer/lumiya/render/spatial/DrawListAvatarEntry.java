package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import java.lang.ref.WeakReference;
import javax.annotation.Nonnull;

public class DrawListAvatarEntry extends DrawListObjectEntry {
    private WeakReference<DrawableAvatar> drawableAvatar = null;
    private WeakReference<DrawableAvatarStub> drawableAvatarStub = null;
    @Nonnull
    private final SLObjectAvatarInfo objectAvatarInfo;

    public DrawListAvatarEntry(@Nonnull SLObjectAvatarInfo sLObjectAvatarInfo) {
        super(sLObjectAvatarInfo);
        this.objectAvatarInfo = sLObjectAvatarInfo;
    }

    public void addToDrawList(@Nonnull DrawList drawList) {
        Object obj = null;
        WeakReference weakReference;
        if (drawList.avatars.size() < drawList.avatarCountLimit || this.objectAvatarInfo.isMyAvatar()) {
            DrawableAvatar drawableAvatar;
            weakReference = this.drawableAvatar;
            if (weakReference != null) {
                drawableAvatar = (DrawableAvatar) weakReference.get();
            }
            if (drawableAvatar == null) {
                drawableAvatar = (DrawableAvatar) drawList.drawableStore.drawableAvatarCache.getUnchecked(this.objectAvatarInfo);
                this.drawableAvatar = new WeakReference(drawableAvatar);
            }
            drawList.avatars.add(drawableAvatar);
            if (this.objectAvatarInfo.isMyAvatar()) {
                drawList.myAvatar = drawableAvatar;
                return;
            }
            return;
        }
        weakReference = this.drawableAvatarStub;
        if (weakReference != null) {
            obj = (DrawableAvatarStub) weakReference.get();
        }
        if (obj == null) {
            obj = (DrawableAvatarStub) drawList.drawableStore.drawableAvatarStubCache.getUnchecked(this.objectAvatarInfo);
            this.drawableAvatarStub = new WeakReference(obj);
        }
        drawList.avatarStubs.add(obj);
    }

    @Nonnull
    public SLObjectAvatarInfo getObjectAvatarInfo() {
        return this.objectAvatarInfo;
    }
}
