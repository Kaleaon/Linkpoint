// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.types;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.types:
//            LLVector3, ImmutableVector

public class AgentPosition
{

    private boolean isValid;
    private long lastAgentDataMillis;
    private final Object lock = new Object();
    private final LLVector3 position = new LLVector3();
    private final LLVector3 velocity = new LLVector3();

    public AgentPosition()
    {
        isValid = false;
        lastAgentDataMillis = 0L;
    }

    public ImmutableVector getImmutablePosition()
    {
        ImmutableVector immutablevector = null;
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        if (isValid)
        {
            immutablevector = new ImmutableVector(position);
        }
        obj;
        JVM INSTR monitorexit ;
        return immutablevector;
        Exception exception;
        exception;
        throw exception;
    }

    public boolean getInterpolatedPosition(LLVector3 llvector3)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        if (!isValid)
        {
            break MISSING_BLOCK_LABEL_100;
        }
        if (velocity.x != 0.0F || velocity.y != 0.0F || velocity.z != 0.0F) goto _L2; else goto _L1
_L1:
        llvector3.set(position);
_L3:
        boolean flag = true;
_L4:
        obj;
        JVM INSTR monitorexit ;
        return flag;
_L2:
        llvector3.setMul(velocity, (float)(System.currentTimeMillis() - lastAgentDataMillis) / 1000F);
        llvector3.add(position);
          goto _L3
        llvector3;
        throw llvector3;
        flag = false;
          goto _L4
    }

    public LLVector3 getPosition()
    {
        LLVector3 llvector3 = new LLVector3();
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        if (isValid)
        {
            llvector3.set(position);
        }
        obj;
        JVM INSTR monitorexit ;
        return llvector3;
        Exception exception;
        exception;
        throw exception;
    }

    public boolean getPosition(LLVector3 llvector3)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        if (!isValid) goto _L2; else goto _L1
_L1:
        llvector3.set(position);
        boolean flag = true;
_L4:
        obj;
        JVM INSTR monitorexit ;
        return flag;
_L2:
        flag = false;
        if (true) goto _L4; else goto _L3
_L3:
        llvector3;
        throw llvector3;
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

    public void set(LLVector3 llvector3, LLVector3 llvector3_1)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        position.set(llvector3);
        llvector3 = velocity;
        if (llvector3_1 == null) goto _L2; else goto _L1
_L1:
        llvector3.set(llvector3_1);
        lastAgentDataMillis = System.currentTimeMillis();
        isValid = true;
        obj;
        JVM INSTR monitorexit ;
        return;
_L2:
        llvector3_1 = LLVector3.Zero;
        if (true) goto _L1; else goto _L3
_L3:
        llvector3;
        throw llvector3;
    }
}
