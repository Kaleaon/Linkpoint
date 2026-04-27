package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector2;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.ArrayList;

public class PrimPath {
    private static final int MIN_DETAIL_FACES = 6;
    private static float[] tableScale = {1.0f, 1.0f, 1.0f, 0.5f, 0.707107f, 0.53f, 0.525f, 0.5f};
    boolean Dirty = true;
    boolean Open = false;
    ArrayList<PathPoint> Path = new ArrayList<>();
    float Step = 1.0f;
    int Total = 0;

    public static class PathPoint {
        float TexT = 0.0f;
        LLVector3 pos = new LLVector3();
        LLQuaternion rot = new LLQuaternion();
        LLVector2 scale = new LLVector2();
    }

    private void genNGon(PrimPathParams primPathParams, int i, float f, float f2, float f3) {
        float f4;
        float f5;
        float f6;
        float f7;
        float f8;
        float f9 = primPathParams.Revolutions;
        float f10 = primPathParams.Skew;
        float abs = Math.abs(f10);
        float f11 = primPathParams.ScaleX * (1.0f - abs);
        float f12 = primPathParams.ScaleY;
        float f13 = 1.0f - primPathParams.TaperX;
        float f14 = 1.0f - primPathParams.TaperY;
        if (f13 > 1.0f) {
            f4 = 1.0f;
            f5 = 2.0f - f13;
        } else {
            f4 = f13;
            f5 = 1.0f;
        }
        if (f14 > 1.0f) {
            f6 = 1.0f;
            f7 = 2.0f - f14;
        } else {
            f6 = f14;
            f7 = 1.0f;
        }
        float f15 = 0.5f;
        if (i < 8) {
            f15 = tableScale[i];
        }
        float f16 = f15 * (1.0f - f12);
        float f17 = primPathParams.RadiusOffset;
        if (f17 < 0.0f) {
            f8 = (f17 + 1.0f) * f16;
        } else {
            float f18 = (1.0f - f17) * f16;
            f8 = f16;
            f16 = f18;
        }
        this.Open = ((primPathParams.End * f2) - primPathParams.Begin < 1.0f || abs > 0.001f || Math.abs(f4 - f5) > 0.001f || Math.abs(f6 - f7) > 0.001f) ? true : Math.abs(f16 - f8) > 0.001f;
        LLQuaternion lLQuaternion = new LLQuaternion();
        LLQuaternion lLQuaternion2 = new LLQuaternion();
        LLVector3 lLVector3 = new LLVector3(1.0f, 0.0f, 0.0f);
        float f19 = primPathParams.TwistBegin * f3;
        float f20 = primPathParams.TwistEnd * f3;
        float f21 = 1.0f / ((float) i);
        float f22 = primPathParams.Begin;
        PathPoint pathPoint = new PathPoint();
        float f23 = 6.2831855f * f9 * f22;
        float sin = (float) (Math.sin((double) f23) * ((double) PrimMath.lerp(f8, f16, f22)));
        pathPoint.pos.set(PrimMath.lerp(0.0f, primPathParams.ShearX, sin) + 0.0f + (PrimMath.lerp(-f10, f10, f22) * 0.5f), ((float) (Math.cos((double) f23) * ((double) PrimMath.lerp(f8, f16, f22)))) + PrimMath.lerp(0.0f, primPathParams.ShearY, sin), sin);
        pathPoint.scale.x = PrimMath.lerp(f5, f4, f22) * f11;
        pathPoint.scale.y = PrimMath.lerp(f7, f6, f22) * f12;
        pathPoint.TexT = f22;
        lLQuaternion.setQuat(((PrimMath.lerp(f19, f20, f22) * 2.0f) * 3.1415927f) - 3.1415927f, 0.0f, 0.0f, 1.0f);
        lLQuaternion2.setQuat(f23, lLVector3);
        pathPoint.rot.setMul(lLQuaternion, lLQuaternion2);
        this.Path.add(pathPoint);
        for (float f24 = ((float) ((int) ((f22 + f21) * ((float) i)))) / ((float) i); f24 < primPathParams.End; f24 += f21) {
            PathPoint pathPoint2 = new PathPoint();
            float f25 = 6.2831855f * f9 * f24;
            float cos = (float) (Math.cos((double) f25) * ((double) PrimMath.lerp(f8, f16, f24)));
            float sin2 = (float) (Math.sin((double) f25) * ((double) PrimMath.lerp(f8, f16, f24)));
            pathPoint2.pos.set(PrimMath.lerp(0.0f, primPathParams.ShearX, sin2) + 0.0f + (PrimMath.lerp(-f10, f10, f24) * 0.5f), cos + PrimMath.lerp(0.0f, primPathParams.ShearY, sin2), sin2);
            pathPoint2.scale.x = PrimMath.lerp(f5, f4, f24) * f11;
            pathPoint2.scale.y = PrimMath.lerp(f7, f6, f24) * f12;
            pathPoint2.TexT = f24;
            lLQuaternion.setQuat(((PrimMath.lerp(f19, f20, f24) * 2.0f) * 3.1415927f) - 3.1415927f, 0.0f, 0.0f, 1.0f);
            lLQuaternion2.setQuat(f25, lLVector3);
            pathPoint2.rot.setMul(lLQuaternion, lLQuaternion2);
            this.Path.add(pathPoint2);
        }
        float f26 = primPathParams.End;
        PathPoint pathPoint3 = new PathPoint();
        float f27 = f9 * 6.2831855f * f26;
        float cos2 = (float) (Math.cos((double) f27) * ((double) PrimMath.lerp(f8, f16, f26)));
        float lerp = (float) (((double) PrimMath.lerp(f8, f16, f26)) * Math.sin((double) f27));
        pathPoint3.pos.set((PrimMath.lerp(-f10, f10, f26) * 0.5f) + PrimMath.lerp(0.0f, primPathParams.ShearX, lerp) + 0.0f, cos2 + PrimMath.lerp(0.0f, primPathParams.ShearY, lerp), lerp);
        pathPoint3.scale.x = PrimMath.lerp(f5, f4, f26) * f11;
        pathPoint3.scale.y = PrimMath.lerp(f7, f6, f26) * f12;
        pathPoint3.TexT = f26;
        lLQuaternion.setQuat(((PrimMath.lerp(f19, f20, f26) * 2.0f) * 3.1415927f) - 3.1415927f, 0.0f, 0.0f, 1.0f);
        lLQuaternion2.setQuat(f27, lLVector3);
        pathPoint3.rot.setMul(lLQuaternion, lLQuaternion2);
        this.Path.add(pathPoint3);
        this.Total = this.Path.size();
    }

    public boolean generate(PrimPathParams primPathParams, float f, int i, boolean z, int i2) {
        if (!this.Dirty && (!z)) {
            return false;
        }
        if (f < 0.0f) {
            f = 0.0f;
        }
        this.Dirty = false;
        this.Path.clear();
        this.Open = true;
        switch (primPathParams.CurveType & PrimProfileParams.LL_PCODE_HOLE_MASK) {
            case 32:
                int floor = (int) Math.floor(Math.floor((double) ((Math.abs(primPathParams.TwistBegin - primPathParams.TwistEnd) * 3.5f * (f - 0.5f)) + (6.0f * f))) * ((double) primPathParams.Revolutions));
                if (z) {
                    floor = i2;
                }
                genNGon(primPathParams, floor, 0.0f, 1.0f, 1.0f);
                break;
            case 48:
                if (primPathParams.End - primPathParams.Begin >= 0.99f && primPathParams.ScaleX >= 0.99f) {
                    this.Open = false;
                }
                genNGon(primPathParams, (int) Math.floor((double) (6.0f * f)), 0.0f, 1.0f, 1.0f);
                float size = 1.0f / ((float) this.Path.size());
                int i3 = 0;
                float f2 = 0.5f;
                while (true) {
                    int i4 = i3;
                    if (i4 >= this.Path.size()) {
                        break;
                    } else {
                        this.Path.get(i4).pos.x = f2;
                        f2 = f2 == 0.5f ? -0.5f : 0.5f;
                        i3 = i4 + 1;
                    }
                }
            case 64:
                this.Step = 1.0f / ((float) 4);
                this.Path.ensureCapacity(5);
                for (int i5 = 0; i5 < 5; i5++) {
                    float f3 = ((float) i5) * this.Step;
                    PathPoint pathPoint = new PathPoint();
                    pathPoint.pos.set(0.0f, PrimMath.lerp(0.0f, (float) ((-Math.sin((double) (primPathParams.TwistEnd * 3.1415927f * f3))) * 0.5d), f3), PrimMath.lerp(-0.5f, (float) (Math.cos((double) (primPathParams.TwistEnd * 3.1415927f * f3)) * 0.5d), f3));
                    pathPoint.scale.x = PrimMath.lerp(1.0f, primPathParams.ScaleX, f3);
                    pathPoint.scale.y = PrimMath.lerp(1.0f, primPathParams.ScaleY, f3);
                    pathPoint.TexT = f3;
                    pathPoint.rot.setQuat(f3 * primPathParams.TwistEnd * 3.1415927f, 1.0f, 0.0f, 0.0f);
                    this.Path.add(pathPoint);
                }
                break;
            default:
                int floor2 = ((int) Math.floor((double) (Math.abs(primPathParams.TwistBegin - primPathParams.TwistEnd) * 3.5f * (f - 0.5f)))) + 2;
                if (floor2 < i + 2) {
                    floor2 = i + 2;
                }
                this.Step = 1.0f / ((float) (floor2 - 1));
                this.Path.ensureCapacity(floor2);
                LLVector2 beginScale = primPathParams.getBeginScale();
                LLVector2 endScale = primPathParams.getEndScale();
                for (int i6 = 0; i6 < floor2; i6++) {
                    float lerp = PrimMath.lerp(primPathParams.Begin, primPathParams.End, ((float) i6) * this.Step);
                    PathPoint pathPoint2 = new PathPoint();
                    pathPoint2.pos.set(PrimMath.lerp(0.0f, primPathParams.ShearX, lerp), PrimMath.lerp(0.0f, primPathParams.ShearY, lerp), lerp - 0.5f);
                    pathPoint2.rot.setQuat(PrimMath.lerp(primPathParams.TwistBegin * 3.1415927f, primPathParams.TwistEnd * 3.1415927f, lerp), 0.0f, 0.0f, 1.0f);
                    pathPoint2.scale.x = PrimMath.lerp(beginScale.x, endScale.x, lerp);
                    pathPoint2.scale.y = PrimMath.lerp(beginScale.y, endScale.y, lerp);
                    pathPoint2.TexT = lerp;
                    this.Path.add(pathPoint2);
                }
                break;
        }
        if (primPathParams.TwistEnd == primPathParams.TwistBegin) {
            return true;
        }
        this.Open = true;
        return true;
    }
}
