package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import java.lang.ref.WeakReference;
import javax.annotation.Nonnull;

/* loaded from: classes.dex */
public class DrawListAvatarEntry extends DrawListObjectEntry {
    private WeakReference<DrawableAvatar> drawableAvatar;
    private WeakReference<DrawableAvatarStub> drawableAvatarStub;

    @Nonnull
    private final SLObjectAvatarInfo objectAvatarInfo;

    public DrawListAvatarEntry(@Nonnull SLObjectAvatarInfo sLObjectAvatarInfo) {
        super(sLObjectAvatarInfo);
        this.drawableAvatar = null;
        this.drawableAvatarStub = null;
        this.objectAvatarInfo = sLObjectAvatarInfo;
    }

    @Override // com.lumiyaviewer.lumiya.render.spatial.DrawListEntry
    public void addToDrawList(@Nonnull DrawList drawList) {
        if (drawList.avatars.size() >= drawList.avatarCountLimit && !this.objectAvatarInfo.isMyAvatar()) {
            WeakReference<DrawableAvatarStub> weakReference = this.drawableAvatarStub;
            DrawableAvatarStub drawableAvatarStub = weakReference != null ? weakReference.get() : null;
            if (drawableAvatarStub == null) {
                drawableAvatarStub = drawList.drawableStore.drawableAvatarStubCache.getUnchecked(this.objectAvatarInfo);
                this.drawableAvatarStub = new WeakReference<>(drawableAvatarStub);
            }
            drawList.avatarStubs.add(drawableAvatarStub);
            return;
        }
        WeakReference<DrawableAvatar> weakReference2 = this.drawableAvatar;
        DrawableAvatar drawableAvatar = weakReference2 != null ? weakReference2.get() : null;
        if (drawableAvatar == null) {
            drawableAvatar = drawList.drawableStore.drawableAvatarCache.getUnchecked(this.objectAvatarInfo);
            this.drawableAvatar = new WeakReference<>(drawableAvatar);
        }
        drawList.avatars.add(drawableAvatar);
        if (this.objectAvatarInfo.isMyAvatar()) {
            drawList.myAvatar = drawableAvatar;
        }
    }

    @Nonnull
    public SLObjectAvatarInfo getObjectAvatarInfo() {
        return this.objectAvatarInfo;
    }
}
