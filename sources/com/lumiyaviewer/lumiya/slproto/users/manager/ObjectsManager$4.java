// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLParcelInfo;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarControl;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.types.AgentPosition;
import java.util.concurrent.atomic.AtomicReference;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            ObjectsManager, UserManager

class this._cls0
    implements Runnable
{

    final ObjectsManager this$0;

    public void run()
    {
        Object obj = ObjectsManager._2D_get1(ObjectsManager.this);
        obj;
        JVM INSTR monitorenter ;
        com.lumiyaviewer.lumiya.slproto.objects.SLObjectFilterInfo slobjectfilterinfo = ObjectsManager._2D_get0(ObjectsManager.this);
        obj;
        JVM INSTR monitorexit ;
        obj = ObjectsManager._2D_get8(ObjectsManager.this).getActiveAgentCircuit();
        Exception exception;
        SLParcelInfo slparcelinfo;
        if (obj != null)
        {
            obj = ((SLAgentCircuit) (obj)).getModules().avatarControl.getAgentPosition();
        } else
        {
            obj = null;
        }
        if (obj != null)
        {
            obj = ((AgentPosition) (obj)).getImmutablePosition();
        } else
        {
            obj = null;
        }
        slparcelinfo = (SLParcelInfo)ObjectsManager._2D_get5(ObjectsManager.this).get();
        Debug.Printf("ObjectList: updating object list, parcelInfo %s, agentPosVector %s", new Object[] {
            slparcelinfo, obj
        });
        if (slparcelinfo != null && obj != null)
        {
            obj = slparcelinfo.getDisplayObjects(((com.lumiyaviewer.lumiya.slproto.types.ImmutableVector) (obj)), slobjectfilterinfo, ObjectsManager._2D_get2(ObjectsManager.this));
        } else
        {
            obj = new jectDisplayList(ImmutableList.of(), false);
        }
        ObjectsManager._2D_get3(ObjectsManager.this).onResultData(SubscriptionSingleKey.Value, obj);
        return;
        exception;
        throw exception;
    }

    jectDisplayList()
    {
        this$0 = ObjectsManager.this;
        super();
    }
}
