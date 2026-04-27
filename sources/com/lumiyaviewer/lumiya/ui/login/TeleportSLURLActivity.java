// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.GridConnectionService;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.SLURL;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.SLWorldMap;
import com.lumiyaviewer.lumiya.slproto.modules.rlv.RLVController;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.TeleportProgressDialog;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.login:
//            LoginActivity

public class TeleportSLURLActivity extends AppCompatActivity
    implements android.view.View.OnClickListener
{

    private SLURL slurl;

    public TeleportSLURLActivity()
    {
        slurl = null;
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_login_TeleportSLURLActivity_4598(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
        finish();
    }

    public void onClick(View view)
    {
        Object obj = null;
        view.getId();
        JVM INSTR tableswitch 2131755665 2131755666: default 28
    //                   2131755665 29
    //                   2131755666 196;
           goto _L1 _L2 _L3
_L1:
        return;
_L2:
        boolean flag1 = false;
        boolean flag = flag1;
        if (slurl != null)
        {
            view = GridConnectionService.getGridConnection();
            flag = flag1;
            if (view != null)
            {
                java.util.UUID uuid = view.getActiveAgentUUID();
                view = obj;
                if (uuid != null)
                {
                    view = UserManager.getUserManager(uuid);
                }
                flag = flag1;
                if (view != null)
                {
                    SLAgentCircuit slagentcircuit = view.getActiveAgentCircuit();
                    flag = flag1;
                    if (slagentcircuit != null)
                    {
                        flag = flag1;
                        if (slagentcircuit.getModules().worldMap.TeleportToRegionByName(slurl.getLocationName(), slurl.getLocationX(), slurl.getLocationY(), slurl.getLocationZ()))
                        {
                            (new TeleportProgressDialog(this, view, 0x7f090350)).show();
                            flag = true;
                        }
                    }
                }
            }
        }
        if (!flag)
        {
            (new android.support.v7.app.AlertDialog.Builder(this)).setMessage(0x7f09034e).setCancelable(true).setPositiveButton("OK", new _2D_.Lambda.txy91ryZVkviKYu9VXLZHkYSvg0(this)).create().show();
            return;
        }
          goto _L1
_L3:
        finish();
        return;
    }

    public void onCreate(Bundle bundle)
    {
        boolean flag;
        super.onCreate(bundle);
        setContentView(0x7f0400a8);
        try
        {
            slurl = new SLURL(getIntent());
        }
        // Misplaced declaration of an exception variable
        catch (Bundle bundle) { }
        try
        {
            bundle = GridConnectionService.getGridConnection();
        }
        // Misplaced declaration of an exception variable
        catch (Bundle bundle)
        {
            finish();
            return;
        }
        if (bundle == null) goto _L2; else goto _L1
_L1:
        if (bundle.getConnectionState() != com.lumiyaviewer.lumiya.slproto.SLGridConnection.ConnectionState.Connected) goto _L2; else goto _L3
_L3:
        bundle = bundle.getAgentCircuit();
        if (bundle == null) goto _L2; else goto _L4
_L4:
        if (!bundle.getModules().rlvController.canTeleportToLocation())
        {
            finish();
            return;
        }
        flag = true;
_L6:
        if (!flag || slurl == null)
        {
            bundle = new Intent(getIntent());
            bundle.setClass(this, com/lumiyaviewer/lumiya/ui/login/LoginActivity);
            startActivity(bundle);
            finish();
            return;
        } else
        {
            findViewById(0x7f100291).setOnClickListener(this);
            findViewById(0x7f100292).setOnClickListener(this);
            ((TextView)findViewById(0x7f10028f)).setText(slurl.getLocationName());
            ((TextView)findViewById(0x7f100290)).setText(String.format("(%d, %d, %d)", new Object[] {
                Integer.valueOf(slurl.getLocationX()), Integer.valueOf(slurl.getLocationY()), Integer.valueOf(slurl.getLocationZ())
            }));
            return;
        }
_L2:
        flag = false;
        if (true) goto _L6; else goto _L5
_L5:
    }
}
