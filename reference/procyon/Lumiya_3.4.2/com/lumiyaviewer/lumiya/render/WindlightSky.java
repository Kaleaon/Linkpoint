// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render;

import java.util.Arrays;
import java.io.InputStream;
import android.content.res.AssetManager;
import java.io.IOException;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.LumiyaApp;
import android.opengl.GLES20;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ShortBuffer;
import java.nio.FloatBuffer;
import com.lumiyaviewer.lumiya.render.glres.textures.GLResourceTexture;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLBuffer;
import android.annotation.SuppressLint;

@SuppressLint({ "InlinedApi" })
public class WindlightSky
{
    private static final int NumStars = 500;
    private static final float Q;
    private static final int SKY_INDEX_BUFFER = 1;
    private static final int SKY_VERTEX_BUFFER = 0;
    private static final int STARS_INDEX_BUFFER = 3;
    private static final int STARS_VERTEX_BUFFER = 2;
    private static final float StarsRadius = 0.8f;
    private static final float ico1;
    private static final float icoQ;
    private static final float icoRadius;
    private static final short[] icosahedronIndices;
    private static final float[] icosahedronVertices;
    private final GLBuffer[] buffers;
    private final GLResourceTexture cloudsTexture;
    private MatrixStack skyMatrix;
    private FloatBuffer starsCoords;
    private ShortBuffer starsIndices;
    
    static {
        Q = (float)((Math.sqrt(5.0) + 1.0) / 2.0);
        icoRadius = (float)Math.sqrt(WindlightSky.Q * WindlightSky.Q + 1.0f);
        ico1 = 1.0f / WindlightSky.icoRadius;
        icoQ = WindlightSky.Q / WindlightSky.icoRadius;
        icosahedronVertices = new float[] { 0.0f, WindlightSky.ico1, WindlightSky.icoQ, 0.0f, -WindlightSky.ico1, WindlightSky.icoQ, 0.0f, -WindlightSky.ico1, -WindlightSky.icoQ, 0.0f, WindlightSky.ico1, -WindlightSky.icoQ, WindlightSky.icoQ, 0.0f, WindlightSky.ico1, -WindlightSky.icoQ, 0.0f, WindlightSky.ico1, -WindlightSky.icoQ, 0.0f, -WindlightSky.ico1, WindlightSky.icoQ, 0.0f, -WindlightSky.ico1, WindlightSky.ico1, -WindlightSky.icoQ, 0.0f, -WindlightSky.ico1, -WindlightSky.icoQ, 0.0f, -WindlightSky.ico1, WindlightSky.icoQ, 0.0f, WindlightSky.ico1, WindlightSky.icoQ, 0.0f };
        icosahedronIndices = new short[] { 5, 0, 1, 10, 0, 5, 5, 1, 9, 10, 5, 6, 6, 5, 9, 11, 0, 10, 3, 11, 10, 3, 10, 6, 3, 6, 2, 7, 3, 2, 8, 7, 2, 4, 7, 8, 1, 4, 8, 9, 8, 2, 9, 2, 6, 11, 3, 7, 4, 0, 11, 4, 11, 7, 1, 0, 4, 1, 8, 9 };
    }
    
    public WindlightSky(final RenderContext renderContext) {
        this.buffers = new GLBuffer[4];
        this.skyMatrix = null;
        this.starsCoords = FloatBuffer.allocate(1500);
        this.starsIndices = ShortBuffer.allocate(500);
        final LLVector3 llVector3 = new LLVector3();
        for (int i = 0; i < 500; ++i) {
            llVector3.set(0.8f, 0.0f, 0.0f);
            final LLQuaternion llQuaternion = new LLQuaternion((float)Math.random() * 2.0f - 1.0f, (float)Math.random() * 2.0f - 1.0f, (float)Math.random() * 2.0f - 1.0f, (float)Math.random() * 2.0f - 1.0f);
            llQuaternion.normalize();
            llVector3.mul(llQuaternion);
            this.starsCoords.put(llVector3.x);
            this.starsCoords.put(llVector3.y);
            this.starsCoords.put(llVector3.z);
            this.starsIndices.put((short)i);
        }
        if (renderContext.skyProgram.hasCloudsTexture()) {
            this.cloudsTexture = this.loadClouds(renderContext);
        }
        else {
            this.cloudsTexture = null;
        }
        final FloatBuffer wrap = FloatBuffer.wrap(WindlightSky.icosahedronVertices);
        final ShortBuffer wrap2 = ShortBuffer.wrap(WindlightSky.icosahedronIndices);
        for (int j = 0; j < this.buffers.length; ++j) {
            this.buffers[j] = new GLBuffer(renderContext.glResourceManager, null);
        }
        GLES20.glBindBuffer(34962, this.buffers[0].handle);
        GLES20.glBufferData(34962, WindlightSky.icosahedronVertices.length * 4, wrap.position(), 35044);
        GLES20.glBindBuffer(34963, this.buffers[1].handle);
        GLES20.glBufferData(34963, WindlightSky.icosahedronIndices.length * 2, wrap2.position(), 35044);
        GLES20.glUseProgram(renderContext.skyProgram.getHandle());
        GLES20.glEnableVertexAttribArray(renderContext.skyProgram.vPosition);
        GLES20.glVertexAttribPointer(renderContext.skyProgram.vPosition, 3, 5126, false, 12, 0);
        GLES20.glBindBuffer(34962, this.buffers[2].handle);
        GLES20.glBufferData(34962, this.starsCoords.capacity() * 4, this.starsCoords.position(), 35044);
        GLES20.glBindBuffer(34963, this.buffers[3].handle);
        GLES20.glBufferData(34963, this.starsIndices.capacity() * 2, this.starsIndices.position(), 35044);
        GLES20.glUseProgram(renderContext.starsProgram.getHandle());
        GLES20.glEnableVertexAttribArray(renderContext.starsProgram.vPosition);
        GLES20.glVertexAttribPointer(renderContext.starsProgram.vPosition, 3, 5126, false, 12, 0);
    }
    
    private GLResourceTexture loadClouds(final RenderContext renderContext) {
        try {
            final AssetManager assetManager = LumiyaApp.getAssetManager();
            final int[] array2;
            final int[] array = array2 = new int[6];
            array2[0] = 34070;
            array2[1] = 34072;
            array2[2] = 34074;
            array2[3] = 34069;
            array2[4] = 34071;
            array2[5] = 34073;
            final OpenJPEG[] array3 = new OpenJPEG[array.length];
            int i;
            int n;
            int loadedSize;
            for (i = 0, n = 0; i < array.length; ++i, n += loadedSize) {
                final InputStream open = assetManager.open("windlight/" + (new String[] { "clouds_nx.tga", "clouds_py.tga", "clouds_nz.tga", "clouds_px.tga", "clouds_ny.tga", "clouds_pz.tga" })[i]);
                final OpenJPEG openJPEG = new OpenJPEG(open, OpenJPEG.ImageFormat.TGA, false, false, 0.0f, 0.0f, true);
                open.close();
                Debug.Printf("WindlightSky: texture %dx%d,  numcomps %d, bpp %d", openJPEG.width, openJPEG.height, openJPEG.num_components, openJPEG.bytes_per_pixel);
                array3[i] = openJPEG;
                loadedSize = openJPEG.getLoadedSize();
            }
            final GLResourceTexture glResourceTexture = new GLResourceTexture(renderContext.glResourceManager, n);
            GLES20.glBindTexture(34067, glResourceTexture.handle);
            for (int j = 0; j < array.length; ++j) {
                array3[j].SetAsTextureTarget(array[j]);
                array3[j] = null;
            }
            GLES20.glTexParameteri(34067, 10240, 9729);
            GLES20.glTexParameteri(34067, 10241, 9729);
            GLES20.glTexParameteri(34067, 10242, 10497);
            GLES20.glTexParameteri(34067, 10243, 10497);
            return glResourceTexture;
        }
        catch (final IOException ex) {
            Debug.Warning(ex);
            return null;
        }
    }
    
    public void GLDraw(final RenderContext renderContext, final float n, final float n2) {
        if (this.skyMatrix == null) {
            return;
        }
        GLES20.glDisable(2884);
        GLES20.glDisable(3042);
        GLES20.glDepthFunc(515);
        this.skyMatrix.glPushMatrix();
        if (n2 != 0.0f) {
            this.skyMatrix.glRotatef(n2, 1.0f, 0.0f, 0.0f);
        }
        this.skyMatrix.glRotatef(-n + 90.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glUseProgram(renderContext.skyProgram.getHandle());
        renderContext.skyProgram.ApplyWindlight(renderContext);
        this.skyMatrix.glApplyUniformMatrix(renderContext.skyProgram.uMVPMatrix);
        GLES20.glBindBuffer(34962, this.buffers[0].handle);
        GLES20.glEnableVertexAttribArray(renderContext.skyProgram.vPosition);
        GLES20.glVertexAttribPointer(renderContext.skyProgram.vPosition, 3, 5126, false, 12, 0);
        GLES20.glBindBuffer(34963, this.buffers[1].handle);
        if (renderContext.skyProgram.hasCloudsTexture()) {
            GLES20.glBindTexture(34067, this.cloudsTexture.handle);
        }
        GLES20.glDrawElements(4, WindlightSky.icosahedronIndices.length, 5123, 0);
        if (renderContext.windlightPreset.star_brightness != 0.0f) {
            GLES20.glUseProgram(renderContext.starsProgram.getHandle());
            renderContext.starsProgram.ApplyWindlight(renderContext);
            this.skyMatrix.glApplyUniformMatrix(renderContext.starsProgram.uMVPMatrix);
            GLES20.glBindBuffer(34962, this.buffers[2].handle);
            GLES20.glEnableVertexAttribArray(renderContext.starsProgram.vPosition);
            GLES20.glVertexAttribPointer(renderContext.starsProgram.vPosition, 3, 5126, false, 12, 0);
            GLES20.glBindBuffer(34963, this.buffers[3].handle);
            GLES20.glDrawElements(0, this.starsIndices.capacity(), 5123, 0);
        }
        GLES20.glEnable(2884);
        GLES20.glEnable(3042);
        GLES20.glDepthFunc(513);
        this.skyMatrix.glPopMatrix();
    }
    
    public void updateMatrix(final RenderContext renderContext) {
        if (this.skyMatrix == null) {
            this.skyMatrix = new MatrixStack();
        }
        final float[] a = new float[16];
        Arrays.fill(a, 0.0f);
        final float n = 1.0f / (float)Math.tan(renderContext.FOVAngle * 3.141592653589793 / 360.0);
        final float n2 = 1.0f / renderContext.aspectRatio;
        renderContext.getClass();
        a[5] = (a[0] = n) / n2;
        a[10] = -1.0f;
        a[14] = (a[11] = -1.0f);
        this.skyMatrix.reset();
        this.skyMatrix.glLoadMatrixf(a, 0);
        this.skyMatrix.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
    }
}
