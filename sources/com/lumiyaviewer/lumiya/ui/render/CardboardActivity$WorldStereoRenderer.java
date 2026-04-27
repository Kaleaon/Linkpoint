// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;

import android.opengl.Matrix;
import android.os.SystemClock;
import com.google.common.util.concurrent.AtomicDouble;
import com.google.vr.sdk.base.Eye;
import com.google.vr.sdk.base.GvrView;
import com.google.vr.sdk.base.HeadTransform;
import com.google.vr.sdk.base.Viewport;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.HeadTransformCompat;
import com.lumiyaviewer.lumiya.render.WorldViewRenderer;
import com.lumiyaviewer.lumiya.render.glres.textures.GLExternalTexture;
import com.lumiyaviewer.lumiya.render.picking.ObjectIntersectInfo;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarControl;
import com.lumiyaviewer.lumiya.slproto.types.CameraParams;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.microedition.khronos.egl.EGLConfig;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.render:
//            CardboardActivity, RenderSettings

private class eyeOffsetMatrix
    implements com.google.vr.sdk.base.eoRenderer
{

    private static final float TURN_DEGREES = 35F;
    private static final float TURN_DEGREES_PER_MS = 0.02F;
    private static final float YAW_AVERAGE_FACTOR = 1E-04F;
    private boolean agentHeadingAcquired;
    private boolean crosshairVisible;
    private final float extTextureMatrixUV[] = new float[16];
    private GLExternalTexture externalTexture;
    private final float eyeHitTests[] = new float[4];
    private final float eyeOffset[] = new float[4];
    private final float eyeOffsetMatrix[] = new float[16];
    private final float eyeProjection[] = new float[32];
    private final boolean eyeProjectionValid[] = new boolean[2];
    private float eyeSeparation;
    private final int eyeViewport[] = new int[4];
    private final HeadTransformCompat headTransformCompat = new HeadTransformCompat();
    private long lastFrameTime;
    final CardboardActivity this$0;
    private int viewportHeight;
    private int viewportWidth;

    void _2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity$WorldStereoRenderer_2D_mthref_2D_0()
    {
        CardboardActivity._2D_wrap8(CardboardActivity.this);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_render_CardboardActivity$WorldStereoRenderer_58554()
    {
        CardboardActivity._2D_wrap4(CardboardActivity.this, externalTexture);
        CardboardActivity._2D_wrap0(CardboardActivity.this);
    }

    public void onDrawEye(Eye eye)
    {
        int k = eye.getType();
        float f;
        if (k == 1)
        {
            f = -0.5F;
        } else
        {
            f = 0.5F;
        }
        f *= eyeSeparation;
        for (int i = 0; i < 4; i++)
        {
            eyeOffset[i] = headTransformCompat.rightVector[i] * f;
        }

        eye.getViewport().getAsArray(eyeViewport, 0);
        eye.getEyeView();
        int j;
        if (k == 1)
        {
            j = 0;
        } else
        {
            j = 1;
        }
        if (CardboardActivity._2D_get24(CardboardActivity.this) != null && (!eyeProjectionValid[j] || eye.getProjectionChanged()))
        {
            System.arraycopy(eye.getPerspective(0.5F, CardboardActivity._2D_get24(CardboardActivity.this).drawDistance), 0, eyeProjection, j * 16, 16);
        }
        CardboardActivity._2D_get25(CardboardActivity.this).onDrawFrame(null, headTransformCompat, eyeOffset, eyeViewport, null, null, 0);
        if (externalTexture != null)
        {
            if (k == 1)
            {
                j = 0;
            } else
            {
                j = 2;
            }
            CardboardActivity._2D_get25(CardboardActivity.this).drawExternalTexture(externalTexture, extTextureMatrixUV, f, headTransformCompat.pitchDegrees, headTransformCompat.useButtonsYaw, 1.5F, 1.125F, eyeHitTests, j);
            if (crosshairVisible)
            {
                CardboardActivity._2D_get25(CardboardActivity.this).drawCrosshair(0.1F, f);
            }
        }
    }

    public void onFinishFrame(Viewport viewport)
    {
        int i;
        int j;
        CardboardActivity._2D_get25(CardboardActivity.this).onFinishFrame();
        if (externalTexture == null)
        {
            break MISSING_BLOCK_LABEL_147;
        }
        float f = (eyeHitTests[0] + eyeHitTests[2]) / 2.0F;
        float f1 = (eyeHitTests[1] + eyeHitTests[3]) / 2.0F;
        i = (int)((f * 2.0F + 0.5F) * (float)externalTexture.getWidth());
        j = (int)((-(f1 * 2.0F) + 0.5F) * (float)externalTexture.getHeight());
        viewport = ((Viewport) (CardboardActivity._2D_get10(CardboardActivity.this)));
        viewport;
        JVM INSTR monitorenter ;
        CardboardActivity._2D_set6(CardboardActivity.this, i);
        CardboardActivity._2D_set7(CardboardActivity.this, j);
        viewport;
        JVM INSTR monitorexit ;
        if (!CardboardActivity._2D_get11(CardboardActivity.this).getAndSet(true))
        {
            runOnUiThread(new _cls2(this));
        }
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void onNewFrame(HeadTransform headtransform)
    {
        long l;
        boolean flag5;
        Object obj = (SLAvatarControl)CardboardActivity._2D_get1(CardboardActivity.this).get();
        l = SystemClock.uptimeMillis();
        headtransform.getQuaternion(headTransformCompat.rotationQuat, 0);
        headtransform.getTranslation(headTransformCompat.translationVector, 0);
        headtransform.getHeadView(headTransformCompat.headTransformMatrix, 0);
        headtransform.getEulerAngles(headTransformCompat.eulerAngles, 0);
        headtransform.getRightVector(headTransformCompat.rightVectorRaw, 0);
        Matrix.multiplyMV(headTransformCompat.rightVector, 0, eyeOffsetMatrix, 0, headTransformCompat.rightVectorRaw, 0);
        headTransformCompat.yawDegrees = CameraParams.wrapAngle(headTransformCompat.eulerAngles[1] / 0.01745329F);
        if (obj != null && agentHeadingAcquired ^ true)
        {
            headTransformCompat.viewExtraYaw = ((SLAvatarControl) (obj)).getAgentHeading();
            agentHeadingAcquired = true;
            Debug.Printf("Cardboard: agent heading acquired: %.2f", new Object[] {
                Float.valueOf(headTransformCompat.viewExtraYaw)
            });
        }
        if (CardboardActivity._2D_get4(CardboardActivity.this) == chAim)
        {
            headTransformCompat.pitchDegrees = 0.0F;
            headTransformCompat.useButtonsYaw = 0.0F;
            headTransformCompat.lastYaw = headTransformCompat.eulerAngles[1];
        } else
        {
label0:
            {
                if (CardboardActivity._2D_get4(CardboardActivity.this) != ect && CardboardActivity._2D_get4(CardboardActivity.this) != No)
                {
                    break label0;
                }
                headTransformCompat.pitchDegrees = 0.0F;
                headTransformCompat.useButtonsYaw = (headTransformCompat.eulerAngles[1] - headTransformCompat.lastYaw) / 0.01745329F;
            }
        }
_L3:
        if (!headTransformCompat.neutralYawValid)
        {
            headTransformCompat.neutralYawValid = true;
            headTransformCompat.neutralYaw = headTransformCompat.yawDegrees;
        } else
        {
            long l1 = l - lastFrameTime;
            float f1 = CameraParams.angleMinusAngle(headTransformCompat.yawDegrees, headTransformCompat.neutralYaw);
            boolean flag1;
            boolean flag3;
            boolean flag4;
            if (CardboardActivity._2D_get17(CardboardActivity.this).get() || CardboardActivity._2D_get3(CardboardActivity.this).get() == 3)
            {
                flag4 = true;
            } else
            {
                flag4 = false;
            }
            flag1 = false;
            flag3 = false;
            f = 1.0F;
            if (flag4)
            {
                double d = CardboardActivity._2D_get18(CardboardActivity.this).get();
                if (d < 0.0D)
                {
                    flag1 = true;
                } else
                {
                    flag1 = false;
                }
                if (d > 0.0D)
                {
                    flag3 = true;
                } else
                {
                    flag3 = false;
                }
                f = (float)Math.abs(d);
            }
            if (f1 < -35F && flag4 ^ true || flag3)
            {
                headTransformCompat.viewExtraYaw = CameraParams.wrapAngle(headTransformCompat.viewExtraYaw - f * ((float)l1 * 0.02F));
            } else
            if (f1 > 35F && flag4 ^ true || flag1)
            {
                headtransform = headTransformCompat;
                f1 = headTransformCompat.viewExtraYaw;
                headtransform.viewExtraYaw = CameraParams.wrapAngle(f * ((float)l1 * 0.02F) + f1);
            } else
            {
                headTransformCompat.neutralYaw = CameraParams.wrapAngle(headTransformCompat.neutralYaw + 1E-04F * f1 * (float)l1);
            }
        }
        if (headTransformCompat.viewExtraYaw != CardboardActivity._2D_get20(CardboardActivity.this) || Float.isNaN(CardboardActivity._2D_get20(CardboardActivity.this)))
        {
            CardboardActivity._2D_set5(CardboardActivity.this, CameraParams.wrapAngle(headTransformCompat.viewExtraYaw));
        }
        CardboardActivity._2D_set1(CardboardActivity.this, CameraParams.wrapAngle(headTransformCompat.yawDegrees + headTransformCompat.viewExtraYaw));
        if (!Float.isNaN(CardboardActivity._2D_get20(CardboardActivity.this)) && obj != null)
        {
            float f;
            boolean flag;
            boolean flag2;
            if (CardboardActivity._2D_get16(CardboardActivity.this))
            {
                f = CardboardActivity._2D_get9(CardboardActivity.this);
            } else
            {
                f = CardboardActivity._2D_get20(CardboardActivity.this);
            }
            ((SLAvatarControl) (obj)).setAgentHeading(f);
        }
        lastFrameTime = l;
        CardboardActivity._2D_get25(CardboardActivity.this).setOwnAvatarHidden(CardboardActivity._2D_get21(CardboardActivity.this) ^ true);
        CardboardActivity._2D_get25(CardboardActivity.this).onPrepareFrame(headTransformCompat);
        if (CardboardActivity._2D_get28(CardboardActivity.this).getAndSet(false))
        {
            CardboardActivity._2D_get25(CardboardActivity.this).pickObject(viewportWidth / 2, viewportHeight / 2, CardboardActivity._2D_get8(CardboardActivity.this));
        }
        headtransform = (ObjectIntersectInfo)CardboardActivity._2D_get22(CardboardActivity.this).get();
        obj = CardboardActivity._2D_get25(CardboardActivity.this);
        if (headtransform != null)
        {
            headtransform = ((ObjectIntersectInfo) (headtransform)).objInfo;
        } else
        {
            headtransform = null;
        }
        ((WorldViewRenderer) (obj)).setDrawPickedObject(headtransform);
        if (CardboardActivity._2D_get4(CardboardActivity.this) == ails)
        {
            flag5 = CardboardActivity._2D_get14(CardboardActivity.this);
        } else
        {
            flag5 = false;
        }
        crosshairVisible = flag5;
        if (externalTexture != null)
        {
            externalTexture.update(extTextureMatrixUV);
        }
        return;
        headTransformCompat.pitchDegrees = headTransformCompat.eulerAngles[0] / 0.01745329F;
        flag = false;
        if (CardboardActivity._2D_get4(CardboardActivity.this) != ech) goto _L2; else goto _L1
_L1:
        flag = true;
_L5:
        if (flag)
        {
            headTransformCompat.lastYaw = headTransformCompat.eulerAngles[1];
            headTransformCompat.useButtonsYaw = 0.0F;
        } else
        {
            headTransformCompat.useButtonsYaw = (headTransformCompat.eulerAngles[1] - headTransformCompat.lastYaw) / 0.01745329F;
        }
          goto _L3
_L2:
        if (CardboardActivity._2D_get4(CardboardActivity.this) != ault) goto _L5; else goto _L4
_L4:
        flag2 = true;
        if (!CardboardActivity._2D_get14(CardboardActivity.this) || CardboardActivity._2D_get12(CardboardActivity.this) < CardboardActivity._2D_get23(CardboardActivity.this))
        {
            flag2 = false;
        }
        if (!CardboardActivity._2D_get14(CardboardActivity.this)) goto _L7; else goto _L6
_L6:
        flag = flag2;
        if (CardboardActivity._2D_get12(CardboardActivity.this) <= CardboardActivity._2D_get19(CardboardActivity.this)) goto _L5; else goto _L7
_L7:
        flag = flag2;
        if (CardboardActivity._2D_get21(CardboardActivity.this))
        {
            flag = false;
        }
          goto _L5
    }

    public void onRendererShutdown()
    {
        externalTexture.release();
        externalTexture = null;
        CardboardActivity._2D_get25(CardboardActivity.this).onRendererShutdown();
    }

    public void onSurfaceChanged(int i, int j)
    {
        viewportWidth = i;
        viewportHeight = j;
        CardboardActivity._2D_get25(CardboardActivity.this).onSurfaceChanged(null, i, j);
        if (externalTexture != null)
        {
            externalTexture.release();
        }
        externalTexture = new GLExternalTexture((int)((float)i * 1.0F), (int)((float)j * 0.75F) + CardboardActivity._2D_get30(CardboardActivity.this));
        eyeSeparation = CardboardActivity._2D_get7(CardboardActivity.this).getInterpupillaryDistance();
        headTransformCompat.neutralYawValid = false;
        runOnUiThread(new _cls3(this));
    }

    public void onSurfaceCreated(EGLConfig eglconfig)
    {
        CardboardActivity._2D_get25(CardboardActivity.this).onSurfaceCreated(null, eglconfig, true);
    }

    _cls3()
    {
        this$0 = CardboardActivity.this;
        super();
        viewportWidth = 0;
        viewportHeight = 0;
        agentHeadingAcquired = false;
        eyeSeparation = 0.0F;
        lastFrameTime = 0L;
        crosshairVisible = false;
        Matrix.setIdentityM(eyeOffsetMatrix, 0);
        Matrix.rotateM(eyeOffsetMatrix, 0, -90F, 1.0F, 0.0F, 0.0F);
    }
}
