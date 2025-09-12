// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.events.SLTeleportResultEvent;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.SLWorldMap;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatNewActivity;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.common:
//            ActivityUtils

public class TeleportProgressDialog extends ProgressDialog
    implements android.content.DialogInterface.OnCancelListener
{

    private final Handler mHandler = new Handler();
    private final UserManager userManager;

    public TeleportProgressDialog(Context context, UserManager usermanager, int i)
    {
        super(context);
        userManager = usermanager;
        setMessage(context.getString(i));
        setCancelable(true);
        setIndeterminate(true);
        setOnCancelListener(this);
    }

    public static void TeleportToLandmark(Context context, UserManager usermanager, UUID uuid, boolean flag)
    {
label0:
        {
            if (usermanager != null)
            {
                SLAgentCircuit slagentcircuit = usermanager.getActiveAgentCircuit();
                if (slagentcircuit != null && slagentcircuit.getModules().rlvController.canTeleportToLandmark())
                {
                    usermanager = new _2D_.Lambda._cls8gWLFwrhoxKapEC8iWggaUhFR1c._cls2(slagentcircuit, uuid, context, usermanager);
                    if (!flag)
                    {
                        break label0;
                    }
                    uuid = new android.app.AlertDialog.Builder(context);
                    uuid.setMessage(context.getString(0x7f09033c)).setCancelable(true).setPositiveButton("Yes", new _2D_.Lambda._cls8gWLFwrhoxKapEC8iWggaUhFR1c._cls1(usermanager)).setNegativeButton("No", new _2D_.Lambda._cls8gWLFwrhoxKapEC8iWggaUhFR1c());
                    uuid.create().show();
                }
            }
            return;
        }
        usermanager.run();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_common_TeleportProgressDialog_1322(SLAgentCircuit slagentcircuit, UUID uuid, Context context, UserManager usermanager)
    {
        if (slagentcircuit.getModules().rlvController.canTeleportToLandmark())
        {
            slagentcircuit.TeleportToLandmarkAsset(uuid);
            (new TeleportProgressDialog(context, usermanager, 0x7f090350)).show();
        }
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_common_TeleportProgressDialog_2034(Runnable runnable, DialogInterface dialoginterface, int i)
    {
        dialoginterface.dismiss();
        runnable.run();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_common_TeleportProgressDialog_2260(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    public void handleTeleportResult(SLTeleportResultEvent slteleportresultevent)
    {
        boolean flag = isShowing();
        Debug.Log((new StringBuilder()).append("TeleportResult: success = ").append(slteleportresultevent.success).toString());
        try
        {
            dismiss();
        }
        catch (Exception exception)
        {
            Debug.Warning(exception);
        }
        if (!slteleportresultevent.success)
        {
            if (flag)
            {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                builder.setTitle(getContext().getString(0x7f09033d));
                builder.setMessage(slteleportresultevent.message);
                builder.setCancelable(true);
                builder.create().show();
            }
            return;
        }
        slteleportresultevent = new Intent(getContext(), com/lumiyaviewer/lumiya/ui/chat/ChatNewActivity);
        if (userManager != null)
        {
            ActivityUtils.setActiveAgentID(slteleportresultevent, userManager.getUserID());
        }
        slteleportresultevent.addFlags(0x14008000);
        getContext().startActivity(slteleportresultevent);
    }

    public void onCancel(DialogInterface dialoginterface)
    {
        if (userManager == null)
        {
            break MISSING_BLOCK_LABEL_29;
        }
        try
        {
            dialoginterface = userManager.getActiveAgentCircuit();
        }
        // Misplaced declaration of an exception variable
        catch (DialogInterface dialoginterface)
        {
            Debug.Warning(dialoginterface);
            return;
        }
        if (dialoginterface == null)
        {
            break MISSING_BLOCK_LABEL_29;
        }
        dialoginterface.getModules().worldMap.CancelPendingTeleports();
    }

    public void onStart()
    {
        super.onStart();
        if (userManager != null)
        {
            userManager.getEventBus().subscribe(this, null, mHandler);
        }
    }

    public void onStop()
    {
        if (userManager != null)
        {
            userManager.getEventBus().unsubscribe(this);
        }
        super.onStop();
    }
}
