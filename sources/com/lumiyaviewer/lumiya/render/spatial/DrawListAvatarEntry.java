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

    /* JADX WARNING: type inference failed for: r0v14, types: [com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub, java.lang.Object] */
    /* JADX WARNING: type inference failed for: r0v16, types: [com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub] */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void addToDrawList(@javax.annotation.Nonnull com.lumiyaviewer.lumiya.render.spatial.DrawList r4) {
        /*
            r3 = this;
            r0 = 0
            java.util.ArrayList<com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar> r1 = r4.avatars
            int r1 = r1.size()
            int r2 = r4.avatarCountLimit
            if (r1 < r2) goto L_0x0013
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo r1 = r3.objectAvatarInfo
            boolean r1 = r1.isMyAvatar()
            if (r1 == 0) goto L_0x0042
        L_0x0013:
            java.lang.ref.WeakReference<com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar> r1 = r3.drawableAvatar
            if (r1 == 0) goto L_0x001d
            java.lang.Object r0 = r1.get()
            com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar r0 = (com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar) r0
        L_0x001d:
            if (r0 != 0) goto L_0x0032
            com.lumiyaviewer.lumiya.render.DrawableStore r0 = r4.drawableStore
            com.google.common.cache.LoadingCache<com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo, com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar> r0 = r0.drawableAvatarCache
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo r1 = r3.objectAvatarInfo
            java.lang.Object r0 = r0.getUnchecked(r1)
            com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar r0 = (com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar) r0
            java.lang.ref.WeakReference r1 = new java.lang.ref.WeakReference
            r1.<init>(r0)
            r3.drawableAvatar = r1
        L_0x0032:
            java.util.ArrayList<com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar> r1 = r4.avatars
            r1.add(r0)
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo r1 = r3.objectAvatarInfo
            boolean r1 = r1.isMyAvatar()
            if (r1 == 0) goto L_0x0041
            r4.myAvatar = r0
        L_0x0041:
            return
        L_0x0042:
            java.lang.ref.WeakReference<com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub> r1 = r3.drawableAvatarStub
            if (r1 == 0) goto L_0x004c
            java.lang.Object r0 = r1.get()
            com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub r0 = (com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub) r0
        L_0x004c:
            if (r0 != 0) goto L_0x0061
            com.lumiyaviewer.lumiya.render.DrawableStore r0 = r4.drawableStore
            com.google.common.cache.LoadingCache<com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo, com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub> r0 = r0.drawableAvatarStubCache
            com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo r1 = r3.objectAvatarInfo
            java.lang.Object r0 = r0.getUnchecked(r1)
            com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub r0 = (com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub) r0
            java.lang.ref.WeakReference r1 = new java.lang.ref.WeakReference
            r1.<init>(r0)
            r3.drawableAvatarStub = r1
        L_0x0061:
            java.util.ArrayList<com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub> r1 = r4.avatarStubs
            r1.add(r0)
            goto L_0x0041
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.render.spatial.DrawListAvatarEntry.addToDrawList(com.lumiyaviewer.lumiya.render.spatial.DrawList):void");
    }

    @Nonnull
    public SLObjectAvatarInfo getObjectAvatarInfo() {
        return this.objectAvatarInfo;
    }
}
