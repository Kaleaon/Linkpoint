// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.slproto.types.LLVector2;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import java.util.ArrayList;

public class PrimPath
{
    private static final int MIN_DETAIL_FACES = 6;
    private static float[] tableScale;
    boolean Dirty;
    boolean Open;
    ArrayList<PathPoint> Path;
    float Step;
    int Total;
    
    static {
        PrimPath.tableScale = new float[] { 1.0f, 1.0f, 1.0f, 0.5f, 0.707107f, 0.53f, 0.525f, 0.5f };
    }
    
    public PrimPath() {
        this.Open = false;
        this.Total = 0;
        this.Dirty = true;
        this.Step = 1.0f;
        this.Path = new ArrayList<PathPoint>();
    }
    
    private void genNGon(final PrimPathParams primPathParams, final int n, float n2, float end, float n3) {
        final float revolutions = primPathParams.Revolutions;
        final float skew = primPathParams.Skew;
        final float abs = Math.abs(skew);
        final float n4 = primPathParams.ScaleX * (1.0f - abs);
        final float scaleY = primPathParams.ScaleY;
        n2 = 1.0f - primPathParams.TaperX;
        float n5 = 1.0f - primPathParams.TaperY;
        float n6;
        if (n2 > 1.0f) {
            n6 = 1.0f;
            n2 = 2.0f - n2;
        }
        else {
            n6 = n2;
            n2 = 1.0f;
        }
        float n8;
        if (n5 > 1.0f) {
            final float n7 = 1.0f;
            n8 = 2.0f - n5;
            n5 = n7;
        }
        else {
            n8 = 1.0f;
        }
        float n9 = 0.5f;
        if (n < 8) {
            n9 = PrimPath.tableScale[n];
        }
        float n10 = n9 * (1.0f - scaleY);
        final float radiusOffset = primPathParams.RadiusOffset;
        float n11;
        if (radiusOffset < 0.0f) {
            n11 = (radiusOffset + 1.0f) * n10;
        }
        else {
            n11 = n10;
            n10 *= 1.0f - radiusOffset;
        }
        this.Open = (primPathParams.End * end - primPathParams.Begin < 1.0f || abs > 0.001f || Math.abs(n6 - n2) > 0.001f || Math.abs(n5 - n8) > 0.001f || Math.abs(n10 - n11) > 0.001f);
        final LLQuaternion llQuaternion = new LLQuaternion();
        final LLQuaternion llQuaternion2 = new LLQuaternion();
        final LLVector3 llVector3 = new LLVector3(1.0f, 0.0f, 0.0f);
        final float n12 = primPathParams.TwistBegin * n3;
        n3 *= primPathParams.TwistEnd;
        final float n13 = 1.0f / n;
        final float begin = primPathParams.Begin;
        final PathPoint e = new PathPoint();
        end = 6.2831855f * revolutions * begin;
        final float n14 = (float)(Math.sin(end) * PrimMath.lerp(n11, n10, begin));
        e.pos.set(PrimMath.lerp(0.0f, primPathParams.ShearX, n14) + 0.0f + PrimMath.lerp(-skew, skew, begin) * 0.5f, (float)(Math.cos(end) * PrimMath.lerp(n11, n10, begin)) + PrimMath.lerp(0.0f, primPathParams.ShearY, n14), n14);
        e.scale.x = PrimMath.lerp(n2, n6, begin) * n4;
        e.scale.y = PrimMath.lerp(n8, n5, begin) * scaleY;
        e.TexT = begin;
        llQuaternion.setQuat(PrimMath.lerp(n12, n3, begin) * 2.0f * 3.1415927f - 3.1415927f, 0.0f, 0.0f, 1.0f);
        llQuaternion2.setQuat(end, llVector3);
        e.rot.setMul(llQuaternion, llQuaternion2);
        this.Path.add(e);
        PathPoint e2;
        float n15;
        float n16;
        float n17;
        for (end = (int)((begin + n13) * n) / (float)n; end < primPathParams.End; end += n13) {
            e2 = new PathPoint();
            n15 = 6.2831855f * revolutions * end;
            n16 = (float)(Math.cos(n15) * PrimMath.lerp(n11, n10, end));
            n17 = (float)(Math.sin(n15) * PrimMath.lerp(n11, n10, end));
            e2.pos.set(PrimMath.lerp(0.0f, primPathParams.ShearX, n17) + 0.0f + PrimMath.lerp(-skew, skew, end) * 0.5f, n16 + PrimMath.lerp(0.0f, primPathParams.ShearY, n17), n17);
            e2.scale.x = PrimMath.lerp(n2, n6, end) * n4;
            e2.scale.y = PrimMath.lerp(n8, n5, end) * scaleY;
            e2.TexT = end;
            llQuaternion.setQuat(PrimMath.lerp(n12, n3, end) * 2.0f * 3.1415927f - 3.1415927f, 0.0f, 0.0f, 1.0f);
            llQuaternion2.setQuat(n15, llVector3);
            e2.rot.setMul(llQuaternion, llQuaternion2);
            this.Path.add(e2);
        }
        end = primPathParams.End;
        final PathPoint e3 = new PathPoint();
        final float n18 = revolutions * 6.2831855f * end;
        final float n19 = (float)(Math.cos(n18) * PrimMath.lerp(n11, n10, end));
        final float n20 = (float)(PrimMath.lerp(n11, n10, end) * Math.sin(n18));
        e3.pos.set(PrimMath.lerp(-skew, skew, end) * 0.5f + (PrimMath.lerp(0.0f, primPathParams.ShearX, n20) + 0.0f), n19 + PrimMath.lerp(0.0f, primPathParams.ShearY, n20), n20);
        e3.scale.x = PrimMath.lerp(n2, n6, end) * n4;
        e3.scale.y = PrimMath.lerp(n8, n5, end) * scaleY;
        e3.TexT = end;
        llQuaternion.setQuat(PrimMath.lerp(n12, n3, end) * 2.0f * 3.1415927f - 3.1415927f, 0.0f, 0.0f, 1.0f);
        llQuaternion2.setQuat(n18, llVector3);
        e3.rot.setMul(llQuaternion, llQuaternion2);
        this.Path.add(e3);
        this.Total = this.Path.size();
    }
    
    public boolean generate(final PrimPathParams primPathParams, float lerp, int i, final boolean b, int minCapacity) {
        if (!this.Dirty && (b ^ true)) {
            return false;
        }
        float n = lerp;
        if (lerp < 0.0f) {
            n = 0.0f;
        }
        this.Dirty = false;
        this.Path.clear();
        this.Open = true;
        switch (primPathParams.CurveType & 0xF0) {
            default: {
                if ((minCapacity = (int)Math.floor(Math.abs(primPathParams.TwistBegin - primPathParams.TwistEnd) * 3.5f * (n - 0.5f)) + 2) < i + 2) {
                    minCapacity = i + 2;
                }
                this.Step = 1.0f / (minCapacity - 1);
                this.Path.ensureCapacity(minCapacity);
                final LLVector2 beginScale = primPathParams.getBeginScale();
                final LLVector2 endScale = primPathParams.getEndScale();
                PathPoint e;
                for (i = 0; i < minCapacity; ++i) {
                    lerp = PrimMath.lerp(primPathParams.Begin, primPathParams.End, i * this.Step);
                    e = new PathPoint();
                    e.pos.set(PrimMath.lerp(0.0f, primPathParams.ShearX, lerp), PrimMath.lerp(0.0f, primPathParams.ShearY, lerp), lerp - 0.5f);
                    e.rot.setQuat(PrimMath.lerp(primPathParams.TwistBegin * 3.1415927f, primPathParams.TwistEnd * 3.1415927f, lerp), 0.0f, 0.0f, 1.0f);
                    e.scale.x = PrimMath.lerp(beginScale.x, endScale.x, lerp);
                    e.scale.y = PrimMath.lerp(beginScale.y, endScale.y, lerp);
                    e.TexT = lerp;
                    this.Path.add(e);
                }
                break;
            }
            case 32: {
                i = (int)Math.floor(Math.floor(Math.abs(primPathParams.TwistBegin - primPathParams.TwistEnd) * 3.5f * (n - 0.5f) + 6.0f * n) * primPathParams.Revolutions);
                if (b) {
                    i = minCapacity;
                }
                this.genNGon(primPathParams, i, 0.0f, 1.0f, 1.0f);
                break;
            }
            case 48: {
                if (primPathParams.End - primPathParams.Begin >= 0.99f && primPathParams.ScaleX >= 0.99f) {
                    this.Open = false;
                }
                this.genNGon(primPathParams, (int)Math.floor(6.0f * n), 0.0f, 1.0f, 1.0f);
                lerp = 1.0f / this.Path.size();
                lerp = 0.5f;
                for (i = 0; i < this.Path.size(); ++i) {
                    this.Path.get(i).pos.x = lerp;
                    if (lerp == 0.5f) {
                        lerp = -0.5f;
                    }
                    else {
                        lerp = 0.5f;
                    }
                }
                break;
            }
            case 64: {
                this.Step = 1.0f / 4;
                this.Path.ensureCapacity(5);
                PathPoint e2;
                for (i = 0; i < 5; ++i) {
                    lerp = i * this.Step;
                    e2 = new PathPoint();
                    e2.pos.set(0.0f, PrimMath.lerp(0.0f, (float)(-Math.sin(primPathParams.TwistEnd * 3.1415927f * lerp) * 0.5), lerp), PrimMath.lerp(-0.5f, (float)(Math.cos(primPathParams.TwistEnd * 3.1415927f * lerp) * 0.5), lerp));
                    e2.scale.x = PrimMath.lerp(1.0f, primPathParams.ScaleX, lerp);
                    e2.scale.y = PrimMath.lerp(1.0f, primPathParams.ScaleY, lerp);
                    e2.TexT = lerp;
                    e2.rot.setQuat(lerp * (primPathParams.TwistEnd * 3.1415927f), 1.0f, 0.0f, 0.0f);
                    this.Path.add(e2);
                }
                break;
            }
        }
        if (primPathParams.TwistEnd != primPathParams.TwistBegin) {
            this.Open = true;
        }
        return true;
    }
    
    public static class PathPoint
    {
        float TexT;
        LLVector3 pos;
        LLQuaternion rot;
        LLVector2 scale;
        
        public PathPoint() {
            this.pos = new LLVector3();
            this.scale = new LLVector2();
            this.rot = new LLQuaternion();
            this.TexT = 0.0f;
        }
    }
}
