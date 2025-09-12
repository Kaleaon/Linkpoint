// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.voice.common.model;

import android.os.Bundle;

// Referenced classes of package com.lumiyaviewer.lumiya.voice.common.model:
//            Voice3DVector

public class Voice3DPosition
{

    public final Voice3DVector atOrientation;
    public final Voice3DVector leftOrientation;
    public final Voice3DVector position;
    public final Voice3DVector upOrientation;
    public final Voice3DVector velocity;

    public Voice3DPosition(Bundle bundle)
    {
        position = new Voice3DVector(bundle.getBundle("position"));
        velocity = new Voice3DVector(bundle.getBundle("velocity"));
        atOrientation = new Voice3DVector(bundle.getBundle("atOrientation"));
        upOrientation = new Voice3DVector(bundle.getBundle("upOrientation"));
        leftOrientation = new Voice3DVector(bundle.getBundle("leftOrientation"));
    }

    public Voice3DPosition(Voice3DVector voice3dvector, Voice3DVector voice3dvector1, Voice3DVector voice3dvector2, Voice3DVector voice3dvector3, Voice3DVector voice3dvector4)
    {
        position = voice3dvector;
        velocity = voice3dvector1;
        atOrientation = voice3dvector2;
        upOrientation = voice3dvector3;
        leftOrientation = voice3dvector4;
    }

    public Bundle toBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putBundle("position", position.toBundle());
        bundle.putBundle("velocity", velocity.toBundle());
        bundle.putBundle("atOrientation", atOrientation.toBundle());
        bundle.putBundle("upOrientation", upOrientation.toBundle());
        bundle.putBundle("leftOrientation", leftOrientation.toBundle());
        return bundle;
    }

    public String toString()
    {
        return String.format("(pos %s vel %s at %s up %s left %s)", new Object[] {
            position.toString(), velocity.toString(), atOrientation.toString(), upOrientation.toString(), leftOrientation.toString()
        });
    }
}
