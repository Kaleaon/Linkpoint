// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render;

import java.nio.ByteBuffer;
import android.graphics.Bitmap;

public interface GLTexture
{
    int SetAsTexture();
    
    Bitmap getAsBitmap();
    
    byte getByte(final int p0);
    
    ByteBuffer getExtraComponentsBuffer();
    
    int getHeight();
    
    int getNumComponents();
    
    int getRGB(final int p0);
    
    int getWidth();
}
