package com.lumiyaviewer.lumiya.render;

import android.graphics.Bitmap;
import java.nio.ByteBuffer;

public interface GLTexture {
    int SetAsTexture();

    Bitmap getAsBitmap();

    byte getByte(int i);

    ByteBuffer getExtraComponentsBuffer();

    int getHeight();

    int getNumComponents();

    int getRGB(int i);

    int getWidth();
}
