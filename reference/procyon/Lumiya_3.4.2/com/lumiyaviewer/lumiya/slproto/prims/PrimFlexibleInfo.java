// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.rawbuffers.DirectByteBuffer;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer;

public class PrimFlexibleInfo
{
    private static final float FLEXIBLE_OBJECT_MAX_INTERNAL_TENSION_FORCE = 0.99f;
    private static final long MIN_UPDATE_INTERVAL = 200L;
    private int NumSections;
    private long lastUpdateMillis;
    private volatile boolean needVertexBufferUpdate;
    private float[] sectionData;
    private float[] sectionMatrices;
    private FlexibleSection[] sections;
    private GLLoadableBuffer vertexBuffer;
    
    public PrimFlexibleInfo() {
        this.NumSections = 0;
        this.needVertexBufferUpdate = false;
        this.vertexBuffer = null;
    }
    
    public boolean doFlexibleUpdate(final PrimFlexibleParams primFlexibleParams, final float[] array, final int n, final float n2, final float n3, final float n4) {
        final long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis < this.lastUpdateMillis + 200L) {
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
        final float n5 = (currentTimeMillis - this.lastUpdateMillis) / 1000.0f;
        this.lastUpdateMillis = currentTimeMillis;
        boolean b = false;
        if (this.sectionData == null) {
            this.sectionData = new float[OpenJPEG.getFlexiDataSize(this.NumSections)];
            this.sectionMatrices = new float[this.NumSections * 16];
            b = true;
        }
        OpenJPEG.calcFlexiSections(this.sectionData, this.NumSections, this.sectionMatrices, array, n, n2, n3, n4, n5 * 5.0f, primFlexibleParams.Tension, primFlexibleParams.AirFriction, primFlexibleParams.Gravity, primFlexibleParams.UserForce.x, primFlexibleParams.UserForce.y, primFlexibleParams.UserForce.z, b);
        return this.needVertexBufferUpdate = true;
    }
    
    public boolean doFlexibleUpdateSlow(final PrimFlexibleParams primFlexibleParams, float[] array, int i, float angleAxis, float n, float n2) {
        final long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis < this.lastUpdateMillis + 200L) {
            return false;
        }
        final LLVector3 llVector3 = new LLVector3(array[i + 12], array[i + 13], array[i + 14]);
        final LLVector3 llVector4 = new LLVector3(angleAxis, n, n2);
        final float[] array2 = new float[32];
        Matrix.invertM(array2, 0, array, i);
        final LLQuaternion llQuaternion = new LLQuaternion(array2);
        if (primFlexibleParams.NumFlexiSections != this.NumSections) {
            this.sections = null;
            this.sectionMatrices = null;
            this.NumSections = primFlexibleParams.NumFlexiSections;
        }
        if (this.NumSections == 0) {
            return false;
        }
        angleAxis = (currentTimeMillis - this.lastUpdateMillis) / 1000.0f;
        this.lastUpdateMillis = currentTimeMillis;
        n2 = angleAxis * 5.0f;
        final LLQuaternion llQuaternion2 = new LLQuaternion(llQuaternion);
        final LLVector3 llVector5 = new LLVector3(LLVector3.z_axis);
        llVector5.mul(llQuaternion2);
        final float n3 = llVector4.z / this.NumSections;
        final LLVector3 llVector6 = new LLVector3(llVector5);
        llVector6.mul(llVector4.z / 2.0f);
        final LLVector3 sub = LLVector3.sub(llVector3, llVector6);
        if (this.sections == null) {
            this.sections = new FlexibleSection[this.NumSections];
            for (i = 0; i < this.NumSections; ++i) {
                this.sections[i] = new FlexibleSection(null);
                (this.sections[i].Position = new LLVector3(sub)).addMul(llVector5, i * n3);
                this.sections[i].Direction = new LLVector3(llVector5);
                this.sections[i].Rotation = new LLQuaternion(llQuaternion);
                this.sections[i].Velocity = new LLVector3();
            }
        }
        this.sections[0].Position.set(sub);
        this.sections[0].Direction.set(llVector5);
        this.sections[0].Rotation.set(llQuaternion);
        angleAxis = (n = primFlexibleParams.Tension * 0.1f * (1.0f - (float)Math.pow(0.85, n2 * 30.0)));
        if (angleAxis > 0.99f) {
            n = 0.99f;
        }
        angleAxis = (float)Math.pow(10.0, (primFlexibleParams.AirFriction * 2.0f + 1.0f) * n2);
        if (angleAxis <= 1.0f) {
            angleAxis = 1.0f;
        }
        final float n4 = 1.0f / angleAxis;
        final float n5 = (float)Math.atan(2.0f * n3);
        final float n6 = n3 * n2;
        final LLVector3 llVector7 = new LLVector3();
        final LLVector3 llVector8 = new LLVector3();
        final LLQuaternion llQuaternion3 = new LLQuaternion();
        final LLQuaternion llQuaternion4 = new LLQuaternion();
        final LLQuaternion llQuaternion5 = new LLQuaternion();
        LLVector3 position;
        LLVector3 position2;
        LLVector3 direction;
        LLVector3 llVector9;
        LLVector3 sub2;
        LLVector3 llVector10;
        LLQuaternion shortestArc;
        for (i = 1; i < this.NumSections; ++i) {
            llVector7.set(this.sections[i].Position);
            position = this.sections[i].Position;
            position.z -= primFlexibleParams.Gravity * n6;
            this.sections[i].Position.addMul(primFlexibleParams.UserForce, n6);
            position2 = this.sections[i - 1].Position;
            direction = this.sections[i - 1].Direction;
            if (i == 1) {
                llVector9 = this.sections[0].Direction;
            }
            else {
                llVector9 = this.sections[i - 2].Direction;
            }
            sub2 = LLVector3.sub(this.sections[i].Position, position2);
            llVector10 = new LLVector3(llVector9);
            llVector10.mul(n3);
            llVector10.sub(sub2);
            this.sections[i].Position.addMul(llVector10, n);
            this.sections[i].Position.addMul(this.sections[i].Velocity, n4);
            this.sections[i].Direction.setSub(this.sections[i].Position, position2);
            this.sections[i].Direction.normVec();
            shortestArc = LLQuaternion.shortestArc(direction, this.sections[i].Direction);
            n2 = (angleAxis = shortestArc.getAngleAxis(llVector8));
            if (n2 > 3.1415927f) {
                angleAxis = n2 - 6.2831855f;
            }
            n2 = angleAxis;
            if (angleAxis < -3.1415927f) {
                n2 = angleAxis + 6.2831855f;
            }
            if (n2 > n5) {
                shortestArc.setQuat(n5, llVector8);
            }
            else if (n2 < -n5) {
                shortestArc.setQuat(-n5, llVector8);
            }
            llQuaternion3.setMul(llQuaternion2, shortestArc);
            llQuaternion2.set(llQuaternion3);
            this.sections[i].Direction.set(direction);
            this.sections[i].Direction.mul(shortestArc);
            this.sections[i].Position.set(position2);
            this.sections[i].Position.addMul(this.sections[i].Direction, n3);
            this.sections[i].Rotation.set(llQuaternion3);
            if (i > 1) {
                llQuaternion4.setQuat(n2 / 2.0f, llVector8);
                llQuaternion5.setMul(this.sections[i - 1].Rotation, llQuaternion4);
                this.sections[i - 1].Rotation.set(llQuaternion5);
            }
            this.sections[i].Velocity.setSub(this.sections[i].Position, llVector7);
            if (this.sections[i].Velocity.magVecSquared() > 1.0f) {
                this.sections[i].Velocity.normVec();
            }
        }
        final float[] array3 = new float[32];
        Matrix.setIdentityM(array3, 16);
        Matrix.scaleM(array3, 16, 1.0f / llVector4.x, 1.0f / llVector4.y, 1.0f / llVector4.z);
        Matrix.multiplyMM(array3, 0, array3, 16, llQuaternion.getMatrix(), 0);
        Matrix.translateM(array3, 0, -llVector3.x, -llVector3.y, -llVector3.z);
        if (this.sectionMatrices == null) {
            this.sectionMatrices = new float[this.NumSections * 16];
        }
        final float[] array4 = new float[8];
        for (i = 0; i < this.NumSections; ++i) {
            array4[0] = this.sections[i].Position.x;
            array4[1] = this.sections[i].Position.y;
            array4[2] = this.sections[i].Position.z;
            array4[3] = 1.0f;
            Matrix.multiplyMV(array4, 4, array3, 0, array4, 0);
            angleAxis = i / (float)this.NumSections - 0.5f;
            array = new float[32];
            Matrix.setIdentityM(array, 16);
            Matrix.translateM(array, 16, array4[4], array4[5], array4[6] - angleAxis);
            Matrix.translateM(array, 16, 0.0f, 0.0f, angleAxis);
            Matrix.scaleM(array, 16, 1.0f / llVector4.x, 1.0f / llVector4.y, 1.0f / llVector4.z);
            Matrix.multiplyMM(array, 0, array, 16, llQuaternion.getMatrix(), 0);
            Matrix.multiplyMM(array, 16, array, 0, this.sections[i].Rotation.getInverseMatrix(), 0);
            Matrix.scaleM(array, 16, llVector4.x, llVector4.y, llVector4.z);
            Matrix.translateM(array, 16, 0.0f, 0.0f, -angleAxis);
            System.arraycopy(array, 16, this.sectionMatrices, i * 16, 16);
        }
        return this.needVertexBufferUpdate = true;
    }
    
    public GLLoadableBuffer getFlexedVertexBuffer(final RenderContext renderContext, final GLLoadableBuffer glLoadableBuffer, final int n) {
        if (this.sectionMatrices != null) {
            if (this.needVertexBufferUpdate) {
                final DirectByteBuffer rawBuffer = glLoadableBuffer.getRawBuffer();
                if (this.vertexBuffer == null) {
                    this.vertexBuffer = new GLLoadableBuffer(new DirectByteBuffer(rawBuffer));
                }
                OpenJPEG.applyFlexibleMorph(this.vertexBuffer.getRawBuffer().asByteBuffer(), rawBuffer.asByteBuffer(), n, this.sectionMatrices);
                this.vertexBuffer.Reload(renderContext);
                this.needVertexBufferUpdate = false;
            }
            if (this.vertexBuffer != null) {
                return this.vertexBuffer;
            }
        }
        return glLoadableBuffer;
    }
    
    public float[] getMatrices() {
        return this.sectionMatrices;
    }
    
    private static class FlexibleSection
    {
        LLVector3 Direction;
        LLVector3 Position;
        LLQuaternion Rotation;
        LLVector3 Velocity;
    }
}
