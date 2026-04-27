// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.rlv;

import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.rlv:
//            RLVController, RLVCommands

public interface RLVCommand
{

    public abstract void Handle(RLVController rlvcontroller, UUID uuid, RLVCommands rlvcommands, String s, String s1);
}
