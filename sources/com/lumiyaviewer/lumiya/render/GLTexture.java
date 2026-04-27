// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render;

import android.graphics.Bitmap;
import java.nio.ByteBuffer;

public interface GLTexture
{

    public abstract int SetAsTexture();

    public abstract Bitmap getAsBitmap();

    public abstract byte getByte(int i);

    public abstract ByteBuffer getExtraComponentsBuffer();

    public abstract int getHeight();

    public abstract int getNumComponents();

    public abstract int getRGB(int i);

    public abstract int getWidth();
}
