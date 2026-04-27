package com.linkpoint.render.glres.textures

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES10
import android.opengl.GLES20
import android.opengl.GLUtils
import com.linkpoint.Debug
import com.linkpoint.openjpeg.OpenJPEG
import com.linkpoint.render.RenderContext
import java.io.IOException
import java.io.InputStream
import javax.annotation.Nullable

class GLLoadedTexture : GLResourceTexture() {
    private val Boolean hasAlphaLayer
    private val Int height
    private val Int width

    GLLoadedTexture(RenderContext renderContext, Bitmap bitmap) {
        super(renderContext.glResourceManager, bitmap.getHeight() * bitmap.getRowBytes())
        if (renderContext.hasGL20) {
            GLES20.glBindTexture(3553, this.handle)
        } else {
            GLES10.glBindTexture(3553, this.handle)
        }
        this.hasAlphaLayer = bitmap.hasAlpha()
        this.width = bitmap.getWidth()
        this.height = bitmap.getHeight()
        renderContext.KeepTexture(bitmap)
        GLUtils.texImage2D(3553, 0, bitmap, 0)
        if (renderContext.hasGL20) {
            GLES20.glTexParameteri(3553, 10240, 9729)
            GLES20.glTexParameteri(3553, 10241, 9729)
            GLES20.glTexParameteri(3553, 10242, 10497)
            GLES20.glTexParameteri(3553, 10243, 10497)
        } else {
            GLES10.glTexParameterf(3553, 10240, 9728.0f)
            GLES10.glTexParameterf(3553, 10241, 9728.0f)
            GLES10.glTexParameterf(3553, 10242, 10497.0f)
            GLES10.glTexParameterf(3553, 10243, 10497.0f)
        }
        if (renderContext.hasGL20) {
            GLES20.glBindTexture(3553, 0)
        } else {
            GLES10.glBindTexture(3553, 0)
        }
    }

    GLLoadedTexture(RenderContext renderContext, OpenJPEG openJPEG) {
        super(renderContext.glResourceManager, openJPEG.getLoadedSize())
        if (renderContext.hasGL20) {
            GLES20.glBindTexture(3553, this.handle)
        } else {
            GLES10.glBindTexture(3553, this.handle)
        }
        this.hasAlphaLayer = openJPEG.hasAlphaLayer()
        this.width = openJPEG.getWidth()
        this.height = openJPEG.getHeight()
        renderContext.KeepTexture(openJPEG)
        if (renderContext.hasGL30) {
            openJPEG.SetAsImmutableTexture()
        } else {
            openJPEG.SetAsTexture()
        }
        if (renderContext.hasGL20) {
            GLES20.glTexParameteri(3553, 10240, 9729)
            GLES20.glTexParameteri(3553, 10241, 9729)
            GLES20.glTexParameteri(3553, 10242, 10497)
            GLES20.glTexParameteri(3553, 10243, 10497)
        } else {
            GLES10.glTexParameterf(3553, 10240, 9728.0f)
            GLES10.glTexParameterf(3553, 10241, 9728.0f)
            GLES10.glTexParameterf(3553, 10242, 10497.0f)
            GLES10.glTexParameterf(3553, 10243, 10497.0f)
        }
        if (renderContext.hasGL20) {
            GLES20.glBindTexture(3553, 0)
        } else {
            GLES10.glBindTexture(3553, 0)
        }
    }

    @JvmStatic
     fun loadFromAssets(renderContext: RenderContext, context: Context, str: String): GLLoadedTexture {
        try {
            val open: InputStream = context.getAssets().open(str)
            val decodeStream: Bitmap = BitmapFactory.decodeStream(open)
            open.close()
            if (decodeStream != null) {
                return GLLoadedTexture(renderContext, decodeStream)
            }
            return null
        } catch (IOException e) {
            Debug.Warning(e)
            return null
        }
    }

    val Unit GLDraw() {
        GLES10.glBindTexture(3553, this.handle)
    }

     public fun getHeight(): Int {
        return this.height
    }

     public fun getWidth(): Int {
        return this.width
    }

     public fun hasAlphaLayer(): Boolean {
        return this.hasAlphaLayer
    }
}
