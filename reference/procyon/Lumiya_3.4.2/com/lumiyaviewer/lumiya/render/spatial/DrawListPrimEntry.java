// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.render.DrawableStore;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.render.DrawableObject;
import java.lang.ref.WeakReference;

public class DrawListPrimEntry extends DrawListObjectEntry
{
    private volatile WeakReference<DrawableObject> drawableObject;
    
    public DrawListPrimEntry(@Nonnull final SLObjectInfo slObjectInfo) {
        super(slObjectInfo);
        this.drawableObject = null;
    }
    
    @Override
    public void addToDrawList(@Nonnull final DrawList list) {
        final WeakReference<DrawableObject> drawableObject = this.drawableObject;
        DrawableObject drawableObject2;
        if (drawableObject != null) {
            drawableObject2 = drawableObject.get();
        }
        else {
            drawableObject2 = null;
        }
        DrawableObject drawableObject3 = drawableObject2;
        if (drawableObject2 == null) {
            drawableObject3 = new DrawableObject(list.drawableStore, this.objectInfo, null);
            this.drawableObject = new WeakReference<DrawableObject>(drawableObject3);
        }
        list.objects.add(drawableObject3);
    }
    
    @Nonnull
    public DrawableObject getDrawableAttachment(final DrawableStore drawableStore, final DrawableAvatar drawableAvatar) {
        DrawableObject drawableObject = null;
        final WeakReference<DrawableObject> drawableObject2 = this.drawableObject;
        if (drawableObject2 != null) {
            drawableObject = drawableObject2.get();
        }
        DrawableObject referent;
        if ((referent = drawableObject) == null) {
            referent = new DrawableObject(drawableStore, this.objectInfo, drawableAvatar);
            this.drawableObject = new WeakReference<DrawableObject>(referent);
        }
        return referent;
    }
    
    public DrawableObject getDrawableObject() {
        DrawableObject drawableObject = null;
        final WeakReference<DrawableObject> drawableObject2 = this.drawableObject;
        if (drawableObject2 != null) {
            drawableObject = (DrawableObject)drawableObject2.get();
        }
        return drawableObject;
    }
}
