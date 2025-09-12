// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.objects;

import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.objects:
//            ObjectDerezDialog

static final class val.derezAction
    implements android.content.ickListener
{

    final UUID val$agentID;
    final rezAction val$derezAction;
    final int val$localID;

    public void onClick(DialogInterface dialoginterface, int i)
    {
        dialoginterface.dismiss();
        dialoginterface = UserManager.getUserManager(val$agentID);
        if (dialoginterface != null)
        {
            dialoginterface = dialoginterface.getActiveAgentCircuit();
            if (dialoginterface != null)
            {
                dialoginterface.DerezObject(val$localID, val$derezAction.deRezDestination);
            }
        }
    }

    rezAction(UUID uuid, int i, rezAction rezaction)
    {
        val$agentID = uuid;
        val$localID = i;
        val$derezAction = rezaction;
        super();
    }
}
