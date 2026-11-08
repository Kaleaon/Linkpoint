package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector2;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class PrimPath {
    private static final int MIN_DETAIL_FACES = 6;
    private static float[] tableScale = {1.0f, 1.0f, 1.0f, 0.5f, 0.707107f, 0.53f, 0.525f, 0.5f};
    boolean Open = false;
    int Total = 0;
    boolean Dirty = true;
    float Step = 1.0f;
    ArrayList<PathPoint> Path = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class PathPoint {
        LLVector3 pos = new LLVector3();
        LLVector2 scale = new LLVector2();
        LLQuaternion rot = new LLQuaternion();
        float TexT = 0.0f;
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
        float f15 = (i < 8 ? tableScale[i] : 0.5f) * (1.0f - f12);
        float f16 = primPathParams.RadiusOffset;
        if (f16 < 0.0f) {
            f8 = (f16 + 1.0f) * f15;
        } else {
            float f17 = (1.0f - f16) * f15;
            f8 = f15;
            f15 = f17;
        }
        this.Open = ((primPathParams.End * f2) - primPathParams.Begin < 1.0f || abs > 0.001f || Math.abs(f4 - f5) > 0.001f || Math.abs(f6 - f7) > 0.001f) ? true : Math.abs(f15 - f8) > 0.001f;
        LLQuaternion lLQuaternion = new LLQuaternion();
        LLQuaternion lLQuaternion2 = new LLQuaternion();
        LLVector3 lLVector3 = new LLVector3(1.0f, 0.0f, 0.0f);
        float f18 = primPathParams.TwistBegin * f3;
        float f19 = primPathParams.TwistEnd * f3;
        float f20 = 1.0f / i;
        float f21 = primPathParams.Begin;
        PathPoint pathPoint = new PathPoint();
        float f22 = 6.2831855f * f9 * f21;
        float sin = (float) (Math.sin(f22) * PrimMath.lerp(f8, f15, f21));
        pathPoint.pos.set(PrimMath.lerp(0.0f, primPathParams.ShearX, sin) + 0.0f + (PrimMath.lerp(-f10, f10, f21) * 0.5f), ((float) (Math.cos(f22) * PrimMath.lerp(f8, f15, f21))) + PrimMath.lerp(0.0f, primPathParams.ShearY, sin), sin);
        pathPoint.scale.x = PrimMath.lerp(f5, f4, f21) * f11;
        pathPoint.scale.y = PrimMath.lerp(f7, f6, f21) * f12;
        pathPoint.TexT = f21;
        lLQuaternion.setQuat(((PrimMath.lerp(f18, f19, f21) * 2.0f) * 3.1415927f) - 3.1415927f, 0.0f, 0.0f, 1.0f);
        lLQuaternion2.setQuat(f22, lLVector3);
        pathPoint.rot.setMul(lLQuaternion, lLQuaternion2);
        this.Path.add(pathPoint);
        for (float f23 = ((int) ((f21 + f20) * i)) / i; f23 < primPathParams.End; f23 += f20) {
            PathPoint pathPoint2 = new PathPoint();
            float f24 = 6.2831855f * f9 * f23;
            float cos = (float) (Math.cos(f24) * PrimMath.lerp(f8, f15, f23));
            float sin2 = (float) (Math.sin(f24) * PrimMath.lerp(f8, f15, f23));
            pathPoint2.pos.set(PrimMath.lerp(0.0f, primPathParams.ShearX, sin2) + 0.0f + (PrimMath.lerp(-f10, f10, f23) * 0.5f), cos + PrimMath.lerp(0.0f, primPathParams.ShearY, sin2), sin2);
            pathPoint2.scale.x = PrimMath.lerp(f5, f4, f23) * f11;
            pathPoint2.scale.y = PrimMath.lerp(f7, f6, f23) * f12;
            pathPoint2.TexT = f23;
            lLQuaternion.setQuat(((PrimMath.lerp(f18, f19, f23) * 2.0f) * 3.1415927f) - 3.1415927f, 0.0f, 0.0f, 1.0f);
            lLQuaternion2.setQuat(f24, lLVector3);
            pathPoint2.rot.setMul(lLQuaternion, lLQuaternion2);
            this.Path.add(pathPoint2);
        }
        float f25 = primPathParams.End;
        PathPoint pathPoint3 = new PathPoint();
        float f26 = f9 * 6.2831855f * f25;
        float cos2 = (float) (Math.cos(f26) * PrimMath.lerp(f8, f15, f25));
        float lerp = (float) (PrimMath.lerp(f8, f15, f25) * Math.sin(f26));
        pathPoint3.pos.set((PrimMath.lerp(-f10, f10, f25) * 0.5f) + PrimMath.lerp(0.0f, primPathParams.ShearX, lerp) + 0.0f, cos2 + PrimMath.lerp(0.0f, primPathParams.ShearY, lerp), lerp);
        pathPoint3.scale.x = PrimMath.lerp(f5, f4, f25) * f11;
        pathPoint3.scale.y = PrimMath.lerp(f7, f6, f25) * f12;
        pathPoint3.TexT = f25;
        lLQuaternion.setQuat(((PrimMath.lerp(f18, f19, f25) * 2.0f) * 3.1415927f) - 3.1415927f, 0.0f, 0.0f, 1.0f);
        lLQuaternion2.setQuat(f26, lLVector3);
        pathPoint3.rot.setMul(lLQuaternion, lLQuaternion2);
        this.Path.add(pathPoint3);
        this.Total = this.Path.size();
    }

    public boolean generate(PrimPathParams primPathParams, float f, int i, boolean z, int i2) {
        if (this.Dirty || !(!z)) {
            if (f < 0.0f) {
                f = 0.0f;
            }
            this.Dirty = false;
            this.Path.clear();
            this.Open = true;
            switch (primPathParams.CurveType & PrimProfileParams.LL_PCODE_HOLE_MASK) {
                case 16:
                default:
                    int floor = ((int) Math.floor(Math.abs(primPathParams.TwistBegin - primPathParams.TwistEnd) * 3.5f * (f - 0.5f))) + 2;
                    if (floor < i + 2) {
                        floor = i + 2;
                    }
                    this.Step = 1.0f / (floor - 1);
                    this.Path.ensureCapacity(floor);
                    LLVector2 beginScale = primPathParams.getBeginScale();
                    LLVector2 endScale = primPathParams.getEndScale();
                    for (int i3 = 0; i3 < floor; i3++) {
                        float lerp = PrimMath.lerp(primPathParams.Begin, primPathParams.End, i3 * this.Step);
                        PathPoint pathPoint = new PathPoint();
                        pathPoint.pos.set(PrimMath.lerp(0.0f, primPathParams.ShearX, lerp), PrimMath.lerp(0.0f, primPathParams.ShearY, lerp), lerp - 0.5f);
                        pathPoint.rot.setQuat(PrimMath.lerp(primPathParams.TwistBegin * 3.1415927f, primPathParams.TwistEnd * 3.1415927f, lerp), 0.0f, 0.0f, 1.0f);
                        pathPoint.scale.x = PrimMath.lerp(beginScale.x, endScale.x, lerp);
                        pathPoint.scale.y = PrimMath.lerp(beginScale.y, endScale.y, lerp);
                        pathPoint.TexT = lerp;
                        this.Path.add(pathPoint);
                    }
                    break;
                case 32:
                    int floor2 = (int) Math.floor(Math.floor((Math.abs(primPathParams.TwistBegin - primPathParams.TwistEnd) * 3.5f * (f - 0.5f)) + (6.0f * f)) * primPathParams.Revolutions);
                    if (z) {
                        floor2 = i2;
                    }
                    genNGon(primPathParams, floor2, 0.0f, 1.0f, 1.0f);
                    break;
                case 48:
                    if (primPathParams.End - primPathParams.Begin >= 0.99f && primPathParams.ScaleX >= 0.99f) {
                        this.Open = false;
                    }
                    genNGon(primPathParams, (int) Math.floor(6.0f * f), 0.0f, 1.0f, 1.0f);
                    float size = 1.0f / this.Path.size();
                    int i4 = 0;
                    float f2 = 0.5f;
                    while (true) {
                        int i5 = i4;
                        if (i5 >= this.Path.size()) {
                            break;
                        } else {
                            this.Path.get(i5).pos.x = f2;
                            f2 = f2 == 0.5f ? -0.5f : 0.5f;
                            i4 = i5 + 1;
                        }
                    }
                    break;
                case 64:
                    this.Step = 1.0f / 4;
                    this.Path.ensureCapacity(5);
                    for (int i6 = 0; i6 < 5; i6++) {
                        float f3 = i6 * this.Step;
                        PathPoint pathPoint2 = new PathPoint();
                        pathPoint2.pos.set(0.0f, PrimMath.lerp(0.0f, (float) ((-Math.sin(primPathParams.TwistEnd * 3.1415927f * f3)) * 0.5d), f3), PrimMath.lerp(-0.5f, (float) (Math.cos(primPathParams.TwistEnd * 3.1415927f * f3) * 0.5d), f3));
                        pathPoint2.scale.x = PrimMath.lerp(1.0f, primPathParams.ScaleX, f3);
                        pathPoint2.scale.y = PrimMath.lerp(1.0f, primPathParams.ScaleY, f3);
                        pathPoint2.TexT = f3;
                        pathPoint2.rot.setQuat(f3 * primPathParams.TwistEnd * 3.1415927f, 1.0f, 0.0f, 0.0f);
                        this.Path.add(pathPoint2);
                    }
                    break;
            }
            if (primPathParams.TwistEnd != primPathParams.TwistBegin) {
                this.Open = true;
                return true;
            }
            return true;
        }
        return false;
    }
}
