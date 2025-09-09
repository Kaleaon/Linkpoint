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
        WeakReference<DrawableObject> weakReference = this.drawableObject;
        DrawableObject drawableObject2 = weakReference != null ? (DrawableObject) weakReference.get() : null;
        if (drawableObject2 == null) {
            drawableObject2 = new DrawableObject(drawList.drawableStore, this.objectInfo, (DrawableAvatar) null);
            this.drawableObject = new WeakReference<>(drawableObject2);
        }
        drawList.objects.add(drawableObject2);
    }

    @Nonnull
    public DrawableObject getDrawableAttachment(DrawableStore drawableStore, DrawableAvatar drawableAvatar) {
        DrawableObject drawableObject2 = null;
        WeakReference<DrawableObject> weakReference = this.drawableObject;
        if (weakReference != null) {
            drawableObject2 = (DrawableObject) weakReference.get();
        }
        if (drawableObject2 != null) {
            return drawableObject2;
        }
        DrawableObject drawableObject3 = new DrawableObject(drawableStore, this.objectInfo, drawableAvatar);
        this.drawableObject = new WeakReference<>(drawableObject3);
        return drawableObject3;
    }

    public DrawableObject getDrawableObject() {
        WeakReference<DrawableObject> weakReference = this.drawableObject;
        if (weakReference != null) {
            return (DrawableObject) weakReference.get();
        }
        return null;
    }
}
