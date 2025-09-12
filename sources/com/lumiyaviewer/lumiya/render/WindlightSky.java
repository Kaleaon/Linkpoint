// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render;

import android.content.res.AssetManager;
import android.opengl.GLES20;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLBuffer;
import com.lumiyaviewer.lumiya.render.glres.textures.GLResourceTexture;
import com.lumiyaviewer.lumiya.render.shaders.SkyProgram;
import com.lumiyaviewer.lumiya.render.shaders.StarsProgram;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.windlight.WindlightPreset;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

// Referenced classes of package com.lumiyaviewer.lumiya.render:
//            RenderContext, MatrixStack

public class WindlightSky
{

    private static final int NumStars = 500;
    private static final float Q;
    private static final int SKY_INDEX_BUFFER = 1;
    private static final int SKY_VERTEX_BUFFER = 0;
    private static final int STARS_INDEX_BUFFER = 3;
    private static final int STARS_VERTEX_BUFFER = 2;
    private static final float StarsRadius = 0.8F;
    private static final float ico1;
    private static final float icoQ;
    private static final float icoRadius;
    private static final short icosahedronIndices[] = {
        5, 0, 1, 10, 0, 5, 5, 1, 9, 10, 
        5, 6, 6, 5, 9, 11, 0, 10, 3, 11, 
        10, 3, 10, 6, 3, 6, 2, 7, 3, 2, 
        8, 7, 2, 4, 7, 8, 1, 4, 8, 9, 
        8, 2, 9, 2, 6, 11, 3, 7, 4, 0, 
        11, 4, 11, 7, 1, 0, 4, 1, 8, 9
    };
    private static final float icosahedronVertices[];
    private final GLBuffer buffers[] = new GLBuffer[4];
    private final GLResourceTexture cloudsTexture;
    private MatrixStack skyMatrix;
    private FloatBuffer starsCoords;
    private ShortBuffer starsIndices;

    public WindlightSky(RenderContext rendercontext)
    {
        skyMatrix = null;
        starsCoords = FloatBuffer.allocate(1500);
        starsIndices = ShortBuffer.allocate(500);
        Object obj = new LLVector3();
        for (int i = 0; i < 500; i++)
        {
            ((LLVector3) (obj)).set(0.8F, 0.0F, 0.0F);
            LLQuaternion llquaternion = new LLQuaternion((float)Math.random() * 2.0F - 1.0F, (float)Math.random() * 2.0F - 1.0F, (float)Math.random() * 2.0F - 1.0F, (float)Math.random() * 2.0F - 1.0F);
            llquaternion.normalize();
            ((LLVector3) (obj)).mul(llquaternion);
            starsCoords.put(((LLVector3) (obj)).x);
            starsCoords.put(((LLVector3) (obj)).y);
            starsCoords.put(((LLVector3) (obj)).z);
            starsIndices.put((short)i);
        }

        ShortBuffer shortbuffer;
        if (rendercontext.skyProgram.hasCloudsTexture())
        {
            cloudsTexture = loadClouds(rendercontext);
        } else
        {
            cloudsTexture = null;
        }
        obj = FloatBuffer.wrap(icosahedronVertices);
        shortbuffer = ShortBuffer.wrap(icosahedronIndices);
        for (int j = 0; j < buffers.length; j++)
        {
            buffers[j] = new GLBuffer(rendercontext.glResourceManager, null);
        }

        GLES20.glBindBuffer(34962, buffers[0].handle);
        GLES20.glBufferData(34962, icosahedronVertices.length * 4, ((FloatBuffer) (obj)).position(0), 35044);
        GLES20.glBindBuffer(34963, buffers[1].handle);
        GLES20.glBufferData(34963, icosahedronIndices.length * 2, shortbuffer.position(0), 35044);
        GLES20.glUseProgram(rendercontext.skyProgram.getHandle());
        GLES20.glEnableVertexAttribArray(rendercontext.skyProgram.vPosition);
        GLES20.glVertexAttribPointer(rendercontext.skyProgram.vPosition, 3, 5126, false, 12, 0);
        GLES20.glBindBuffer(34962, buffers[2].handle);
        GLES20.glBufferData(34962, starsCoords.capacity() * 4, starsCoords.position(0), 35044);
        GLES20.glBindBuffer(34963, buffers[3].handle);
        GLES20.glBufferData(34963, starsIndices.capacity() * 2, starsIndices.position(0), 35044);
        GLES20.glUseProgram(rendercontext.starsProgram.getHandle());
        GLES20.glEnableVertexAttribArray(rendercontext.starsProgram.vPosition);
        GLES20.glVertexAttribPointer(rendercontext.starsProgram.vPosition, 3, 5126, false, 12, 0);
    }

    private GLResourceTexture loadClouds(RenderContext rendercontext)
    {
        int ai[];
        OpenJPEG aopenjpeg[];
        AssetManager assetmanager;
        InputStream inputstream;
        OpenJPEG openjpeg;
        int i;
        int j;
        int k;
        try
        {
            assetmanager = LumiyaApp.getAssetManager();
            ai = new int[6];
            ai;
            ai[0] = 34070;
            ai[1] = 34072;
            ai[2] = 34074;
            ai[3] = 34069;
            ai[4] = 34071;
            ai[5] = 34073;
            aopenjpeg = new OpenJPEG[ai.length];
        }
        // Misplaced declaration of an exception variable
        catch (RenderContext rendercontext)
        {
            Debug.Warning(rendercontext);
            return null;
        }
        j = 0;
        i = 0;
        if (j >= ai.length)
        {
            break; /* Loop/switch isn't completed */
        }
        inputstream = assetmanager.open((new StringBuilder()).append("windlight/").append((new String[] {
            "clouds_nx.tga", "clouds_py.tga", "clouds_nz.tga", "clouds_px.tga", "clouds_ny.tga", "clouds_pz.tga"
        })[j]).toString());
        openjpeg = new OpenJPEG(inputstream, com.lumiyaviewer.lumiya.openjpeg.OpenJPEG.ImageFormat.TGA, false, false, 0.0F, 0.0F, true);
        inputstream.close();
        Debug.Printf("WindlightSky: texture %dx%d,  numcomps %d, bpp %d", new Object[] {
            Integer.valueOf(openjpeg.width), Integer.valueOf(openjpeg.height), Integer.valueOf(openjpeg.num_components), Integer.valueOf(openjpeg.bytes_per_pixel)
        });
        aopenjpeg[j] = openjpeg;
        k = openjpeg.getLoadedSize();
        j++;
        i += k;
        if (true) goto _L2; else goto _L1
_L2:
        break MISSING_BLOCK_LABEL_54;
_L1:
        rendercontext = new GLResourceTexture(rendercontext.glResourceManager, i);
        GLES20.glBindTexture(34067, ((GLResourceTexture) (rendercontext)).handle);
        i = 0;
_L4:
        if (i >= ai.length)
        {
            break; /* Loop/switch isn't completed */
        }
        aopenjpeg[i].SetAsTextureTarget(ai[i]);
        aopenjpeg[i] = null;
        i++;
        if (true) goto _L4; else goto _L3
_L3:
        GLES20.glTexParameteri(34067, 10240, 9729);
        GLES20.glTexParameteri(34067, 10241, 9729);
        GLES20.glTexParameteri(34067, 10242, 10497);
        GLES20.glTexParameteri(34067, 10243, 10497);
        return rendercontext;
    }

    public void GLDraw(RenderContext rendercontext, float f, float f1)
    {
        if (skyMatrix == null)
        {
            return;
        }
        GLES20.glDisable(2884);
        GLES20.glDisable(3042);
        GLES20.glDepthFunc(515);
        skyMatrix.glPushMatrix();
        if (f1 != 0.0F)
        {
            skyMatrix.glRotatef(f1, 1.0F, 0.0F, 0.0F);
        }
        skyMatrix.glRotatef(-f + 90F, 0.0F, 0.0F, 1.0F);
        GLES20.glUseProgram(rendercontext.skyProgram.getHandle());
        rendercontext.skyProgram.ApplyWindlight(rendercontext);
        skyMatrix.glApplyUniformMatrix(rendercontext.skyProgram.uMVPMatrix);
        GLES20.glBindBuffer(34962, buffers[0].handle);
        GLES20.glEnableVertexAttribArray(rendercontext.skyProgram.vPosition);
        GLES20.glVertexAttribPointer(rendercontext.skyProgram.vPosition, 3, 5126, false, 12, 0);
        GLES20.glBindBuffer(34963, buffers[1].handle);
        if (rendercontext.skyProgram.hasCloudsTexture())
        {
            GLES20.glBindTexture(34067, cloudsTexture.handle);
        }
        GLES20.glDrawElements(4, icosahedronIndices.length, 5123, 0);
        if (rendercontext.windlightPreset.star_brightness != 0.0F)
        {
            GLES20.glUseProgram(rendercontext.starsProgram.getHandle());
            rendercontext.starsProgram.ApplyWindlight(rendercontext);
            skyMatrix.glApplyUniformMatrix(rendercontext.starsProgram.uMVPMatrix);
            GLES20.glBindBuffer(34962, buffers[2].handle);
            GLES20.glEnableVertexAttribArray(rendercontext.starsProgram.vPosition);
            GLES20.glVertexAttribPointer(rendercontext.starsProgram.vPosition, 3, 5126, false, 12, 0);
            GLES20.glBindBuffer(34963, buffers[3].handle);
            GLES20.glDrawElements(0, starsIndices.capacity(), 5123, 0);
        }
        GLES20.glEnable(2884);
        GLES20.glEnable(3042);
        GLES20.glDepthFunc(513);
        skyMatrix.glPopMatrix();
    }

    public void updateMatrix(RenderContext rendercontext)
    {
        if (skyMatrix == null)
        {
            skyMatrix = new MatrixStack();
        }
        float af[] = new float[16];
        Arrays.fill(af, 0.0F);
        float f = 1.0F / (float)Math.tan(((double)rendercontext.FOVAngle * 3.1415926535897931D) / 360D);
        float f1 = 1.0F / rendercontext.aspectRatio;
        rendercontext.getClass();
        af[0] = f;
        af[5] = f / f1;
        af[10] = -1F;
        af[11] = -1F;
        af[14] = -1F;
        skyMatrix.reset();
        skyMatrix.glLoadMatrixf(af, 0);
        skyMatrix.glRotatef(-90F, 1.0F, 0.0F, 0.0F);
    }

    static 
    {
        Q = (float)((Math.sqrt(5D) + 1.0D) / 2D);
        icoRadius = (float)Math.sqrt(Q * Q + 1.0F);
        ico1 = 1.0F / icoRadius;
        icoQ = Q / icoRadius;
        icosahedronVertices = (new float[] {
            0.0F, ico1, icoQ, 0.0F, -ico1, icoQ, 0.0F, -ico1, -icoQ, 0.0F, 
            ico1, -icoQ, icoQ, 0.0F, ico1, -icoQ, 0.0F, ico1, -icoQ, 0.0F, 
            -ico1, icoQ, 0.0F, -ico1, ico1, -icoQ, 0.0F, -ico1, -icoQ, 0.0F, 
            -ico1, icoQ, 0.0F, ico1, icoQ, 0.0F
        });
    }
}
