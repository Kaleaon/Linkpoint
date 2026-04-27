// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.events.SLDisconnectEvent;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;

public class LogoutDialog extends ProgressDialog
{

    private static final long DISCONNECT_TIMEOUT = 5000L;
    private UUID agentUUID;
    private final EventBus eventBus;
    private final Handler handler;
    private final Runnable onDisconnectTimeout;

    public LogoutDialog(Context context)
    {
        super(context);
        handler = new Handler();
        eventBus = EventBus.getInstance();
        onDisconnectTimeout = new _2D_.Lambda.Ido4EAnXE9yUsM2nDeFKnyTfU7w(this);
    }

    public LogoutDialog(Context context, int i)
    {
        super(context, i);
        handler = new Handler();
        eventBus = EventBus.getInstance();
        onDisconnectTimeout = new _2D_.Lambda.Ido4EAnXE9yUsM2nDeFKnyTfU7w._cls1(this);
    }

    private UserManager getUserManager()
    {
        if (agentUUID != null)
        {
            return UserManager.getUserManager(agentUUID);
        } else
        {
            return null;
        }
    }

    public static void show(Activity activity)
    {
        UUID uuid = ActivityUtils.getActiveAgentID(activity.getIntent());
        if (uuid != null)
        {
            activity = new LogoutDialog(activity);
            activity.setAgentUUID(uuid);
            activity.show();
        }
    }

    public void handleDisconnectEvent(SLDisconnectEvent sldisconnectevent)
    {
        Debug.Printf("LogoutDialog: disconnect event", new Object[0]);
        dismiss();
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_login_LogoutDialog_3137()
    {
        Object obj;
        boolean flag;
        flag = true;
        obj = getUserManager();
        if (obj == null) goto _L2; else goto _L1
_L1:
        obj = ((UserManager) (obj)).getActiveAgentCircuit();
        if (obj == null) goto _L2; else goto _L3
_L3:
        obj = ((SLAgentCircuit) (obj)).getGridConnection();
        if (obj == null) goto _L2; else goto _L4
_L4:
        ((SLGridConnection) (obj)).forceDisconnect(true);
_L6:
        if (!flag)
        {
            dismiss();
        }
        return;
_L2:
        flag = false;
        if (true) goto _L6; else goto _L5
_L5:
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setProgressStyle(0);
        setMessage(getContext().getString(0x7f090193));
        if (bundle != null)
        {
            agentUUID = UUIDPool.getUUID(bundle.getString("agentUUID"));
        }
    }

    public Bundle onSaveInstanceState()
    {
        Bundle bundle1 = super.onSaveInstanceState();
        Bundle bundle = bundle1;
        if (bundle1 == null)
        {
            bundle = new Bundle();
        }
        if (agentUUID != null)
        {
            bundle.putString("agentUUID", agentUUID.toString());
        }
        return bundle;
    }

    public void onStart()
    {
        boolean flag1 = false;
        super.onStart();
        eventBus.subscribe(this, null, handler);
        Object obj = getUserManager();
        boolean flag = flag1;
        if (obj != null)
        {
            obj = ((UserManager) (obj)).getActiveAgentCircuit();
            flag = flag1;
            if (obj != null)
            {
                obj = ((SLAgentCircuit) (obj)).getGridConnection();
                flag = flag1;
                if (obj != null)
                {
                    Debug.Printf("LogoutDialog: connection is not null", new Object[0]);
                    handler.postDelayed(onDisconnectTimeout, 5000L);
                    ((SLGridConnection) (obj)).Disconnect();
                    flag = true;
                }
            }
        }
        if (!flag)
        {
            dismiss();
            EventBus.getInstance().publish(new SLDisconnectEvent(true, "Disconnected"));
        }
    }

    protected void onStop()
    {
        handler.removeCallbacks(onDisconnectTimeout);
        eventBus.unsubscribe(this);
        super.onStop();
    }

    public void setAgentUUID(UUID uuid)
    {
        agentUUID = uuid;
    }
}
