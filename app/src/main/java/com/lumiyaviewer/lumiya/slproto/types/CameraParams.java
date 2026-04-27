package com.lumiyaviewer.lumiya.slproto.types;

import com.lumiyaviewer.lumiya.render.HeadTransformCompat;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class CameraParams {
    private static final float FLING_DECEL_PITCH = 100.0f;
    private static final float FLING_DECEL_YAW = 50.0f;
    private static final float MAX_PITCH = 85.0f;
    private static final float MIN_PITCH = -85.0f;
    private long flingStartTime = 0;
    private float heading = 0.0f;
    private float headingFlingSpeed = 0.0f;
    private boolean isFlinging = false;
    private boolean isManualControl = false;
    private boolean isValid = false;
    private final Object lock = new Object();
    private long manualControlStartTime = 0;
    private float manualFlySpeed = 0.0f;
    private float manualMoveSpeed = 0.0f;
    private float manualStrafeSpeed = 0.0f;
    private float manualTurnSpeed = 0.0f;
    private final LLVector3 offset = new LLVector3(-2.0f, 0.0f, 1.0f);
    private final LLVector3 offsetVR = new LLVector3(0.0f, 0.0f, 1.0f);
    @Nonnull
    private LLVector3 position = new LLVector3();
    private float tilt = 0.0f;
    private float tiltFlingSpeed = 0.0f;
    private boolean useOffset = false;

    public static float angleMinusAngle(float f, float f2) {
        return wrapAngle(wrapAngle(f) - wrapAngle(f2));
    }

    private void processFling() {
        if (this.isFlinging) {
            long currentTimeMillis = System.currentTimeMillis();
            float f = ((float) (currentTimeMillis - this.flingStartTime)) / 1000.0f;
            this.heading = wrapAngle(this.heading + (this.headingFlingSpeed * f));
            this.tilt = Math.max(Math.min(this.tilt + (this.tiltFlingSpeed * f), MAX_PITCH), MIN_PITCH);
            if (this.headingFlingSpeed > 0.0f) {
                this.headingFlingSpeed -= FLING_DECEL_PITCH * f;
                if (this.headingFlingSpeed < 0.0f) {
                    this.headingFlingSpeed = 0.0f;
                }
            } else if (this.headingFlingSpeed < 0.0f) {
                this.headingFlingSpeed += FLING_DECEL_PITCH * f;
                if (this.headingFlingSpeed > 0.0f) {
                    this.headingFlingSpeed = 0.0f;
                }
            }
            if (this.tiltFlingSpeed > 0.0f) {
                this.tiltFlingSpeed -= f * FLING_DECEL_YAW;
                if (this.tiltFlingSpeed < 0.0f) {
                    this.tiltFlingSpeed = 0.0f;
                }
            } else if (this.tiltFlingSpeed < 0.0f) {
                this.tiltFlingSpeed = (f * FLING_DECEL_YAW) + this.tiltFlingSpeed;
                if (this.tiltFlingSpeed > 0.0f) {
                    this.tiltFlingSpeed = 0.0f;
                }
            }
            this.flingStartTime = currentTimeMillis;
            if (this.tiltFlingSpeed == 0.0f && this.headingFlingSpeed == 0.0f) {
                this.isFlinging = false;
            }
        }
    }

    private void processManualControl(HeadTransformCompat headTransformCompat) {
        float f;
        float f2;
        if (this.isManualControl) {
            long currentTimeMillis = System.currentTimeMillis();
            float f3 = ((float) (currentTimeMillis - this.manualControlStartTime)) / 1000.0f;
            if (headTransformCompat != null) {
                f = wrapAngle(headTransformCompat.yawDegrees + headTransformCompat.viewExtraYaw);
                f2 = headTransformCompat.pitchDegrees;
            } else {
                this.heading = wrapAngle(this.heading + (this.manualTurnSpeed * f3));
                f = this.heading;
                f2 = this.tilt;
            }
            if (!(this.manualMoveSpeed == 0.0f && this.manualFlySpeed == 0.0f && this.manualStrafeSpeed == 0.0f)) {
                LLQuaternion mayaQ = LLQuaternion.mayaQ(0.0f, f2, f, LLQuaternion.Order.YZX);
                LLVector3 lLVector3 = new LLVector3(1.0f, 0.0f, 0.0f);
                LLVector3 lLVector32 = new LLVector3(0.0f, 0.0f, 1.0f);
                LLVector3 lLVector33 = new LLVector3(0.0f, 1.0f, 0.0f);
                lLVector3.mul(mayaQ);
                lLVector32.mul(mayaQ);
                lLVector33.mul(mayaQ);
                this.position.addMul(lLVector3, this.manualMoveSpeed * f3);
                this.position.addMul(lLVector32, this.manualFlySpeed * f3);
                this.position.addMul(lLVector33, this.manualStrafeSpeed * f3);
            }
            this.manualControlStartTime = currentTimeMillis;
        }
    }

    public static float wrapAngle(float f) {
        float f2 = (f + 180.0f) % 360.0f;
        if (f2 < 0.0f) {
            f2 += 360.0f;
        }
        return f2 - 180.0f;
    }

    public void copyFrom(@Nullable CameraParams cameraParams) {
        float f;
        float f2;
        float f3;
        float f4;
        float f5;
        if (cameraParams != null) {
            synchronized (cameraParams.lock) {
                cameraParams.processFling();
                cameraParams.processManualControl((HeadTransformCompat) null);
                f = cameraParams.position.x;
                f2 = cameraParams.position.y;
                f3 = cameraParams.position.z;
                f4 = cameraParams.heading;
                f5 = cameraParams.tilt;
                z = cameraParams.isValid;
                if (cameraParams.useOffset) {
                    LLVector3 lLVector3 = new LLVector3(this.offset);
                    lLVector3.mul(LLQuaternion.mayaQ(0.0f, f5, f4, LLQuaternion.Order.YZX));
                    f += lLVector3.x;
                    f2 += lLVector3.y;
                    f3 += lLVector3.z;
                }
            }
            synchronized (this.lock) {
                this.position.set(f, f2, f3);
                this.heading = f4;
                this.tilt = f5;
                this.isValid = z;
            }
        }
    }

    public void fling(float f, float f2) {
        synchronized (this.lock) {
            this.headingFlingSpeed = f;
            this.tiltFlingSpeed = f2;
            this.flingStartTime = System.currentTimeMillis();
            this.isFlinging = true;
        }
    }

    public float getHeading() {
        float f;
        synchronized (this.lock) {
            f = this.heading;
        }
        return f;
    }

    @Nonnull
    public LLVector3 getPosition() {
        LLVector3 lLVector3;
        synchronized (this.lock) {
            lLVector3 = this.position;
        }
        return lLVector3;
    }

    public float getTilt() {
        float f;
        synchronized (this.lock) {
            f = this.tilt;
        }
        return f;
    }

    public void getVRCamera(@Nullable CameraParams cameraParams, HeadTransformCompat headTransformCompat) {
        float f;
        float f2;
        float f3;
        float f4;
        float f5;
        if (cameraParams != null) {
            synchronized (cameraParams.lock) {
                cameraParams.processManualControl(headTransformCompat);
                f = cameraParams.position.x;
                f2 = cameraParams.position.y;
                f3 = cameraParams.position.z;
                f4 = cameraParams.heading;
                f5 = cameraParams.tilt;
                z = cameraParams.isValid;
                if (cameraParams.useOffset) {
                    LLVector3 lLVector3 = new LLVector3(this.offsetVR);
                    lLVector3.mul(LLQuaternion.mayaQ(0.0f, f5, f4, LLQuaternion.Order.YZX));
                    f += lLVector3.x;
                    f2 += lLVector3.y;
                    f3 += lLVector3.z;
                }
            }
            synchronized (this.lock) {
                this.position.set(f, f2, f3);
                this.heading = f4;
                this.tilt = f5;
                this.isValid = z;
            }
        }
    }

    public boolean isFlinging() {
        synchronized (this.lock) {
            z = this.isFlinging;
        }
        return z;
    }

    public boolean isValid() {
        synchronized (this.lock) {
            z = this.isValid;
        }
        return z;
    }

    public void rotate(float f, float f2) {
        synchronized (this.lock) {
            this.heading = wrapAngle(this.heading + f);
            this.tilt = Math.max(Math.min(this.tilt + f2, MAX_PITCH), MIN_PITCH);
            this.isFlinging = false;
        }
    }

    public void set(@Nullable LLVector3 lLVector3, float f, float f2) {
        synchronized (this.lock) {
            if (lLVector3 != null) {
                this.position.set(lLVector3);
            }
            this.heading = f;
            this.tilt = f2;
            this.isValid = true;
        }
    }

    public void setHeading(float f) {
        synchronized (this.lock) {
            this.heading = f;
        }
    }

    public void setPosition(@Nullable LLVector3 lLVector3) {
        synchronized (this.lock) {
            if (lLVector3 != null) {
                this.position.set(lLVector3);
            }
            this.useOffset = true;
            this.isValid = true;
        }
    }

    public void setPosition(@Nullable LLVector3 lLVector3, float f) {
        synchronized (this.lock) {
            if (lLVector3 != null) {
                this.position.set(lLVector3);
            }
            this.heading = f;
            this.tilt = 0.0f;
            this.isFlinging = false;
            this.isValid = true;
            this.useOffset = true;
        }
    }

    public void startManualControl(float f, float f2, float f3, float f4) {
        synchronized (this.lock) {
            if (!this.isManualControl) {
                LLVector3 lLVector3 = new LLVector3(this.position);
                LLQuaternion mayaQ = LLQuaternion.mayaQ(0.0f, this.tilt, this.heading, LLQuaternion.Order.YZX);
                if (!this.useOffset) {
                    LLVector3 lLVector32 = new LLVector3(this.offset);
                    lLVector32.mul(mayaQ);
                    lLVector3.add(lLVector32);
                    this.useOffset = true;
                }
                this.position.set(lLVector3);
                this.isManualControl = true;
                this.manualControlStartTime = System.currentTimeMillis();
            }
            this.manualMoveSpeed = f2;
            this.manualTurnSpeed = f;
            this.manualFlySpeed = f3;
            this.manualStrafeSpeed = f4;
        }
    }

    public void stopManualControl() {
        synchronized (this.lock) {
            this.isManualControl = false;
        }
    }

    public void zoom(float f, float f2, float f3, float f4, float f5) {
        synchronized (this.lock) {
            float f6 = f - 1.0f;
            LLVector3 lLVector3 = new LLVector3(this.position);
            LLQuaternion mayaQ = LLQuaternion.mayaQ(0.0f, this.tilt, this.heading, LLQuaternion.Order.YZX);
            if (!this.useOffset) {
                LLVector3 lLVector32 = new LLVector3(this.offset);
                lLVector32.mul(mayaQ);
                lLVector3.add(lLVector32);
                this.useOffset = true;
            }
            LLVector3 lLVector33 = new LLVector3(1.0f, 0.0f, 0.0f);
            LLVector3 lLVector34 = new LLVector3(0.0f, 0.0f, 1.0f);
            LLVector3 lLVector35 = new LLVector3(0.0f, 1.0f, 0.0f);
            lLVector33.mul(mayaQ);
            lLVector33.mul(f6);
            lLVector34.mul(mayaQ);
            lLVector34.mul((f6 * f3) + f5);
            lLVector35.mul(mayaQ);
            lLVector35.mul((f6 * f2) + f4);
            lLVector3.add(lLVector33);
            lLVector3.add(lLVector34);
            lLVector3.add(lLVector35);
            this.position.set(lLVector3);
        }
    }
}
