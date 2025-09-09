package com.lumiyaviewer.lumiya.render;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.support.v4.view.InputDeviceCompat;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLBuffer;
import com.lumiyaviewer.lumiya.render.glres.textures.GLResourceTexture;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

@SuppressLint({"InlinedApi"})
public class WindlightSky {
    private static final int NumStars = 500;
    private static final float Q = ((float) ((Math.sqrt(5.0d) + 1.0d) / 2.0d));
    private static final int SKY_INDEX_BUFFER = 1;
    private static final int SKY_VERTEX_BUFFER = 0;
    private static final int STARS_INDEX_BUFFER = 3;
    private static final int STARS_VERTEX_BUFFER = 2;
    private static final float StarsRadius = 0.8f;
    private static final float ico1 = (1.0f / icoRadius);
    private static final float icoQ = (Q / icoRadius);
    private static final float icoRadius = ((float) Math.sqrt((double) ((Q * Q) + 1.0f)));
    private static final short[] icosahedronIndices = {5, 0, 1, 10, 0, 5, 5, 1, 9, 10, 5, 6, 6, 5, 9, 11, 0, 10, 3, 11, 10, 3, 10, 6, 3, 6, 2, 7, 3, 2, 8, 7, 2, 4, 7, 8, 1, 4, 8, 9, 8, 2, 9, 2, 6, 11, 3, 7, 4, 0, 11, 4, 11, 7, 1, 0, 4, 1, 8, 9};
    private static final float[] icosahedronVertices = {0.0f, ico1, icoQ, 0.0f, -ico1, icoQ, 0.0f, -ico1, -icoQ, 0.0f, ico1, -icoQ, icoQ, 0.0f, ico1, -icoQ, 0.0f, ico1, -icoQ, 0.0f, -ico1, icoQ, 0.0f, -ico1, ico1, -icoQ, 0.0f, -ico1, -icoQ, 0.0f, -ico1, icoQ, 0.0f, ico1, icoQ, 0.0f};
    private final GLBuffer[] buffers = new GLBuffer[4];
    private final GLResourceTexture cloudsTexture;
    private MatrixStack skyMatrix = null;
    private FloatBuffer starsCoords = FloatBuffer.allocate(1500);
    private ShortBuffer starsIndices = ShortBuffer.allocate(500);

    public WindlightSky(RenderContext renderContext) {
        LLVector3 lLVector3 = new LLVector3();
        for (int i = 0; i < 500; i++) {
            lLVector3.set(StarsRadius, 0.0f, 0.0f);
            LLQuaternion lLQuaternion = new LLQuaternion((((float) Math.random()) * 2.0f) - 1.0f, (((float) Math.random()) * 2.0f) - 1.0f, (((float) Math.random()) * 2.0f) - 1.0f, (((float) Math.random()) * 2.0f) - 1.0f);
            lLQuaternion.normalize();
            lLVector3.mul(lLQuaternion);
            this.starsCoords.put(lLVector3.x);
            this.starsCoords.put(lLVector3.y);
            this.starsCoords.put(lLVector3.z);
            this.starsIndices.put((short) i);
        }
        if (renderContext.skyProgram.hasCloudsTexture()) {
            this.cloudsTexture = loadClouds(renderContext);
        } else {
            this.cloudsTexture = null;
        }
        FloatBuffer wrap = FloatBuffer.wrap(icosahedronVertices);
        ShortBuffer wrap2 = ShortBuffer.wrap(icosahedronIndices);
        for (int i2 = 0; i2 < this.buffers.length; i2++) {
            this.buffers[i2] = new GLBuffer(renderContext.glResourceManager, (DirectByteBuffer) null);
        }
        GLES20.glBindBuffer(34962, this.buffers[0].handle);
        GLES20.glBufferData(34962, icosahedronVertices.length * 4, wrap.position(0), 35044);
        GLES20.glBindBuffer(34963, this.buffers[1].handle);
        GLES20.glBufferData(34963, icosahedronIndices.length * 2, wrap2.position(0), 35044);
        GLES20.glUseProgram(renderContext.skyProgram.getHandle());
        GLES20.glEnableVertexAttribArray(renderContext.skyProgram.vPosition);
        GLES20.glVertexAttribPointer(renderContext.skyProgram.vPosition, 3, 5126, false, 12, 0);
        GLES20.glBindBuffer(34962, this.buffers[2].handle);
        GLES20.glBufferData(34962, this.starsCoords.capacity() * 4, this.starsCoords.position(0), 35044);
        GLES20.glBindBuffer(34963, this.buffers[3].handle);
        GLES20.glBufferData(34963, this.starsIndices.capacity() * 2, this.starsIndices.position(0), 35044);
        GLES20.glUseProgram(renderContext.starsProgram.getHandle());
        GLES20.glEnableVertexAttribArray(renderContext.starsProgram.vPosition);
        GLES20.glVertexAttribPointer(renderContext.starsProgram.vPosition, 3, 5126, false, 12, 0);
    }

    private GLResourceTexture loadClouds(RenderContext renderContext) {
        try {
            AssetManager assetManager = LumiyaApp.getAssetManager();
            int[] iArr = {34070, 34072, 34074, 34069, 34071, 34073};
            String[] strArr = {"clouds_nx.tga", "clouds_py.tga", "clouds_nz.tga", "clouds_px.tga", "clouds_ny.tga", "clouds_pz.tga"};
            OpenJPEG[] openJPEGArr = new OpenJPEG[iArr.length];
            int i = 0;
            int i2 = 0;
            while (i < iArr.length) {
                InputStream open = assetManager.open("windlight/" + strArr[i]);
                OpenJPEG openJPEG = new OpenJPEG(open, OpenJPEG.ImageFormat.TGA, false, false, 0.0f, 0.0f, true);
                open.close();
                Debug.Printf("WindlightSky: texture %dx%d,  numcomps %d, bpp %d", Integer.valueOf(openJPEG.width), Integer.valueOf(openJPEG.height), Integer.valueOf(openJPEG.num_components), Integer.valueOf(openJPEG.bytes_per_pixel));
                openJPEGArr[i] = openJPEG;
                i++;
                i2 += openJPEG.getLoadedSize();
            }
            GLResourceTexture gLResourceTexture = new GLResourceTexture(renderContext.glResourceManager, i2);
            GLES20.glBindTexture(34067, gLResourceTexture.handle);
            for (int i3 = 0; i3 < iArr.length; i3++) {
                openJPEGArr[i3].SetAsTextureTarget(iArr[i3]);
                openJPEGArr[i3] = null;
            }
            GLES20.glTexParameteri(34067, 10240, 9729);
            GLES20.glTexParameteri(34067, 10241, 9729);
            GLES20.glTexParameteri(34067, 10242, 10497);
            GLES20.glTexParameteri(34067, 10243, 10497);
            return gLResourceTexture;
        } catch (IOException e) {
            Debug.Warning(e);
            return null;
        }
    }

    public void GLDraw(RenderContext renderContext, float f, float f2) {
        if (this.skyMatrix != null) {
            GLES20.glDisable(2884);
            GLES20.glDisable(3042);
            GLES20.glDepthFunc(515);
            this.skyMatrix.glPushMatrix();
            if (f2 != 0.0f) {
                this.skyMatrix.glRotatef(f2, 1.0f, 0.0f, 0.0f);
            }
            this.skyMatrix.glRotatef((-f) + 90.0f, 0.0f, 0.0f, 1.0f);
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
            GLES20.glDrawElements(4, icosahedronIndices.length, 5123, 0);
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
            GLES20.glDepthFunc(InputDeviceCompat.SOURCE_DPAD);
            this.skyMatrix.glPopMatrix();
        }
    }

    public void updateMatrix(RenderContext renderContext) {
        if (this.skyMatrix == null) {
            this.skyMatrix = new MatrixStack();
        }
        float[] fArr = new float[16];
        Arrays.fill(fArr, 0.0f);
        float tan = 1.0f / ((float) Math.tan((((double) renderContext.FOVAngle) * 3.141592653589793d) / 360.0d));
        renderContext.getClass();
        fArr[0] = tan;
        fArr[5] = tan / (1.0f / renderContext.aspectRatio);
        fArr[10] = -1.0f;
        fArr[11] = -1.0f;
        fArr[14] = -1.0f;
        this.skyMatrix.reset();
        this.skyMatrix.glLoadMatrixf(fArr, 0);
        this.skyMatrix.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
    }
}
