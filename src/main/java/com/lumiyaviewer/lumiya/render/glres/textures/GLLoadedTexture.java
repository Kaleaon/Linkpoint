package com.lumiyaviewer.lumiya.render.glres.textures;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.RenderContext;
import java.io.InputStream;
import javax.annotation.Nullable;

public class GLLoadedTexture extends GLResourceTexture {
    private final boolean hasAlphaLayer;
    private final int height;
    private final int width;

    GLLoadedTexture(RenderContext renderContext, Bitmap bitmap) {
        super(renderContext.glResourceManager, bitmap.getHeight() * bitmap.getRowBytes());
        if (renderContext.hasGL20) {
            GLES20.glBindTexture(3553, this.handle);
        } else {
            GLES10.glBindTexture(3553, this.handle);
        }
        this.hasAlphaLayer = bitmap.hasAlpha();
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        renderContext.KeepTexture(bitmap);
        GLUtils.texImage2D(3553, 0, bitmap, 0);
        if (renderContext.hasGL20) {
            GLES20.glTexParameteri(3553, 10240, 9729);
            GLES20.glTexParameteri(3553, 10241, 9729);
            GLES20.glTexParameteri(3553, 10242, 10497);
            GLES20.glTexParameteri(3553, 10243, 10497);
        } else {
            GLES10.glTexParameterf(3553, 10240, 9728.0f);
            GLES10.glTexParameterf(3553, 10241, 9728.0f);
            GLES10.glTexParameterf(3553, 10242, 10497.0f);
            GLES10.glTexParameterf(3553, 10243, 10497.0f);
        }
        if (renderContext.hasGL20) {
            GLES20.glBindTexture(3553, 0);
        } else {
            GLES10.glBindTexture(3553, 0);
        }
    }

    GLLoadedTexture(RenderContext renderContext, OpenJPEG openJPEG) {
        super(renderContext.glResourceManager, openJPEG.getLoadedSize());
        if (renderContext.hasGL20) {
            GLES20.glBindTexture(3553, this.handle);
        } else {
            GLES10.glBindTexture(3553, this.handle);
        }
        this.hasAlphaLayer = openJPEG.hasAlphaLayer();
        this.width = openJPEG.getWidth();
        this.height = openJPEG.getHeight();
        renderContext.KeepTexture(openJPEG);
        if (renderContext.hasGL30) {
            openJPEG.SetAsImmutableTexture();
        } else {
            openJPEG.SetAsTexture();
        }
        if (renderContext.hasGL20) {
            GLES20.glTexParameteri(3553, 10240, 9729);
            GLES20.glTexParameteri(3553, 10241, 9729);
            GLES20.glTexParameteri(3553, 10242, 10497);
            GLES20.glTexParameteri(3553, 10243, 10497);
        } else {
            GLES10.glTexParameterf(3553, 10240, 9728.0f);
            GLES10.glTexParameterf(3553, 10241, 9728.0f);
            GLES10.glTexParameterf(3553, 10242, 10497.0f);
            GLES10.glTexParameterf(3553, 10243, 10497.0f);
        }
        if (renderContext.hasGL20) {
            GLES20.glBindTexture(3553, 0);
        } else {
            GLES10.glBindTexture(3553, 0);
        }
    }

    @Nullable
    public static GLLoadedTexture loadFromAssets(RenderContext renderContext, Context context, String str) {
        try {
            InputStream open = context.getAssets().open(str);
            Bitmap decodeStream = BitmapFactory.decodeStream(open);
            open.close();
            return decodeStream != null ? new GLLoadedTexture(renderContext, decodeStream) : null;
        } catch (Throwable e) {
            Debug.Warning(e);
            return null;
        }
    }

    public final void GLDraw() {
        GLES10.glBindTexture(3553, this.handle);
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public boolean hasAlphaLayer() {
        return this.hasAlphaLayer;
    }
}
