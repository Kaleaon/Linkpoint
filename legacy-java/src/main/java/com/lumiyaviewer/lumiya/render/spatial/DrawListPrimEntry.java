package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.render.DrawableObject;
import com.lumiyaviewer.lumiya.render.DrawableStore;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import java.lang.ref.WeakReference;
import javax.annotation.Nonnull;

/* loaded from: classes.dex */
public class DrawListPrimEntry extends DrawListObjectEntry {
    private volatile WeakReference<DrawableObject> drawableObject;

    public DrawListPrimEntry(@Nonnull SLObjectInfo sLObjectInfo) {
        super(sLObjectInfo);
        this.drawableObject = null;
    }

    @Override // com.lumiyaviewer.lumiya.render.spatial.DrawListEntry
    public void addToDrawList(@Nonnull DrawList drawList) {
        WeakReference<DrawableObject> weakReference = this.drawableObject;
        DrawableObject drawableObject = weakReference != null ? weakReference.get() : null;
        if (drawableObject == null) {
            drawableObject = new DrawableObject(drawList.drawableStore, this.objectInfo, null);
            this.drawableObject = new WeakReference<>(drawableObject);
        }
        drawList.objects.add(drawableObject);
    }

    @Nonnull
    public DrawableObject getDrawableAttachment(DrawableStore drawableStore, DrawableAvatar drawableAvatar) {
        WeakReference<DrawableObject> weakReference = this.drawableObject;
        DrawableObject drawableObject = weakReference != null ? weakReference.get() : null;
        if (drawableObject != null) {
            return drawableObject;
        }
        DrawableObject drawableObject2 = new DrawableObject(drawableStore, this.objectInfo, drawableAvatar);
        this.drawableObject = new WeakReference<>(drawableObject2);
        return drawableObject2;
    }

    public DrawableObject getDrawableObject() {
        WeakReference<DrawableObject> weakReference = this.drawableObject;
        if (weakReference != null) {
            return weakReference.get();
        }
        return null;
    }
}
