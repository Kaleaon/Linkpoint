// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.utils.UUIDPool;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.common:
//            TeleportProgressDialog, ActivityUtils

public class TeleportHomeDialog
{

    public TeleportHomeDialog()
    {
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_common_TeleportHomeDialog_1348(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_common_TeleportHomeDialog_956(Activity activity, UserManager usermanager, SLAgentCircuit slagentcircuit, DialogInterface dialoginterface, int i)
    {
        (new TeleportProgressDialog(activity, usermanager, 0x7f090350)).show();
        slagentcircuit.TeleportToLandmarkAsset(UUIDPool.ZeroUUID);
        dialoginterface.dismiss();
    }

    public static void show(Activity activity)
    {
        UserManager usermanager = ActivityUtils.getUserManager(activity.getIntent());
        if (usermanager != null)
        {
            SLAgentCircuit slagentcircuit = usermanager.getActiveAgentCircuit();
            if (slagentcircuit != null && slagentcircuit.getModules().rlvController.canTeleportToLocation())
            {
                (new android.support.v7.app.AlertDialog.Builder(activity)).setMessage(0x7f09033e).setPositiveButton("Yes", new _2D_.Lambda.sKhJxooMqZpn4u0mFmtSpF7hGx8._cls1(activity, usermanager, slagentcircuit)).setNegativeButton("No", new _2D_.Lambda.sKhJxooMqZpn4u0mFmtSpF7hGx8()).setCancelable(true).create().show();
            }
        }
    }
}
