// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.types;

import com.lumiyaviewer.lumiya.render.HeadTransformCompat;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.types:
//            LLVector3, LLQuaternion

public class CameraParams
{

    private static final float FLING_DECEL_PITCH = 100F;
    private static final float FLING_DECEL_YAW = 50F;
    private static final float MAX_PITCH = 85F;
    private static final float MIN_PITCH = -85F;
    private long flingStartTime;
    private float heading;
    private float headingFlingSpeed;
    private boolean isFlinging;
    private boolean isManualControl;
    private boolean isValid;
    private final Object lock = new Object();
    private long manualControlStartTime;
    private float manualFlySpeed;
    private float manualMoveSpeed;
    private float manualStrafeSpeed;
    private float manualTurnSpeed;
    private final LLVector3 offset = new LLVector3(-2F, 0.0F, 1.0F);
    private final LLVector3 offsetVR = new LLVector3(0.0F, 0.0F, 1.0F);
    private LLVector3 position;
    private float tilt;
    private float tiltFlingSpeed;
    private boolean useOffset;

    public CameraParams()
    {
        position = new LLVector3();
        heading = 0.0F;
        tilt = 0.0F;
        isValid = false;
        useOffset = false;
        isFlinging = false;
        headingFlingSpeed = 0.0F;
        tiltFlingSpeed = 0.0F;
        flingStartTime = 0L;
        isManualControl = false;
        manualControlStartTime = 0L;
        manualTurnSpeed = 0.0F;
        manualMoveSpeed = 0.0F;
        manualFlySpeed = 0.0F;
        manualStrafeSpeed = 0.0F;
    }

    public static float angleMinusAngle(float f, float f1)
    {
        return wrapAngle(wrapAngle(f) - wrapAngle(f1));
    }

    private void processFling()
    {
        if (isFlinging)
        {
            long l = System.currentTimeMillis();
            float f = (float)(l - flingStartTime) / 1000F;
            heading = wrapAngle(heading + headingFlingSpeed * f);
            tilt = Math.max(Math.min(tilt + tiltFlingSpeed * f, 85F), -85F);
            if (headingFlingSpeed > 0.0F)
            {
                headingFlingSpeed = headingFlingSpeed - 100F * f;
                if (headingFlingSpeed < 0.0F)
                {
                    headingFlingSpeed = 0.0F;
                }
            } else
            if (headingFlingSpeed < 0.0F)
            {
                headingFlingSpeed = headingFlingSpeed + 100F * f;
                if (headingFlingSpeed > 0.0F)
                {
                    headingFlingSpeed = 0.0F;
                }
            }
            if (tiltFlingSpeed > 0.0F)
            {
                tiltFlingSpeed = tiltFlingSpeed - f * 50F;
                if (tiltFlingSpeed < 0.0F)
                {
                    tiltFlingSpeed = 0.0F;
                }
            } else
            if (tiltFlingSpeed < 0.0F)
            {
                tiltFlingSpeed = f * 50F + tiltFlingSpeed;
                if (tiltFlingSpeed > 0.0F)
                {
                    tiltFlingSpeed = 0.0F;
                }
            }
            flingStartTime = l;
            if (tiltFlingSpeed == 0.0F && headingFlingSpeed == 0.0F)
            {
                isFlinging = false;
            }
        }
    }

    private void processManualControl(HeadTransformCompat headtransformcompat)
    {
        manualControlStartTime = l;
        return;
        {
            if (!isManualControl)
            {
                break MISSING_BLOCK_LABEL_183;
            }
            long l = System.currentTimeMillis();
            float f2 = (float)(l - manualControlStartTime) / 1000F;
            float f;
            float f1;
            LLVector3 llvector3;
            LLVector3 llvector3_1;
            LLVector3 llvector3_2;
            if (headtransformcompat != null)
            {
                f1 = wrapAngle(headtransformcompat.yawDegrees + headtransformcompat.viewExtraYaw);
                f = headtransformcompat.pitchDegrees;
            } else
            {
                heading = wrapAngle(heading + manualTurnSpeed * f2);
                f1 = heading;
                f = tilt;
            }
        }
        if (manualMoveSpeed != 0.0F || manualFlySpeed != 0.0F || manualStrafeSpeed != 0.0F)
        {
            headtransformcompat = LLQuaternion.mayaQ(0.0F, f, f1, LLQuaternion.Order.YZX);
            llvector3 = new LLVector3(1.0F, 0.0F, 0.0F);
            llvector3_1 = new LLVector3(0.0F, 0.0F, 1.0F);
            llvector3_2 = new LLVector3(0.0F, 1.0F, 0.0F);
            llvector3.mul(headtransformcompat);
            llvector3_1.mul(headtransformcompat);
            llvector3_2.mul(headtransformcompat);
            position.addMul(llvector3, manualMoveSpeed * f2);
            position.addMul(llvector3_1, manualFlySpeed * f2);
            position.addMul(llvector3_2, manualStrafeSpeed * f2);
        }
        break MISSING_BLOCK_LABEL_177;
    }

    public static float wrapAngle(float f)
    {
        float f1 = (f + 180F) % 360F;
        f = f1;
        if (f1 < 0.0F)
        {
            f = f1 + 360F;
        }
        return f - 180F;
    }

    public void copyFrom(CameraParams cameraparams)
    {
        if (cameraparams == null) goto _L2; else goto _L1
_L1:
        Object obj = cameraparams.lock;
        obj;
        JVM INSTR monitorenter ;
        float f3;
        float f4;
        float f5;
        float f6;
        float f7;
        boolean flag;
        cameraparams.processFling();
        cameraparams.processManualControl(null);
        f5 = cameraparams.position.x;
        f4 = cameraparams.position.y;
        f3 = cameraparams.position.z;
        f6 = cameraparams.heading;
        f7 = cameraparams.tilt;
        flag = cameraparams.isValid;
        float f;
        float f1;
        float f2;
        f2 = f3;
        f1 = f4;
        f = f5;
        if (!cameraparams.useOffset)
        {
            break MISSING_BLOCK_LABEL_140;
        }
        cameraparams = new LLVector3(offset);
        cameraparams.mul(LLQuaternion.mayaQ(0.0F, f7, f6, LLQuaternion.Order.YZX));
        f = f5 + ((LLVector3) (cameraparams)).x;
        f1 = f4 + ((LLVector3) (cameraparams)).y;
        f2 = ((LLVector3) (cameraparams)).z;
        f2 = f3 + f2;
        obj;
        JVM INSTR monitorexit ;
        cameraparams = ((CameraParams) (lock));
        cameraparams;
        JVM INSTR monitorenter ;
        position.set(f, f1, f2);
        heading = f6;
        tilt = f7;
        isValid = flag;
        cameraparams;
        JVM INSTR monitorexit ;
_L2:
        return;
        cameraparams;
        throw cameraparams;
        Exception exception;
        exception;
        throw exception;
    }

    public void fling(float f, float f1)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        headingFlingSpeed = f;
        tiltFlingSpeed = f1;
        flingStartTime = System.currentTimeMillis();
        isFlinging = true;
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public float getHeading()
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        float f = heading;
        obj;
        JVM INSTR monitorexit ;
        return f;
        Exception exception;
        exception;
        throw exception;
    }

    public LLVector3 getPosition()
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        LLVector3 llvector3 = position;
        obj;
        JVM INSTR monitorexit ;
        return llvector3;
        Exception exception;
        exception;
        throw exception;
    }

    public float getTilt()
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        float f = tilt;
        obj;
        JVM INSTR monitorexit ;
        return f;
        Exception exception;
        exception;
        throw exception;
    }

    public void getVRCamera(CameraParams cameraparams, HeadTransformCompat headtransformcompat)
    {
        if (cameraparams == null) goto _L2; else goto _L1
_L1:
        Object obj = cameraparams.lock;
        obj;
        JVM INSTR monitorenter ;
        float f3;
        float f4;
        float f5;
        float f6;
        float f7;
        boolean flag;
        cameraparams.processManualControl(headtransformcompat);
        f5 = cameraparams.position.x;
        f4 = cameraparams.position.y;
        f3 = cameraparams.position.z;
        f6 = cameraparams.heading;
        f7 = cameraparams.tilt;
        flag = cameraparams.isValid;
        float f;
        float f1;
        float f2;
        f2 = f3;
        f1 = f4;
        f = f5;
        if (!cameraparams.useOffset)
        {
            break MISSING_BLOCK_LABEL_138;
        }
        cameraparams = new LLVector3(offsetVR);
        cameraparams.mul(LLQuaternion.mayaQ(0.0F, f7, f6, LLQuaternion.Order.YZX));
        f = f5 + ((LLVector3) (cameraparams)).x;
        f1 = f4 + ((LLVector3) (cameraparams)).y;
        f2 = ((LLVector3) (cameraparams)).z;
        f2 = f3 + f2;
        obj;
        JVM INSTR monitorexit ;
        cameraparams = ((CameraParams) (lock));
        cameraparams;
        JVM INSTR monitorenter ;
        position.set(f, f1, f2);
        heading = f6;
        tilt = f7;
        isValid = flag;
        cameraparams;
        JVM INSTR monitorexit ;
_L2:
        return;
        cameraparams;
        throw cameraparams;
        headtransformcompat;
        throw headtransformcompat;
    }

    public boolean isFlinging()
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        boolean flag = isFlinging;
        obj;
        JVM INSTR monitorexit ;
        return flag;
        Exception exception;
        exception;
        throw exception;
    }

    public boolean isValid()
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        boolean flag = isValid;
        obj;
        JVM INSTR monitorexit ;
        return flag;
        Exception exception;
        exception;
        throw exception;
    }

    public void rotate(float f, float f1)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        heading = wrapAngle(heading + f);
        tilt = Math.max(Math.min(tilt + f1, 85F), -85F);
        isFlinging = false;
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void set(LLVector3 llvector3, float f, float f1)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        if (llvector3 == null)
        {
            break MISSING_BLOCK_LABEL_21;
        }
        position.set(llvector3);
        heading = f;
        tilt = f1;
        isValid = true;
        obj;
        JVM INSTR monitorexit ;
        return;
        llvector3;
        throw llvector3;
    }

    public void setHeading(float f)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        heading = f;
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void setPosition(LLVector3 llvector3)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        if (llvector3 == null)
        {
            break MISSING_BLOCK_LABEL_19;
        }
        position.set(llvector3);
        useOffset = true;
        isValid = true;
        obj;
        JVM INSTR monitorexit ;
        return;
        llvector3;
        throw llvector3;
    }

    public void setPosition(LLVector3 llvector3, float f)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        if (llvector3 == null)
        {
            break MISSING_BLOCK_LABEL_19;
        }
        position.set(llvector3);
        heading = f;
        tilt = 0.0F;
        isFlinging = false;
        isValid = true;
        useOffset = true;
        obj;
        JVM INSTR monitorexit ;
        return;
        llvector3;
        throw llvector3;
    }

    public void startManualControl(float f, float f1, float f2, float f3)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        if (!isManualControl)
        {
            LLVector3 llvector3 = new LLVector3(position);
            LLQuaternion llquaternion = LLQuaternion.mayaQ(0.0F, tilt, heading, LLQuaternion.Order.YZX);
            if (!useOffset)
            {
                LLVector3 llvector3_1 = new LLVector3(offset);
                llvector3_1.mul(llquaternion);
                llvector3.add(llvector3_1);
                useOffset = true;
            }
            position.set(llvector3);
            isManualControl = true;
            manualControlStartTime = System.currentTimeMillis();
        }
        manualMoveSpeed = f1;
        manualTurnSpeed = f;
        manualFlySpeed = f2;
        manualStrafeSpeed = f3;
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void stopManualControl()
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        isManualControl = false;
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void zoom(float f, float f1, float f2, float f3, float f4)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        f--;
        LLVector3 llvector3 = new LLVector3(position);
        LLQuaternion llquaternion = LLQuaternion.mayaQ(0.0F, tilt, heading, LLQuaternion.Order.YZX);
        if (!useOffset)
        {
            LLVector3 llvector3_1 = new LLVector3(offset);
            llvector3_1.mul(llquaternion);
            llvector3.add(llvector3_1);
            useOffset = true;
        }
        LLVector3 llvector3_2 = new LLVector3(1.0F, 0.0F, 0.0F);
        LLVector3 llvector3_3 = new LLVector3(0.0F, 0.0F, 1.0F);
        LLVector3 llvector3_4 = new LLVector3(0.0F, 1.0F, 0.0F);
        llvector3_2.mul(llquaternion);
        llvector3_2.mul(f);
        llvector3_3.mul(llquaternion);
        llvector3_3.mul(f * f2 + f4);
        llvector3_4.mul(llquaternion);
        llvector3_4.mul(f * f1 + f3);
        llvector3.add(llvector3_2);
        llvector3.add(llvector3_3);
        llvector3.add(llvector3_4);
        position.set(llvector3);
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }
}
