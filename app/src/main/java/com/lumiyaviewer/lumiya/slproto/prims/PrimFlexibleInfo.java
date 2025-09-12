package com.lumiyaviewer.lumiya.slproto.prims;

import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;

public class PrimFlexibleInfo {
    private static final float FLEXIBLE_OBJECT_MAX_INTERNAL_TENSION_FORCE = 0.99f;
    private static final long MIN_UPDATE_INTERVAL = 200;
    private int NumSections = 0;
    private long lastUpdateMillis;
    private volatile boolean needVertexBufferUpdate = false;
    private float[] sectionData;
    private float[] sectionMatrices;
    private FlexibleSection[] sections;
    private GLLoadableBuffer vertexBuffer = null;

    private static class FlexibleSection {
        LLVector3 Direction;
        LLVector3 Position;
        LLQuaternion Rotation;
        LLVector3 Velocity;

        private FlexibleSection() {
        }

        /* synthetic */ FlexibleSection(FlexibleSection flexibleSection) {
            this();
        }
    }

    public boolean doFlexibleUpdate(PrimFlexibleParams primFlexibleParams, float[] fArr, int i, float f, float f2, float f3) {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis < this.lastUpdateMillis + MIN_UPDATE_INTERVAL) {
            return false;
        }
        if (primFlexibleParams.NumFlexiSections != this.NumSections) {
            this.sections = null;
            this.sectionMatrices = null;
            this.sectionData = null;
            this.NumSections = primFlexibleParams.NumFlexiSections;
        }
        if (this.NumSections == 0) {
            return false;
        }
        this.lastUpdateMillis = currentTimeMillis;
        float f4 = (((float) (currentTimeMillis - this.lastUpdateMillis)) / 1000.0f) * 5.0f;
        boolean z = false;
        if (this.sectionData == null) {
            this.sectionData = new float[OpenJPEG.getFlexiDataSize(this.NumSections)];
            this.sectionMatrices = new float[(this.NumSections * 16)];
            z = true;
        }
        OpenJPEG.calcFlexiSections(this.sectionData, this.NumSections, this.sectionMatrices, fArr, i, f, f2, f3, f4, primFlexibleParams.Tension, primFlexibleParams.AirFriction, primFlexibleParams.Gravity, primFlexibleParams.UserForce.x, primFlexibleParams.UserForce.y, primFlexibleParams.UserForce.z, z);
        this.needVertexBufferUpdate = true;
        return true;
    }

    public boolean doFlexibleUpdateSlow(PrimFlexibleParams primFlexibleParams, float[] fArr, int i, float f, float f2, float f3) {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis < this.lastUpdateMillis + MIN_UPDATE_INTERVAL) {
            return false;
        }
        LLVector3 lLVector3 = new LLVector3(fArr[i + 12], fArr[i + 13], fArr[i + 14]);
        LLVector3 lLVector32 = new LLVector3(f, f2, f3);
        float[] fArr2 = new float[32];
        Matrix.invertM(fArr2, 0, fArr, i);
        LLQuaternion lLQuaternion = new LLQuaternion(fArr2);
        if (primFlexibleParams.NumFlexiSections != this.NumSections) {
            this.sections = null;
            this.sectionMatrices = null;
            this.NumSections = primFlexibleParams.NumFlexiSections;
        }
        if (this.NumSections == 0) {
            return false;
        }
        this.lastUpdateMillis = currentTimeMillis;
        float f4 = (((float) (currentTimeMillis - this.lastUpdateMillis)) / 1000.0f) * 5.0f;
        LLQuaternion lLQuaternion2 = new LLQuaternion(lLQuaternion);
        LLVector3 lLVector33 = new LLVector3(LLVector3.z_axis);
        lLVector33.mul(lLQuaternion2);
        float f5 = lLVector32.z / ((float) this.NumSections);
        LLVector3 lLVector34 = new LLVector3(lLVector33);
        lLVector34.mul(lLVector32.z / 2.0f);
        LLVector3 sub = LLVector3.sub(lLVector3, lLVector34);
        if (this.sections == null) {
            this.sections = new FlexibleSection[this.NumSections];
            for (int i2 = 0; i2 < this.NumSections; i2++) {
                this.sections[i2] = new FlexibleSection((FlexibleSection) null);
                this.sections[i2].Position = new LLVector3(sub);
                this.sections[i2].Position.addMul(lLVector33, ((float) i2) * f5);
                this.sections[i2].Direction = new LLVector3(lLVector33);
                this.sections[i2].Rotation = new LLQuaternion(lLQuaternion);
                this.sections[i2].Velocity = new LLVector3();
            }
        }
        this.sections[0].Position.set(sub);
        this.sections[0].Direction.set(lLVector33);
        this.sections[0].Rotation.set(lLQuaternion);
        float pow = primFlexibleParams.Tension * 0.1f * (1.0f - ((float) Math.pow(0.85d, ((double) f4) * 30.0d)));
        if (pow > FLEXIBLE_OBJECT_MAX_INTERNAL_TENSION_FORCE) {
            pow = FLEXIBLE_OBJECT_MAX_INTERNAL_TENSION_FORCE;
        }
        float pow2 = (float) Math.pow(10.0d, (double) (((primFlexibleParams.AirFriction * 2.0f) + 1.0f) * f4));
        if (pow2 <= 1.0f) {
            pow2 = 1.0f;
        }
        float f6 = 1.0f / pow2;
        float atan = (float) Math.atan((double) (2.0f * f5));
        float f7 = f5 * f4;
        LLVector3 lLVector35 = new LLVector3();
        LLVector3 lLVector36 = new LLVector3();
        LLQuaternion lLQuaternion3 = new LLQuaternion();
        LLQuaternion lLQuaternion4 = new LLQuaternion();
        LLQuaternion lLQuaternion5 = new LLQuaternion();
        int i3 = 1;
        while (i3 < this.NumSections) {
            lLVector35.set(this.sections[i3].Position);
            this.sections[i3].Position.z -= primFlexibleParams.Gravity * f7;
            this.sections[i3].Position.addMul(primFlexibleParams.UserForce, f7);
            LLVector3 lLVector37 = this.sections[i3 - 1].Position;
            LLVector3 lLVector38 = this.sections[i3 - 1].Direction;
            LLVector3 lLVector39 = i3 == 1 ? this.sections[0].Direction : this.sections[i3 - 2].Direction;
            LLVector3 sub2 = LLVector3.sub(this.sections[i3].Position, lLVector37);
            LLVector3 lLVector310 = new LLVector3(lLVector39);
            lLVector310.mul(f5);
            lLVector310.sub(sub2);
            this.sections[i3].Position.addMul(lLVector310, pow);
            this.sections[i3].Position.addMul(this.sections[i3].Velocity, f6);
            this.sections[i3].Direction.setSub(this.sections[i3].Position, lLVector37);
            this.sections[i3].Direction.normVec();
            LLQuaternion shortestArc = LLQuaternion.shortestArc(lLVector38, this.sections[i3].Direction);
            float angleAxis = shortestArc.getAngleAxis(lLVector36);
            if (angleAxis > 3.1415927f) {
                angleAxis -= 6.2831855f;
            }
            if (angleAxis < -3.1415927f) {
                angleAxis += 6.2831855f;
            }
            if (angleAxis > atan) {
                shortestArc.setQuat(atan, lLVector36);
            } else if (angleAxis < (-atan)) {
                shortestArc.setQuat(-atan, lLVector36);
            }
            lLQuaternion3.setMul(lLQuaternion2, shortestArc);
            lLQuaternion2.set(lLQuaternion3);
            this.sections[i3].Direction.set(lLVector38);
            this.sections[i3].Direction.mul(shortestArc);
            this.sections[i3].Position.set(lLVector37);
            this.sections[i3].Position.addMul(this.sections[i3].Direction, f5);
            this.sections[i3].Rotation.set(lLQuaternion3);
            if (i3 > 1) {
                lLQuaternion4.setQuat(angleAxis / 2.0f, lLVector36);
                lLQuaternion5.setMul(this.sections[i3 - 1].Rotation, lLQuaternion4);
                this.sections[i3 - 1].Rotation.set(lLQuaternion5);
            }
            this.sections[i3].Velocity.setSub(this.sections[i3].Position, lLVector35);
            if (this.sections[i3].Velocity.magVecSquared() > 1.0f) {
                this.sections[i3].Velocity.normVec();
            }
            i3++;
        }
        float[] fArr3 = new float[32];
        Matrix.setIdentityM(fArr3, 16);
        Matrix.scaleM(fArr3, 16, 1.0f / lLVector32.x, 1.0f / lLVector32.y, 1.0f / lLVector32.z);
        Matrix.multiplyMM(fArr3, 0, fArr3, 16, lLQuaternion.getMatrix(), 0);
        Matrix.translateM(fArr3, 0, -lLVector3.x, -lLVector3.y, -lLVector3.z);
        if (this.sectionMatrices == null) {
            this.sectionMatrices = new float[(this.NumSections * 16)];
        }
        float[] fArr4 = new float[8];
        int i4 = 0;
        while (true) {
            int i5 = i4;
            if (i5 < this.NumSections) {
                fArr4[0] = this.sections[i5].Position.x;
                fArr4[1] = this.sections[i5].Position.y;
                fArr4[2] = this.sections[i5].Position.z;
                fArr4[3] = 1.0f;
                Matrix.multiplyMV(fArr4, 4, fArr3, 0, fArr4, 0);
                float f8 = (((float) i5) / ((float) this.NumSections)) - 0.5f;
                float[] fArr5 = new float[32];
                Matrix.setIdentityM(fArr5, 16);
                Matrix.translateM(fArr5, 16, fArr4[4], fArr4[5], fArr4[6] - f8);
                Matrix.translateM(fArr5, 16, 0.0f, 0.0f, f8);
                Matrix.scaleM(fArr5, 16, 1.0f / lLVector32.x, 1.0f / lLVector32.y, 1.0f / lLVector32.z);
                Matrix.multiplyMM(fArr5, 0, fArr5, 16, lLQuaternion.getMatrix(), 0);
                Matrix.multiplyMM(fArr5, 16, fArr5, 0, this.sections[i5].Rotation.getInverseMatrix(), 0);
                Matrix.scaleM(fArr5, 16, lLVector32.x, lLVector32.y, lLVector32.z);
                Matrix.translateM(fArr5, 16, 0.0f, 0.0f, -f8);
                System.arraycopy(fArr5, 16, this.sectionMatrices, i5 * 16, 16);
                i4 = i5 + 1;
            } else {
                this.needVertexBufferUpdate = true;
                return true;
            }
        }
    }

    public GLLoadableBuffer getFlexedVertexBuffer(RenderContext renderContext, GLLoadableBuffer gLLoadableBuffer, int i) {
        if (this.sectionMatrices != null) {
            if (this.needVertexBufferUpdate) {
                DirectByteBuffer rawBuffer = gLLoadableBuffer.getRawBuffer();
                if (this.vertexBuffer == null) {
                    this.vertexBuffer = new GLLoadableBuffer(new DirectByteBuffer(rawBuffer));
                }
                OpenJPEG.applyFlexibleMorph(this.vertexBuffer.getRawBuffer().asByteBuffer(), rawBuffer.asByteBuffer(), i, this.sectionMatrices);
                this.vertexBuffer.Reload(renderContext);
                this.needVertexBufferUpdate = false;
            }
            if (this.vertexBuffer != null) {
                return this.vertexBuffer;
            }
        }
        return gLLoadableBuffer;
    }

    public float[] getMatrices() {
        return this.sectionMatrices;
    }
}
