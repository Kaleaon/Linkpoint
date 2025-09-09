package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.render.DrawableObject;
import com.lumiyaviewer.lumiya.render.DrawableStore;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import java.lang.ref.WeakReference;
import javax.annotation.Nonnull;

public class DrawListPrimEntry extends DrawListObjectEntry {
    private volatile WeakReference<DrawableObject> drawableObject = null;

    public DrawListPrimEntry(@Nonnull SLObjectInfo sLObjectInfo) {
        super(sLObjectInfo);
    }

    public void addToDrawList(@Nonnull DrawList drawList) {
        WeakReference weakReference = this.drawableObject;
        Object obj = weakReference != null ? (DrawableObject) weakReference.get() : null;
        if (obj == null) {
            obj = new DrawableObject(drawList.drawableStore, this.objectInfo, null);
            this.drawableObject = new WeakReference(obj);
        }
        drawList.objects.add(obj);
    }

    @Nonnull
    public DrawableObject getDrawableAttachment(DrawableStore drawableStore, DrawableAvatar drawableAvatar) {
        DrawableObject drawableObject = null;
        WeakReference weakReference = this.drawableObject;
        if (weakReference != null) {
            drawableObject = (DrawableObject) weakReference.get();
        }
        if (drawableObject != null) {
            return drawableObject;
        }
        drawableObject = new DrawableObject(drawableStore, this.objectInfo, drawableAvatar);
        this.drawableObject = new WeakReference(drawableObject);
        return drawableObject;
    }

    public DrawableObject getDrawableObject() {
        WeakReference weakReference = this.drawableObject;
        return weakReference != null ? (DrawableObject) weakReference.get() : null;
    }
}
